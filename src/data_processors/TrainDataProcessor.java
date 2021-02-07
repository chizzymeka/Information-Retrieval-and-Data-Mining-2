package data_processors;

public class TrainDataProcessor {

//    private static String trainData;
//
//    static {
//        try {
//            trainData = RunIRDM2.loadTrainData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //private static HashMap<TrainDataAttributes.QueryAttributes, HashSet<TrainDataAttributes.PassageAttributes>> queryMap = new HashMap<>();

//    public static void processTrainData () {
//
//        String[] trainDataLine = IRDM2Utils.splitByLine(IRDM2Utils.convertToLowercase(trainData));
//
//        for (int i = 1; i < trainDataLine.length; i++) {
//            String[] trainDataCell = IRDM2Utils.splitByTab(trainDataLine[i]);
//            HashSet<TrainDataAttributes.PassageAttributes> trainDataAttributesHashSet = new HashSet<>();
//            int indexCounter = 0;
//            for (int j = 0; j < trainDataCell.length; j++) {
//                indexCounter++;
//                // Remove punctuations from every other column apart from the decimal point in the relevancy score.
//                if (indexCounter < 5) {
//                    trainDataCell[j] = IRDM2Utils.removePunctuations(trainDataCell[j]);
//                }
//                // Build data map that maps query attributes to list of associated passage attributes.
//                if (indexCounter == 5) {
//                    String qid = trainDataCell[j-4];
//                    String pid = trainDataCell[j-3];
//                    String query = trainDataCell[j-2];
//                    String passage = trainDataCell[j-1];
//                    Double relevanceScore = Double.parseDouble(trainDataCell[j]);
//
//                    TrainDataAttributes trainDataAttributes = new TrainDataAttributes();
//                    TrainDataAttributes.QueryAttributes queryAttributes = trainDataAttributes.new QueryAttributes(qid, query); // qid	queries
//                    TrainDataAttributes.PassageAttributes passageAttributes = trainDataAttributes.new PassageAttributes(pid, passage, relevanceScore); // pid	passage	relevancy
//                    if (!queryMap.containsKey(queryAttributes)) {
//                        trainDataAttributesHashSet.add(passageAttributes);
//                        queryMap.put(queryAttributes, trainDataAttributesHashSet);
//                    } else {
//                        queryMap.get(queryAttributes).add(passageAttributes);
//                    }
//                }
//            }
//        }
//
//        //Text File output.
//        try {
//            Files.write(Paths.get("./output/queryMap.txt"), queryMap.toString().getBytes(), StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static String getTrainData() {
//        return trainData;
//    }

//    public static HashMap<TrainDataAttributes.QueryAttributes, HashSet<TrainDataAttributes.PassageAttributes>> getQueryMap() {
//        return queryMap;
//    }

//    public static LinkedHashMap<TrainDataAttributes.QueryAttributes, HashSet<TrainDataAttributes.PassageAttributes>> buildTrainDataMap() throws IOException {
//
//        LinkedHashMap<TrainDataAttributes.QueryAttributes, HashSet<TrainDataAttributes.PassageAttributes>> queryToPassageMap = new LinkedHashMap<>();
//
//        File train_data = new File("resources/train_data.tsv");
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(train_data));
//        String line;
//
//        int count = 0;
//        while ((line = bufferedReader.readLine()) != null) {
//            count++;
//            if (count > 1) { // Check used for skipping the dataset header row.
//                HashSet<TrainDataAttributes.PassageAttributes> trainDataAttributesHashSet = new HashSet<>();
//                // Text pre-processing.
//                line = IRDM2Utils.convertToLowercase(line);
//                String[] trainDataCell = IRDM2Utils.splitByTab(line);
//                int indexCounter = 0;
//                for (int j = 0; j < trainDataCell.length; j++) {
//                    indexCounter++;
//                    // Remove punctuations from every other column apart from the decimal point in the relevancy score.
//                    if (indexCounter < 5) {
//                        trainDataCell[j] = IRDM2Utils.removePunctuations(trainDataCell[j]); // Some more text pre-processing.
//                    }
//                    if (indexCounter == 5) {
//                        String qid = trainDataCell[j-4];
//                        String pid = trainDataCell[j-3];
//                        String query = trainDataCell[j-2];
//                        String passage = trainDataCell[j-1];
//                        Double relevanceScore = Double.parseDouble(trainDataCell[j]);
//
//                        TrainDataAttributes trainDataAttributes = new TrainDataAttributes();
//                        TrainDataAttributes.QueryAttributes queryAttributes = trainDataAttributes.new QueryAttributes(qid, query); // qid	queries
//                        TrainDataAttributes.PassageAttributes passageAttributes = trainDataAttributes.new PassageAttributes(pid, passage, relevanceScore); // pid	passage	relevancy
//                        if (!queryToPassageMap.containsKey(queryAttributes)) {
//                            trainDataAttributesHashSet.add(passageAttributes);
//                            queryToPassageMap.put(queryAttributes, trainDataAttributesHashSet);
//                        } else {
//                            queryToPassageMap.get(queryAttributes).add(passageAttributes);
//                        }
//                    }
//                }
//            }
//            /**
//             * Used to limit the rows that are processed.
//            if (count == 10000) {
//                break;
//            }
//            */
//            //Text File output.
//            try {
//                Files.write(Paths.get("./output/queryToPassageMap.txt"), queryToPassageMap.toString().getBytes(), StandardOpenOption.CREATE);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return queryToPassageMap;
//    }
}
