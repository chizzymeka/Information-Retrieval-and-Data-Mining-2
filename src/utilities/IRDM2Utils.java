package utilities;

import opennlp.tools.stemmer.PorterStemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.*;

public class IRDM2Utils {

    public static Timestamp getCurrentTimeStamp() {
        Date date= new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        return timestamp;
    }

    public static String convertToLowercase(String data) {
        data = data.toLowerCase();
        //System.out.println("Lower Case Conversion completed at: " + getCurrentTimeStamp());

        return data;
    }

    public static StringBuilder convertStringBuilderToLowercase(StringBuilder data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < builder.length(); i++) {
            if (Character.isUpperCase(builder.charAt(i))) {
                builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));
            }
        }
        //System.out.println("Lower Case Conversion completed at: " + getCurrentTimeStamp());
        return data;
    }

    public static String removePunctuations(String data) {
        data = data.replaceAll("\\p{P}", " ");
        //System.out.println("Punctuation Removal completed at: " + getCurrentTimeStamp());

        return data;
    }

    public static String splitByWhitespace(String data) {
        String[] dataArray = data.split("\\s+");
        //System.out.println("Whitespace Splitting completed at: " + getCurrentTimeStamp());

        return dataArray.toString();
    }

    public static String[] splitByTab(String data) {
        String[] dataArray = data.split("\t");
        //System.out.println("Tab Splitting completed at: " + getCurrentTimeStamp());

        return dataArray;
    }

    public static String[] splitStringBuilderByTab(StringBuilder data) {
        String[] dataArray = data.toString().split("\t");
        //System.out.println("Tab Splitting completed at: " + getCurrentTimeStamp());

        return dataArray;
    }

    public static String[] splitByLine(String data) {
        String[] dataArray = data.split("\n");
        //System.out.println("Line Splitting completed at: " + getCurrentTimeStamp());

        return dataArray;
    }

    public static String tokenizeByTabAndWhitespace(String data) {
        ArrayList<String> dataList = new ArrayList<>();
        String[] dataArray;
        String delimiters = "\n\t";
        StringTokenizer stringTokenizer = new StringTokenizer(data,delimiters);
        int counter = 0;
        while (stringTokenizer.hasMoreTokens()) {
            counter++;
            dataList.add(stringTokenizer.nextToken());
        }
        dataArray = dataList.toArray(new String[counter]);
        //System.out.println("Tab and Whitespace Tokenization completed at: " + getCurrentTimeStamp());

        return dataArray.toString();
    }

    public static String stemData(String[] dataArray) {
        ArrayList<String> dataList = new ArrayList<>(Arrays.asList(dataArray));
        PorterStemmer porterStemmer = new PorterStemmer();
        ArrayList<String> stemmedDataList = new ArrayList<>();
        for (String word : dataList) {
            String stem = porterStemmer.stem(word);
            stemmedDataList.add(stem);
        }
        dataArray = stemmedDataList.stream().toArray(String[]::new);
        //System.out.println("Stemming completed at: " + getCurrentTimeStamp());

        return dataArray.toString();
    }

    public static HashMap<String, Integer> calculateTermFrequency(String[] dataArray) {
        HashMap<String, Integer> termFrequencyMap = new HashMap<>();
        ArrayList<String> dataList = new ArrayList<>(Arrays.asList(dataArray));
        for (String word : dataList) {
            if (termFrequencyMap.containsKey(word)) {
                int value = termFrequencyMap.get(word);
                value++;
                termFrequencyMap.put(word, value);
            } else {
                termFrequencyMap.put(word, 1);
            }
        }
        //System.out.println("Term Frequency Calculation completed at: " + getCurrentTimeStamp());

        return termFrequencyMap;
    }

    public static HashMap<String, Integer> removeStopwords(HashMap<String, Integer> dataMap) {
        text_statistics.StopwordsManager stopwordsManager = new text_statistics.StopwordsManager();
        String[] stopwords = stopwordsManager.getStopWords();
        ArrayList<String> stopwordsList = new ArrayList<>(Arrays.asList(stopwords));
        Iterator<Map.Entry<String, Integer>> iterator = dataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (stopwordsList.contains(entry.getKey())) {
                iterator.remove();
            }
        }
        //System.out.println("Stopword Removal completed at: " + getCurrentTimeStamp());

        return dataMap;
    }

    public static void writeTermFrequencyFile(HashMap<String, Integer> map) throws IOException {
        String mapContent = "";
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            mapContent += entry.getKey() + " = " + entry.getValue() + "\n";
        }
        Files.write(Paths.get("./output/term_frequencies.txt"), mapContent.getBytes(), StandardOpenOption.CREATE);
        //System.out.println("Term Frequency File created at: " + getCurrentTimeStamp());
    }
}
