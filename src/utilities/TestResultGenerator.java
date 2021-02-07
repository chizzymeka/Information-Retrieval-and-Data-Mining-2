package utilities;

import evaluating_retrieval_quality.QualityEvaluator;

import java.io.*;
import java.util.*;

import static logistic_regression.Word2VecBuilder.rowLimit;

public class TestResultGenerator {

    // qid -> [{pid1:score1}, {pid2:score2}, {pid3:score3}]
    static LinkedHashMap<String, List<LinkedHashMap<String, String>>> testResultsMap_ordered = new LinkedHashMap<>();
    static LinkedHashMap<String, List<LinkedHashMap<String, String>>> testResultsMap_unordered = new LinkedHashMap<>();

    public static void outputLRTestResult() throws IOException {
        String predictedFileSource = (rowLimit == Integer.MAX_VALUE) ? "output/LR/validation_data_predicted.csv" : "output/LR/validation_data_predicted" + "_" + rowLimit + ".csv";
        File file = new File(predictedFileSource);
        buildTestResultsMap(file, "LR");
    }

    public static void outputLMTestResult() throws IOException {
        String predictedFileSource = (rowLimit == Integer.MAX_VALUE) ? "output/LM/validation_data_predicted.csv" : "output/LM/validation_data_predicted" + "_" + rowLimit + ".csv";
        File file = new File(predictedFileSource);
        buildTestResultsMap(file, "LM");
    }

    public static void outputNNTestResult() throws IOException {
        String predictedFileSource = (rowLimit == Integer.MAX_VALUE) ? "output/NN/validation_data_predicted.csv" : "output/NN/validation_data_predicted" + "_" + rowLimit + ".csv";
        File file = new File(predictedFileSource);
        buildTestResultsMap(file, "NN");
    }

    public static void buildTestResultsMap(File file, String modelCode) throws IOException {

        // These will be used to evaluate retrieval quality
        LinkedHashMap<String, List<LinkedHashMap<String, String>>> unorderedData = new LinkedHashMap<>();
        LinkedHashMap<String, List<LinkedHashMap<String, String>>> orderedData = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> sizeTracker = new LinkedHashMap<>(); // Used for determining the qid with the highest number of pids, so that we can use it in our NDCG computation.

        BufferedReader br = new BufferedReader(new FileReader(file));

        String currentLine;
        int row = 0;
        while ((currentLine = br.readLine()) != null) {
            row++;
            if (row > 1) {
                LinkedHashMap<String, String> pidAndScoreMap_ordered = new LinkedHashMap<>();
                List<LinkedHashMap<String, String>> pidsAndScoresList_ordered = new ArrayList<>();

                LinkedHashMap<String, String> pidAndScoreMap_unordered = new LinkedHashMap<>();
                List<LinkedHashMap<String, String>> pidsAndScoresList_unordered = new ArrayList<>();

                String[] currentLineArray = currentLine.split(",");
                String qid = currentLineArray[0].replace("\"", "").trim();
                String pid = currentLineArray[1].replace("\"", "").trim();
                String embedding = currentLineArray[2].replace("\"", "").trim();
                String relevancy = currentLineArray[3].replace("\"", "").trim();

                if (!testResultsMap_ordered.containsKey(qid)) {
                    pidAndScoreMap_ordered.put("pid", pid);
                    pidAndScoreMap_ordered.put("relevancy", relevancy);
                    pidsAndScoresList_ordered.add(pidAndScoreMap_ordered);
                    testResultsMap_ordered.put(qid, pidsAndScoresList_ordered);

                    // Quality Evaluation Code
                    pidAndScoreMap_unordered.put("pid", pid);
                    pidAndScoreMap_unordered.put("relevancy", relevancy);
                    pidsAndScoresList_unordered.add(pidAndScoreMap_unordered);
                    testResultsMap_unordered.put(qid, pidsAndScoresList_unordered);
                } else {
                    // Obtain an unordered recommended set from the qid with the largest pids for use in NDCG.* - Quality Evaluation Code.
                    // Note that the code directly below does not include the sorting mechanism.
                    List<LinkedHashMap<String, String>> pidsAndScoresListForCurrentQid_unordered = testResultsMap_unordered.get(qid);
                    pidAndScoreMap_unordered.put("pid", pid);
                    pidAndScoreMap_unordered.put("relevancy", relevancy);
                    pidsAndScoresListForCurrentQid_unordered.add(pidAndScoreMap_unordered);

                    // Obtain an unordered recommendation sets from for use in NDCG. - Quality Evaluation Code.
                    unorderedData.put(qid, pidsAndScoresListForCurrentQid_unordered);

                    // Continue from the 'if' to build testResultMap
                    List<LinkedHashMap<String, String>> pidsAndScoresListForCurrentQid_ordered = testResultsMap_ordered.get(qid); // Map containing pid and score for the subject qid.
                    pidAndScoreMap_ordered.put("pid", pid);
                    pidAndScoreMap_ordered.put("relevancy", relevancy);
                    pidsAndScoresListForCurrentQid_ordered.add(pidAndScoreMap_ordered);
                    pidsAndScoresListForCurrentQid_ordered.sort(Comparator.comparing(m -> m.get("relevancy"), Comparator.nullsLast(Comparator.reverseOrder()))); // Sort by relevancy, that is, score.

                    // Obtain ordered recommendation sets from for use in NDCG. - Quality Evaluation Code
                    orderedData.put(qid, pidsAndScoresListForCurrentQid_ordered);

                    // Map the qids against the number of their associated pids.
                    sizeTracker.put(qid, pidsAndScoresListForCurrentQid_ordered.size());
                }
            }
        }
        br.close();

        // Evaluate Performance on validation data.
        int numberOfQids = unorderedData.size();
        for (Map.Entry<String, List<LinkedHashMap<String, String>>> unordered_entry : unorderedData.entrySet()) {
            String qid = unordered_entry.getKey();
            List<LinkedHashMap<String, String>> unorderedRecommendationSet = unordered_entry.getValue();
            List<LinkedHashMap<String, String>> orderedRecommendationSet = orderedData.get(qid);
            QualityEvaluator.calculateQualityMetrics(modelCode, qid, numberOfQids, unorderedRecommendationSet, orderedRecommendationSet);
        }

        String testResultPath = "output/test_results/" + modelCode + ".txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(testResultPath));

        int rowCounter = 0;
        for (Map.Entry<String, List<LinkedHashMap<String, String>>> entry : testResultsMap_ordered.entrySet()) {
            String qid = entry.getKey();
            String pid;
            String assignment = "A1";
            int rank = 0;
            String score;
            String algoName = modelCode;

            List<LinkedHashMap<String, String>> pidsAndScoresListForCurrentQid = entry.getValue();
            for (int i = 0; i < pidsAndScoresListForCurrentQid.size()-1; i++) {
                rowCounter++;
                rank++;
                if (rowCounter <= 100) {
                    pid = pidsAndScoresListForCurrentQid.get(i).get("pid");
                    score = pidsAndScoresListForCurrentQid.get(i).get("relevancy");
                    String testResultRow = "<" + qid + " " + assignment + " " + pid + " " + rank + " " + score + " " + algoName + ">" + '\n';
                    bw.write(testResultRow);
                }
            }
        }
        bw.write("\n");
        String testResultMessage = (rowLimit == Integer.MAX_VALUE) ? "Test results based on the full dataset." : "Quick test based on " + rowLimit + " rows.";
        bw.write(testResultMessage);
        bw.close();
    }
}
