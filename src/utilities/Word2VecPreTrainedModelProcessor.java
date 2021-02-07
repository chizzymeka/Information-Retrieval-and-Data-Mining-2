package utilities;

import java.io.*;
import java.util.*;

public class Word2VecPreTrainedModelProcessor {

    static LinkedHashMap<String, List<Double>> wordVectorHashMap = new LinkedHashMap<>();

    public static void transformPreTrainedWord2VecModelToMap() throws IOException {
        File preTrainedWord2vecFile = new File("resources/word2vec.c.output.model.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(preTrainedWord2vecFile));
        LinkedHashMap<String, List<String>> wordVectorHashMap = new LinkedHashMap<>();
        int count = 0;
        String currentLine;
        while ((currentLine = bufferedReader.readLine()) != null) {
            count++;
            if (count < 3) {
                continue;
            }
            /**
             * Only use this condition to limit lines that will be transformed.
             if (count == 10) {
             break;
             }
            */
            String word = null;
            List<String> vectors = new ArrayList<>();
            String[] wordAndVectors = currentLine.split(" ");
            for (int i = 0; i < wordAndVectors.length; i++) {
                if (i == 0) {
                    word = wordAndVectors[i];
                } else {
                    vectors.add(wordAndVectors[i]);
                }
                wordVectorHashMap.put(word, vectors);
            }
        }
        writeWordVectorHashMap(wordVectorHashMap);
    }

    public static void writeWordVectorHashMap(LinkedHashMap<String, List<String>> wordVectorHashMap) throws IOException {
        File preTrainedWord2vecMapFile = new File("output/Word2Vec/preTrainedWord2vecMapFile.txt");
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(preTrainedWord2vecMapFile) );
        for(Map.Entry<String, List<String>> entry : wordVectorHashMap.entrySet()) {
            bufferedWriter.write( entry.getKey() + ":" + entry.getValue() );
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static LinkedHashMap<String, List<Double>> readWordVectorHashMap() throws IOException {
        File preTrainedWord2vecMapFile = new File("output/Word2Vec/preTrainedWord2vecMapFile.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(preTrainedWord2vecMapFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(":");
            String word = parts[0].trim(); // Map key - Word.
            String[] vectors = parts[1].trim().replace("[", "").replace("]", "").split(", "); // Map value - Vector.
            List<Double> vectorsToDoubleList = new ArrayList<>();
            if ((!word.equals("")) && (vectors.length > 0)) {
                for (String vector : vectors) {
                    vectorsToDoubleList.add(Double.parseDouble(vector));
                }
                wordVectorHashMap.put(word, vectorsToDoubleList);
            }
        }
        bufferedReader.close();
        return wordVectorHashMap;
    }

    public static LinkedHashMap<String, List<Double>> getWordVectorHashMap() {
        return wordVectorHashMap;
    }
}
