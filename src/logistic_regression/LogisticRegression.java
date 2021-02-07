package logistic_regression;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

import java.io.File;

import static logistic_regression.Word2VecBuilder.rowLimit;
import static utilities.TestResultGenerator.outputLRTestResult;

public class LogisticRegression {

    public static void Trainer() throws Exception {
        //Reading training CSV file
        String[] options = new String[1];
        options[0] = "-H";

        String filepath = (rowLimit == Integer.MAX_VALUE) ? "output/LR/train_data.csv" : "output/LR/train_data" + "_" + rowLimit + ".csv";
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setOptions(options);
        csvLoader.setSource(new File(filepath));
        Instances train = csvLoader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);

        Logistic logistic = new Logistic();

        //Setting Parameters
        logistic.setRidge(100);

        logistic.buildClassifier(train);

        // Evaluate training data
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(logistic, train);

        System.out.println(evaluation.errorRate()); //Printing Training Mean root squared Error
        System.out.println(evaluation.toSummaryString()); //Summary of Training

        // Save classifier model
        String modelPath = (rowLimit == Integer.MAX_VALUE) ? "output/LR/LogisticModel.model" : "output/LR/LogisticModel" + "_" + rowLimit + ".model";
        logistic.buildClassifier(train);
        weka.core.SerializationHelper.write(modelPath, logistic);

        // Read classifier model
        logistic = (Logistic) weka.core.SerializationHelper.read(modelPath);

        // Testing prediction performance on Validation Data.
        String predictedUnlabelledDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LR/validation_data.csv" : "output/LR/validation_data" + "_" + rowLimit + ".csv";
        CSVLoader validationDataCSVLoader = new CSVLoader();
        validationDataCSVLoader.setOptions(options);
        validationDataCSVLoader.setSource(new File(predictedUnlabelledDataPath));
        Instances dataPredict = validationDataCSVLoader.getDataSet();
        dataPredict.setClassIndex(dataPredict.numAttributes() - 1);
        Instances predictedData = new Instances(dataPredict);

        //  Classify and predict each value
        for (int i = 0; i < dataPredict.numInstances(); i++) {
            double clsLabel = logistic.classifyInstance(dataPredict.instance(i));
            predictedData.instance(i).setClassValue(clsLabel);
        }

        //Storing again as csv
        String predictedDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/LR/validation_data_predicted.csv" : "output/LR/validation_data_predicted" + "_" + rowLimit + ".csv";
        CSVSaver predictedDataCSVSaver = new CSVSaver();
        predictedDataCSVSaver.setFile(new File(predictedDataPath));
        predictedDataCSVSaver.setInstances(predictedData);
        predictedDataCSVSaver.setFieldSeparator(",");
        predictedDataCSVSaver.writeBatch();
        outputLRTestResult();
    }
}
