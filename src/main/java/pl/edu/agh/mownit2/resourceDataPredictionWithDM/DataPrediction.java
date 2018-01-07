package pl.edu.agh.mownit2.resourceDataPredictionWithDM;


import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
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
        Instances data = getData(0);

        Instances newData = transformNumericToNominal(data);

        for (int i = 2; i < 5; i++) {
            newData.setClassIndex(i);
            String[] options1 = new String[1];
            options1[0] = "-U";            // unpruned tree
            J48 tree = new J48();         // new instance of tree
            tree.setOptions(options1);     // set the options
            tree.buildClassifier(newData);   // build classifier

            Evaluation eval = new Evaluation(newData);
            eval.crossValidateModel(tree, newData, 10, new Random(1));
            Instances dates = getDates(0);
            //eval.evaluateModel(tree, dates);
            //System.out.println(tree.);
            //System.out.println(eval.toSummaryString("Res", false));
            /*System.out.println(eval.evaluateModel(tree,));*/
            //eval.evaluateModel(cls, test);
            ArrayList<Prediction> predictions = eval.predictions();
            for (int j = 0; j < predictions.size(); j++) {
                System.out.println(j + " " + predictions.get(j).predicted());
            }

            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        }
    }

    private Instances transformNumericToNominal(Instances data) throws Exception {
        /*String[] options = new String[2];
        options[0] = "-R";
        options[1] = "2-4";
        NumericToNominal numericToNominal = new NumericToNominal();
        numericToNominal.setOptions(options);
        numericToNominal.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
        return Filter.useFilter(data, numericToNominal);*/
        String[] options = new String[6];
        options[0] = "-B";
        options[1] = "250";
        options[2] = "-M";
        options[3] = "-1.0";
        options[4] = "-R";
        options[5] = "3-5";
        //options[3] = "-precision 2";
        Discretize discretize = new Discretize();
        discretize.setOptions(options);
        discretize.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
        return Filter.useFilter(data, discretize);
    }

    private Instances getData(int classIndex) throws Exception {
        DataSource source = new DataSource("data/test.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(classIndex);
        return data;
    }

    private Instances getDates(int classIndex) throws Exception {
        DataSource source = new DataSource("data/dates.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(classIndex);
        return data;
    }
}
