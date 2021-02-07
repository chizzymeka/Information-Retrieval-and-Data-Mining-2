Program Execution Instructions
1.	Ensure that the supplied TSV files are placed in the ‘resources’ folder.
    a.	candidate_passages_top1000.tsv
    b.	passage_collection_new.txt
    c.	test-queries.tsv
    d.	train_data.tsv
    e.	validation_data.tsv
2.	The main method is: main.RunIRDM2.
3.	Set the row limit in the main method: int rowLimit. You can set the limit to ‘Integer.MAX_VALUE’ to use the full datasets.
4.	The model codes are LR, LM and NN and there are two options when it comes to executing the program, which is handled by a Switch statement in the main method. Setting the int option = 0; to ‘1’ allows you to run one specific specified model and setting it to ‘2’ runs the three models in order.
5.	To run any one of the three models, set the option to ‘1’ and specify the model code as the first formal parameter in the following method call in the main method: Word2VecBuilder.runSpecifiedModel("LM", rowLimit); within the Switch statement.
6.	To run all three models in quick succession, just set the option to ‘2’ and the For-Loop in the Switch statement will handle all three executions.
7.	The trained model, the validation data with the predicted values and the CSV data representation files for each executing algorithm will be generated and placed in their respective file output folders. That is:
    a.	output/LR
    b.	output/LM
    c.	output/NN
8.	The file names of the data representation files will be appended with the number of rows specified, unless you use the full dataset.
9.	The Test Results (LR, LM and NN txt files) will be generated and placed in ‘output/test_results’.
10.	The quality metrics (Average Precision and NDCG) will be printed to the console.
11.	To use the Hyperparameter Tuning utility, comment out the code for executing the program and uncomment and run the method call: HyperParameterTuningUtility.outputHyperParameterOptions(); in the main method.
12.	Pay attention to the output in the console to identify the optimal parameter settings based on the AUC values of both the training and validation data.