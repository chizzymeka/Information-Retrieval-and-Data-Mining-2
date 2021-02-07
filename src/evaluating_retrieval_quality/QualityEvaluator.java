package evaluating_retrieval_quality;

import java.math.BigDecimal;
import java.util.*;

public class QualityEvaluator {

    static int qidCounter = 0;

    public static void calculateQualityMetrics(String modelCode, String qid, int numberOfQids, List<LinkedHashMap<String, String>> unorderedRecommendationSet, List<LinkedHashMap<String, String>> orderedRecommendationSet) {

        qidCounter++;

        // AP
        double averagePrecision = calculateAveragePrecision(unorderedRecommendationSet, orderedRecommendationSet);
        //System.out.println(modelCode + ": The Average Precision is : " + BigDecimal.valueOf(averagePrecision).setScale(2, BigDecimal.ROUND_HALF_UP));

        // mAP
        double averagePrecisionAggregate = 0.0;
        averagePrecisionAggregate += averagePrecision;
        // Detect last iteration and calculate the mAP.
        if (qidCounter == numberOfQids) {
            double mAP = averagePrecisionAggregate/numberOfQids;
            System.out.println(modelCode + ": The Mean Average Precision is : " + BigDecimal.valueOf(mAP).setScale(5, BigDecimal.ROUND_HALF_UP));
        }

        // CG
        double unorderedCG = calculateCG(unorderedRecommendationSet);
        double orderedCG = calculateCG(orderedRecommendationSet);

        // DCG
        double unorderedDCG = calculateDCG(unorderedRecommendationSet);
        double orderedDCG = calculateDCG(orderedRecommendationSet);

        // NDCG
        double ndcg = calculateNDCG(unorderedDCG, orderedDCG);
        // Mean NDCG
        double ndcgAggregate = 0.0;
        ndcgAggregate +=  ndcg;
        // Detect last iteration and calculate the Mean NDCG.
        if (qidCounter == numberOfQids) {
            double meanNDCG = ndcgAggregate/numberOfQids;
            System.out.println(modelCode + ": The Mean NDCG is : " + BigDecimal.valueOf(meanNDCG).setScale(5, BigDecimal.ROUND_HALF_UP));
        }
    }

    public static double calculateAveragePrecision(List<LinkedHashMap<String, String>> unorderedRecommendationSet, List<LinkedHashMap<String, String>> orderedRecommendationSet) {

        /**
         * For more information on the theory behind this implementation, please refer to this link: https://towardsdatascience.com/breaking-down-mean-average-precision-map-ae462f623a52
         */

        HashMap<Double, Double> truePositivesTracker = new HashMap<>();
        double numberOfRelevantHits = 0.0;
        double averagePrecisionAggregate = 0.0;
        double divisor = 0.0;

        for (int i = 0; i < unorderedRecommendationSet.size(); i++) {
            double k = i+1;
            if (unorderedRecommendationSet.get(i).equals(orderedRecommendationSet.get(i))) {
                numberOfRelevantHits++;
                truePositivesTracker.put(k, numberOfRelevantHits);
            } else {
                truePositivesTracker.put(k, 0.0);
            }
            double truePositives = truePositivesTracker.get(k); // numberOfRelevantHits becomes truePositives
            double predictedPositives = i+1;
            double averagePrecisionAtK = truePositives/predictedPositives; // AP@k

            /**
             * Test Printout
             * System.out.println("truePositives: " + truePositives + " " + "predictedPositives: " + predictedPositives  + " " + "averagePrecisionAtK: " + averagePrecisionAtK);
             */

            if (averagePrecisionAtK != 0) {
                averagePrecisionAggregate += averagePrecisionAtK;
                divisor++;
            }
        }

        double averagePrecision = ((averagePrecisionAggregate > 0.0) && (divisor > 0.0)) ? averagePrecisionAggregate/divisor : 0.0;

        return averagePrecision;
    }

    public static double calculateCG(List<LinkedHashMap<String, String>> recommendationSet) {

        double cg = 0.0;

        for (LinkedHashMap<String, String> recommendation : recommendationSet) {
            String pid = recommendation.get("pid");
            String relevance = recommendation.get("relevancy");
            cg += Double.valueOf(relevance);
        }

        return cg;
    }

    public static double calculateDCG(List<LinkedHashMap<String, String>> recommendationSet) {

        double dcg = 0.0;
        ArrayList<Double> relevanceToLogOfRankRatioList = new ArrayList<>();

        int rank = 0;
        for (LinkedHashMap<String, String> recommendation : recommendationSet) {
            rank++;
            String pid = recommendation.get("pid");
            String relevance = recommendation.get("relevancy");
            relevanceToLogOfRankRatioList.add(Double.valueOf(relevance)/Math.log(rank + 1));
        }

        for (double value : relevanceToLogOfRankRatioList) {
            dcg += value;
        }

        return dcg;
    }

    public static double calculateNDCG(double unorderedDCG, double orderedDCG) {
        System.out.println("unorderedDCG: " + unorderedDCG + " " + "orderedDCG: " + orderedDCG);
        return unorderedDCG/orderedDCG;
    }
}
