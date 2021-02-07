package lambdaMART;

import com.opencsv.exceptions.CsvValidationException;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

import java.io.IOException;
import java.util.HashMap;

import static logistic_regression.Word2VecBuilder.rowLimit;

public class LambdaMART {

    public static void Trainer() throws XGBoostError, IOException, CsvValidationException, InterruptedException {

        // Set parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("eta", 1.0);
                put("max_depth", 2);
                put("objective", "rank:ndcg"); // rank:ndcg
                put("eval_metric", "logloss");
            }
        };

        // Training
        String trainDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/train_data.csv" : "output/LM/train_data" + "_" + rowLimit + ".csv";
        String validationDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/validation_data.csv" : "output/LM/validation_data" + "_" + rowLimit + ".csv";
        DMatrix trainDMatrix = new DMatrix(trainDataPath);
        DMatrix validationDMatrix = new DMatrix(validationDataPath);

        // Specify a watch list to see model accuracy on data sets
        HashMap<String, DMatrix> watches = new HashMap<String, DMatrix>() {
            {
                put("train", trainDMatrix);
                put("test", validationDMatrix);
            }
        };
        int nRound = 2;
        Booster booster = XGBoost.train(trainDMatrix, parameters, nRound, watches, null, null);

        // Save model
        String modelPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/model.bin" : "output/LM/model" + "_" + rowLimit + ".bin";
        booster.saveModel(modelPath);

        // Load Model
        booster = XGBoost.loadModel(modelPath);

        // Testing prediction performance on Validation Data.
        DMatrix dTest = new DMatrix(validationDataPath);

        // predict
        float[][] predictions = booster.predict(dTest);
        PredictedFileCreator.createNewValidationDataFileWithPredictedValues(predictions, rowLimit, validationDataPath);
    }
}