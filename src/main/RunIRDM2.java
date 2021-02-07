package main;

import logistic_regression.Word2VecBuilder;

import java.util.ArrayList;
import java.util.List;

public class RunIRDM2 {

    public static void main(String[] args) throws Exception {

        int rowLimit = 1000;  // Change to Integer.MAX_VALUE to use the full dataset.
        int option = 2;

        switch (option) {
            case 1:
                // Option 1 - Singular Execution: Specify model to run using either of the three values: 'LR', 'LM' or 'NN'.
                Word2VecBuilder.runSpecifiedModel("LM", rowLimit);
                break;
            case 2:
                // Option 2 - Complete Execution: Run for-loop to execute all three models.
                List<String> modelCodes = new ArrayList<>(3);
                modelCodes.add("LR");
                modelCodes.add("LM");
                modelCodes.add("NN");
                for (String modelCode : modelCodes) {
                    Word2VecBuilder.runSpecifiedModel(modelCode, rowLimit);
                }
                break;
        }

        /**
         * Use the method below to assist with hyper parameter tuning.
         * HyperParameterTuningUtility.outputHyperParameterOptions();
         */
    }
}