package logistic_regression;

import com.opencsv.CSVWriter;
import lambdaMART.LambdaMART;
import neural_network.NeuralNetwork;
import utilities.IRDM2Utils;
import utilities.Word2VecPreTrainedModelProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static utilities.IRDM2Utils.getCurrentTimeStamp;

public class Word2VecBuilder {

    private static String specifiedAlgorithmName;
    private static LinkedHashMap<String, List<Double>> preTrainedWord2VecLinkedHashMap = null;
    public static int rowLimit;
    public static LinkedHashMap<Integer, List<String>> rowNumbersQidsAndPidsMap = new LinkedHashMap<>();
    public static boolean buildValidationDataEmbeddingsForLM = false;

    public static void runSpecifiedModel(String algorithmName, int limit) throws Exception {

        specifiedAlgorithmName = algorithmName;
        rowLimit = limit;

        switch (specifiedAlgorithmName) {
            case "LR":
                Word2VecBuilder.buildTrainDataEmbeddings();
                Word2VecBuilder.buildValidationDataEmbeddings();
                Word2VecBuilder.buildCandidatePassagesEmbeddings();
                LogisticRegression.Trainer();
                break;
            case "LM":
                Word2VecBuilder.buildTrainDataEmbeddings();
                Word2VecBuilder.buildValidationDataEmbeddings();
                Word2VecBuilder.buildCandidatePassagesEmbeddings();
                LambdaMART.Trainer();
                break;
            case "NN":
                Word2VecBuilder.buildTrainDataEmbeddings();
                Word2VecBuilder.buildValidationDataEmbeddings();
                Word2VecBuilder.buildCandidatePassagesEmbeddings();
                NeuralNetwork.Trainer();
                break;
        }
    }

    public static void buildTrainDataEmbeddings() throws IOException, InterruptedException {
        String description = "Training Dataset";
        String datasetFilePath = "resources/train_data.tsv";
        String csvOutputFilePath = (rowLimit == Integer.MAX_VALUE) ? "output/" + specifiedAlgorithmName + "/train_data.csv" : "output/" + specifiedAlgorithmName + "/train_data" + "_" + rowLimit + ".csv";
        boolean hasRelevancyColumn = true;
        buildEmbeddings(description, datasetFilePath, csvOutputFilePath, hasRelevancyColumn, rowLimit);
    }

    public static void buildValidationDataEmbeddings() throws IOException, InterruptedException {
        String description = "Validation Dataset";
        String datasetFilePath = "resources/validation_data.tsv";
        String csvOutputFilePath = (rowLimit == Integer.MAX_VALUE) ? "output/" + specifiedAlgorithmName + "/validation_data.csv" : "output/" + specifiedAlgorithmName + "/validation_data" + "_" + rowLimit + ".csv";
        boolean hasRelevancyColumn = true;
        buildValidationDataEmbeddingsForLM = true;
        buildEmbeddings(description, datasetFilePath, csvOutputFilePath, hasRelevancyColumn, rowLimit);
    }

    public static void buildCandidatePassagesEmbeddings() throws IOException, InterruptedException {
        String description = "Candidate Passages";
        String datasetFilePath = "resources/candidate_passages_top1000.tsv";
        String csvOutputFilePath = (rowLimit == Integer.MAX_VALUE) ? "output/" + specifiedAlgorithmName + "/candidate_passages_top1000.csv" : "output/" + specifiedAlgorithmName + "/candidate_passages_top1000" + "_" + rowLimit + ".csv";
        boolean hasRelevancyColumn = false;
        buildEmbeddings(description, datasetFilePath, csvOutputFilePath, hasRelevancyColumn, rowLimit);
    }

    public static void buildEmbeddings(String description, String datasetFilePath, String csvOutputFilePath, boolean hasRelevancyColumn, int rowLimit) throws IOException, InterruptedException {

        preTrainedWord2VecLinkedHashMap = Word2VecPreTrainedModelProcessor.readWordVectorHashMap();
        System.out.println("Word embedding construction started for " + description + " at: " + getCurrentTimeStamp());
        File dataFile = new File(datasetFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile));
        String currentLine;

        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvOutputFilePath));
        String[] csvArray;

        int count = 0;
        int indexCheck = (hasRelevancyColumn) ? 5 : 4;

        switch (specifiedAlgorithmName) {
            case "LR":
                prepareDataForLRandNN(csvWriter, bufferedReader, indexCheck, hasRelevancyColumn, count);
                break;
            case "LM":

                csvArray = new String[2];
                /**
                csvArray[0] = "relevancy";
                csvArray[1] = "embedding"; // Labelled query-plus-passage average embedding
                csvWriter.writeNext(csvArray);
                 */

                while ((currentLine = bufferedReader.readLine()) != null) {
                    count++;
                    if (count > 1) { // Check used for skipping the dataset header row.
                        // Text pre-processing.
                        currentLine = IRDM2Utils.convertToLowercase(currentLine);
                        String[] dataCell = IRDM2Utils.splitByTab(currentLine);
                        int indexCounter = 0;
                        for (int j = 0; j < dataCell.length; j++) {
                            indexCounter++;
                            // Remove punctuations from every other column apart from the decimal point in the relevancy score.
                            if (indexCounter < 5) {
                                dataCell[j] = IRDM2Utils.removePunctuations(dataCell[j]); // Some more text pre-processing.
                            }
                            if (indexCounter == indexCheck) {
                                String qid = (hasRelevancyColumn) ? dataCell[j-4] : dataCell[j-3];
                                String pid = (hasRelevancyColumn) ? dataCell[j-3] : dataCell[j-2];

                                //*********************************************************************************************************************************************************************************************************
                                // Collect extra qids and pids for building validation data file with predicted values for the LambdaMartModel.
                                String rowNumbersQidsAndPidsMapFilePath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/rowNumbersQidsAndPidsMap.txt" : "output/LM/rowNumbersQidsAndPidsMap" + "_" + rowLimit + ".txt";
                                File rowNumbersQidsAndPidsMapFile = new File(rowNumbersQidsAndPidsMapFilePath);

                                if ((buildValidationDataEmbeddingsForLM) && (!rowNumbersQidsAndPidsMapFile.isFile())) {
                                    List<String> qidsAndPids = new ArrayList<>();
                                    int rowNumber = count;
                                    rowNumber--;  // One has been subtracted to account for the fact that the libsvm file for XGBoost (LambdaMART) cannot contain a header at the time it is processed in the 'LambdaMART.java' class.
                                    if (!rowNumbersQidsAndPidsMap.containsKey(rowNumber)) {
                                        qidsAndPids.add(qid);
                                        qidsAndPids.add(pid);
                                        rowNumbersQidsAndPidsMap.put(rowNumber, qidsAndPids);
                                    }
                                }
                                //*********************************************************************************************************************************************************************************************************

                                String query = (hasRelevancyColumn) ? dataCell[j-2] : dataCell[j-1];
                                String passage = (hasRelevancyColumn) ? dataCell[j-1] : dataCell[j];
                                String relevanceScore = (hasRelevancyColumn) ? dataCell[j] : "NA";
                                String queryPlusPassageAverageLabelledEmbedding = "";

                                LinkedHashMap<String, List<Double>> queryVectorsLinkedHashMap = getWordVectors(query.split(" "));
                                LinkedHashMap<String, List<Double>> passageVectorsLinkedHashMap = getWordVectors(passage.split(" "));
                                List<Double> queryVectorsAverage = calculateVectorAverage(queryVectorsLinkedHashMap);
                                List<Double> passageVectorsAverage = calculateVectorAverage(passageVectorsLinkedHashMap);

                                int vectorLabelGenerator = 0;
                                for (Double value : queryVectorsAverage) {
                                    vectorLabelGenerator++;
                                    String vectorLabel = String.valueOf(vectorLabelGenerator);
                                    queryPlusPassageAverageLabelledEmbedding += vectorLabel + ":" + value.toString();
                                    queryPlusPassageAverageLabelledEmbedding += " ";
                                }
                                int lastElementDetector = 0;
                                for (Double value : passageVectorsAverage) {
                                    vectorLabelGenerator++;
                                    String vectorLabel = String.valueOf(vectorLabelGenerator);
                                    queryPlusPassageAverageLabelledEmbedding += vectorLabel + ":" + value.toString();
                                    if (lastElementDetector++ != passageVectorsAverage.size() - 1) {
                                        queryPlusPassageAverageLabelledEmbedding += " ";
                                    }
                                }
                                csvArray[0] = relevanceScore;
                                csvArray[1] = queryPlusPassageAverageLabelledEmbedding;
                                csvWriter.writeNext(csvArray);
                            }
                        }
                    }
                    if (count == (rowLimit + 1)) {
                        break;
                    }
                }

                break;
            case "NN":
                prepareDataForLRandNN(csvWriter, bufferedReader, indexCheck, hasRelevancyColumn, count);
                break; // End case
        }
        csvWriter.close();
        System.out.println("Word embedding construction completed for " + description + " at: " + getCurrentTimeStamp());
    }

    public static void prepareDataForLRandNN(CSVWriter csvWriter, BufferedReader bufferedReader, int indexCheck, boolean hasRelevancyColumn, int count) throws IOException {
        String[] csvArray = new String[4];
        csvArray[0] = "qid";
        csvArray[1] = "pid";
        csvArray[2] = "embedding"; // query-plus-passage average embedding
        csvArray[3] = "relevancy";
        csvWriter.writeNext(csvArray);

        String currentLine;
        while ((currentLine = bufferedReader.readLine()) != null) {
            count++;
            if (count > 1) { // Check used for skipping the dataset header row.
                // Text pre-processing.
                currentLine = IRDM2Utils.convertToLowercase(currentLine);
                String[] dataCell = IRDM2Utils.splitByTab(currentLine);
                int indexCounter = 0;
                for (int j = 0; j < dataCell.length; j++) {
                    indexCounter++;
                    // Remove punctuations from every other column apart from the decimal point in the relevancy score.
                    if (indexCounter < 5) {
                        dataCell[j] = IRDM2Utils.removePunctuations(dataCell[j]); // Some more text pre-processing.
                    }
                    if (indexCounter == indexCheck) {
                        String qid = (hasRelevancyColumn) ? dataCell[j-4] : dataCell[j-3];
                        String pid = (hasRelevancyColumn) ? dataCell[j-3] : dataCell[j-2];
                        String query = (hasRelevancyColumn) ? dataCell[j-2] : dataCell[j-1];
                        String passage = (hasRelevancyColumn) ? dataCell[j-1] : dataCell[j];
                        String relevanceScore = (hasRelevancyColumn) ? dataCell[j] : "?";
                        String queryPlusPassageAverageEmbedding = "";

                        LinkedHashMap<String, List<Double>> queryVectorsLinkedHashMap = getWordVectors(query.split(" "));
                        LinkedHashMap<String, List<Double>> passageVectorsLinkedHashMap = getWordVectors(passage.split(" "));
                        List<Double> queryVectorsAverage = calculateVectorAverage(queryVectorsLinkedHashMap);
                        List<Double> passageVectorsAverage = calculateVectorAverage(passageVectorsLinkedHashMap);

                        for (Double value : queryVectorsAverage) {
                            queryPlusPassageAverageEmbedding += value.toString();
                            queryPlusPassageAverageEmbedding += " ";
                        }
                        int lastElementDetector = 0;
                        for (Double value : passageVectorsAverage) {
                            queryPlusPassageAverageEmbedding += value.toString();
                            if (lastElementDetector++ != passageVectorsAverage.size() - 1) {
                                queryPlusPassageAverageEmbedding += " ";
                            }
                        }
                        csvArray[0] = qid;
                        csvArray[1] = pid;
                        csvArray[2] = queryPlusPassageAverageEmbedding;
                        csvArray[3] = relevanceScore;
                        csvWriter.writeNext(csvArray);
                    }
                }
            }
            if (count == (rowLimit + 1)) {
                break;
            }
        }
    }

    public static LinkedHashMap<String, List<Double>> getWordVectors(String[] words) throws IOException {
        LinkedHashMap<String, List<Double>> wordVectorsLinkedHashMap = new LinkedHashMap<>();
        List<Double> vectors;
        for (String word : words) {
            word.trim();
            if (preTrainedWord2VecLinkedHashMap.containsKey(word)) {
                vectors = preTrainedWord2VecLinkedHashMap.get(word);
                wordVectorsLinkedHashMap.put(word, vectors);
            } else {
                vectors = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    vectors.add(0.0);
                }
                wordVectorsLinkedHashMap.put(word, vectors);
            }
        }
        return wordVectorsLinkedHashMap;
    }

    public static List<Double> calculateVectorAverage(LinkedHashMap<String, List<Double>> vectorsLinkedHashMap) {
        List<Double> averageVectors = new ArrayList<>();
        List<Double> correspondingIndexList = new ArrayList<>();
        double total = 0.0;
        int numberOfVectorLists = 0;
        for (int i = 0; i < 10; i++) { // Iterate for the same number of times as the vector dimensions, that is, 10.
            for (Map.Entry<String, List<Double>> entry : vectorsLinkedHashMap.entrySet()) {
                //String word = entry.getKey();
                numberOfVectorLists = vectorsLinkedHashMap.size();
                List<Double> vectors = entry.getValue();
                correspondingIndexList.add(vectors.get(i));
                continue;
            }
            for (Double value: correspondingIndexList) {
                total += value;
            }
            averageVectors.add(total/numberOfVectorLists);
        }
        return averageVectors;
    }

    public static int getRowLimit() {
        return rowLimit;
    }
}