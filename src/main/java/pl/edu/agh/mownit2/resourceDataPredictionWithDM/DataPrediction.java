package pl.edu.agh.mownit2.resourceDataPredictionWithDM;


import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.util.ArrayList;
import java.util.Random;

public class DataPrediction {
    public static void main(String[] args) throws Exception {
        new DataPrediction();
    }

    public DataPrediction() throws Exception {
        Instances data = getData(0, "data/test.arff");
        Instances newData = transformNumericToNominal(data);

        /*for (int i = 2; i <= 2; i++) {*/

            newData.setClassIndex(2);
            String[] options1 = new String[14];
            //options1[0] = "-U";            // unpruned tree
            options1[0] = "-P";            // unpruned tree
            options1[1] = "100";            // unpruned tree
            options1[2] = "-I";            // unpruned tree
            options1[3] = "100";            // unpruned tree
            options1[4] = "-num-slots";            // unpruned tree
            options1[5] = "1";            // unpruned tree
            options1[6] = "-K";            // unpruned tree
            options1[7] = "0";            // unpruned tree
            options1[8] = "-M";            // unpruned tree
            options1[9] = "1.0";            // unpruned tree
            options1[10] = "-V";            // unpruned tree
            options1[11] = "0.001";            // unpruned tree
            options1[12] = "-S";            // unpruned tree
            options1[13] = "-1";            // unpruned tree
        RandomForest tree = new RandomForest();
            //J48 tree = new J48();         // new instance of tree
            tree.setOptions(options1);     // set the options
            tree.buildClassifier(newData);   // build classifier
            /*Evaluation eval = new Evaluation(newData);
            eval.crossValidateModel(tree, newData, 10, new Random(1));
            */

for(int z = 0; z < newData.numClasses();z++){
    System.out.println("%"+z+" " + newData.classAttribute().value(z));
}

            Instances dataToTest = getData(0, "data/copy.arff");
            Instances newDataToTest = transformNumericToNominal(dataToTest);

            newDataToTest.setClassIndex(2);
            for(int p = 0; p < newDataToTest.numClasses(); p++){
                String classValue = newDataToTest.classAttribute().value(p);
                System.out.println("class value " + p+" "+classValue);
            }
        //newDataToTest.setClass(dataToTest.classAttribute());
            for(int k = 0; k < newDataToTest.numInstances(); k++){
                double actualClass = newDataToTest.instance(k).classValue();
//                System.out.println("classvalue" + (int)actualClass);
                String actual = newDataToTest.classAttribute().value((int) actualClass);
//                System.out.println("actual" + actual);
                Instance newInst = newDataToTest.instance(k);
               // System.out.println("newInst" + newInst+"k"+k);
                double predNB= tree.classifyInstance(newInst);

//                System.out.println("predNB" +predNB);
                String predString= newDataToTest.classAttribute().value((int) predNB);

                System.out.println(actual + "  -  "+predString);
            }

            /*System.out.println(eval.toSummaryString("\nResults\n======\n", false));*/
        /*}*/
    }

    private Instances transformNumericToNominal(Instances data) throws Exception {
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1-2";
        NumericToNominal numericToNominal = new NumericToNominal();
        numericToNominal.setOptions(options);
        numericToNominal.setInputFormat(data);
        return Filter.useFilter(data, numericToNominal);

/*
        String[] options = new String[6];
        options[0] = "-B";
        options[1] = "250";
        options[2] = "-M";
        options[3] = "-1.0";
        options[4] = "-R";
        options[5] = "1-3";
        Discretize discretize = new Discretize();
        discretize.setOptions(options);
        discretize.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
        return Filter.useFilter(data, discretize);
*/
    }

    private Instances getData(int classIndex, String loc) throws Exception {
        DataSource source = new DataSource(loc);
        Instances data = source.getDataSet();
        data.setClassIndex(classIndex);
        return data;
    }

}
