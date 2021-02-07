package lambdaMART;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import logistic_regression.Word2VecBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import static utilities.TestResultGenerator.outputLMTestResult;

public class PredictedFileCreator {

    public static void createNewValidationDataFileWithPredictedValues(float[][] predictions, int rowLimit, String validationDataPath) throws IOException, CsvValidationException, InterruptedException {

        LinkedHashMap<Integer, List<String>> qidsAndPidsDetailsMap = Word2VecBuilder.rowNumbersQidsAndPidsMap;

        // Set up new csv file path for the predicted validation data representation file
        String validationDataPredictedPath = (rowLimit == Integer.MAX_VALUE) ? "output/LM/validation_data_predicted.csv" : "output/LM/validation_data_predicted" + "_" + rowLimit + ".csv";
        CSVWriter csvWriter = new CSVWriter(new FileWriter(validationDataPredictedPath));
        String[] csvArray = new String[4];
        csvArray[0] = "qid";
        csvArray[1] = "pid";
        csvArray[2] = "embedding"; // query-plus-passage average embedding
        csvArray[3] = "relevancy";
        csvWriter.writeNext(csvArray);

        // Read the existing validation data representation file
        Reader reader = Files.newBufferedReader(Paths.get(validationDataPath));
        CSVReader csvReader = new CSVReader(reader);
        String[] currentLine;
        for (int i = 0; i < predictions.length; i++) { // Number of Predictions is predictions.length which is equal to the number of rows
            float[] pred = predictions[i];
            for (int j = 0; j < pred.length; j++) {
                if ((currentLine = csvReader.readNext()) != null) {
                    int row = i+1;
                    String qid = qidsAndPidsDetailsMap.get(row).get(0);
                    String pid = qidsAndPidsDetailsMap.get(row).get(1);
                    csvArray[0] = qid;
                    csvArray[1] = pid;
                    csvArray[2] = currentLine[1]; // Embedding

                    // Predicted Relevancy
                    String predictedRelevancy = String.valueOf(pred[j]); // Replace the first column with the new predicted value
                    currentLine[0] = predictedRelevancy;
                    csvArray[3] = currentLine[0];

                    csvWriter.writeNext(csvArray);
                }
            }
        }
        csvWriter.close();
        outputLMTestResult();
    }
}
