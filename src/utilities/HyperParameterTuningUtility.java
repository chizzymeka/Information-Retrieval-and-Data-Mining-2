package utilities;

import ml.dmlc.xgboost4j.java.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static logistic_regression.Word2VecBuilder.rowLimit;

public class HyperParameterTuningUtility {

    public static void outputHyperParameterOptions() throws XGBoostError {

        /**
         * Random Search Parameter Optimization Methodology
         * This method combines all the specified parameter options from the array and creates unique hyper parameter map from which the best model is derived from.
         */

        HashSet<HashMap<String, Object>> uniqueHyperParameterMaps = new HashSet<>();
        ArrayList<Integer> maximumCapacityMonitorForUniqueHyperParameterMaps = new ArrayList<>();
        HashMap<HashMap<String, Object>, String> hyperParamtersToCVResultMap = new HashMap<>();

        double[] etaArray = {0.05, 0.10, 0.15, 0.20, 0.25, 0.30};  //6 Options
        int[] maxDepthArray = {3, 4, 5, 6, 8, 10, 12, 15}; //8 Options
        String[] objectiveArray = {"rank:pairwise", "rank:ndcg", "rank:map"}; //3 Options

        while (true) {
            double eta = etaArray[ThreadLocalRandom.current().nextInt(0, 5)];
            int maxDepth = maxDepthArray[ThreadLocalRandom.current().nextInt(0, 5)];
            String objective = objectiveArray[ThreadLocalRandom.current().nextInt(0, 2)];

            HashMap<String, Object> hyperParameters = new HashMap<String, Object>() {
                {
                    put("eta", eta);
                    put("max_depth", maxDepth);
                    put("objective", objective);
                    put("eval_metric", "auc");
                }
            };
            uniqueHyperParameterMaps.add(hyperParameters);

            if (!maximumCapacityMonitorForUniqueHyperParameterMaps.contains(uniqueHyperParameterMaps.size())) {
                maximumCapacityMonitorForUniqueHyperParameterMaps.add(uniqueHyperParameterMaps.size());
            } else {
                break; // "All unique combinations of Hyper Parameters attained!
            }
        }

        // Use a unique set of parameters to generate a model on each iteration
        for (HashMap<String, Object> parameters : uniqueHyperParameterMaps) {
            // Training
            String trainDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/train_data.csv" : "output/LM/train_data" + "_" + rowLimit + ".csv";
            String validationDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/validation_data.csv" : "output/LM/validation_data" + "_" + rowLimit + ".csv";
            DMatrix trainDMatrix = new DMatrix(trainDataPath);
            DMatrix validationDMatrix = new DMatrix(validationDataPath);

            // Specify a watch list to see model accuracy on data sets
            HashMap<String, DMatrix> watches = new HashMap<String, DMatrix>() {
                {
                    put("train", trainDMatrix);
                    put("validation", validationDMatrix);
                }
            };

            // Cross Validation
            int nRound = 10;
            IObjective obj = null;
            IEvaluation eval = null;

            int nfold = 3;
            String[] metric = {"auc"};
            String[] crossValidationResults = XGBoost.crossValidation(trainDMatrix, parameters, nRound, nfold, metric, obj, eval);
            hyperParamtersToCVResultMap.put(parameters, Arrays.toString(crossValidationResults));
        }
        int rowCounter = 0;
        for (Map.Entry<HashMap<String, Object>, String> entry : hyperParamtersToCVResultMap.entrySet()) {
            rowCounter++;
            HashMap<String, Object> key = entry.getKey();
            String value = entry.getValue();
            System.out.println(rowCounter + ". " + key + " | " + value);
            System.out.println("\n");
        }
    }
}
