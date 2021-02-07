package neural_network;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

import java.io.File;

import static logistic_regression.Word2VecBuilder.rowLimit;

import static utilities.TestResultGenerator.outputNNTestResult;

public class NeuralNetwork {

    //Neural classifier
    public static void Trainer() throws Exception {
        //Reading training CSV file
        String[] options = new String[1];
        options[0] = "-H";

        String filepath = (rowLimit == Integer.MAX_VALUE) ? "output/NN/train_data.csv" : "output/NN/train_data" + "_" + rowLimit + ".csv";
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setOptions(options);
        csvLoader.setSource(new File(filepath));
        Instances train = csvLoader.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);

        //Instance of NN
        MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();

        //Setting Parameters
        multilayerPerceptron.setLearningRate(0.1);
        multilayerPerceptron.setMomentum(0.2);
        multilayerPerceptron.setTrainingTime(2000);
        multilayerPerceptron.setHiddenLayers("1");

        multilayerPerceptron.buildClassifier(train);

        // Evaluate training data
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(multilayerPerceptron, train);

        System.out.println(evaluation.errorRate()); //Printing Training Mean root squared Error
        System.out.println(evaluation.toSummaryString()); //Summary of Training

        // Save classifier model
        String modelPath = (rowLimit == Integer.MAX_VALUE) ? "output/NN/MultilayerPerceptronModel.model" : "output/NN/MultilayerPerceptronModel" + "_" + rowLimit + ".model";
        multilayerPerceptron.buildClassifier(train);
        weka.core.SerializationHelper.write(modelPath, multilayerPerceptron);

        // Read classifier model
        multilayerPerceptron = (MultilayerPerceptron) weka.core.SerializationHelper.read(modelPath);

        // Testing prediction performance on Validation Data.
        String predictedUnlabelledDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/NN/validation_data.csv" : "output/NN/validation_data" + "_" + rowLimit + ".csv";
        CSVLoader validationDataCSVLoader = new CSVLoader();
        validationDataCSVLoader.setOptions(options);
        validationDataCSVLoader.setSource(new File(predictedUnlabelledDataPath));
        Instances dataPredict = validationDataCSVLoader.getDataSet();
        dataPredict.setClassIndex(dataPredict.numAttributes() - 1);
        Instances predictedData = new Instances(dataPredict);

        //  Classify and predict each value
        for (int i = 0; i < dataPredict.numInstances(); i++) {
            double clsLabel = multilayerPerceptron.classifyInstance(dataPredict.instance(i));
            predictedData.instance(i).setClassValue(clsLabel);
        }

        //Storing again as csv
        String predictedDataPath = (rowLimit == Integer.MAX_VALUE) ? "output/NN/validation_data_predicted.csv" : "output/NN/validation_data_predicted" + "_" + rowLimit + ".csv";
        CSVSaver predictedDataCSVSaver = new CSVSaver();
        predictedDataCSVSaver.setFile(new File(predictedDataPath));
        predictedDataCSVSaver.setInstances(predictedData);
        predictedDataCSVSaver.setFieldSeparator(",");
        predictedDataCSVSaver.writeBatch();
        outputNNTestResult();
    }
}
