package pl.edu.agh.mownit2.resourceDataPredictionWithDM;


import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.io.PrintWriter;

public class DataPrediction {

    private static final String DATA_TRAINING_DATA_ARFF = "data/trainingData.arff";
    private static final String DATA_DATA_TO_PREDICT_ARFF = "data/dataToPredict.arff";

    public static void main(String[] args) throws Exception {
        new DataPrediction();
    }

    DataPrediction() throws Exception {
        Instances dataToTrain = loadData(DATA_TRAINING_DATA_ARFF);
        RandomForest tree = trainRandomForest(dataToTrain);

        Instances dataToPredict = loadData(DATA_DATA_TO_PREDICT_ARFF);

        predictDataForSensor(tree, dataToPredict);
    }

    private void predictDataForSensor(RandomForest tree, Instances dataToPredict) throws Exception {
        int sensors = dataToPredict.instance(0).attribute(1).numValues();
        PrintWriter[] writers = new PrintWriter[sensors * 2];
        for (int i = 0; i < sensors; i++) {
            writers[2 * i] = new PrintWriter("data/actual" + i + ".data");
            writers[2 * i + 1] = new PrintWriter("data/predicted" + i + ".data");
        }
        for (int k = 0; k < dataToPredict.numInstances(); k++) {
            double actualClass = dataToPredict.instance(k).classValue();
            String actual = dataToPredict.classAttribute().value((int) actualClass);
            Instance newInst = dataToPredict.instance(k);
            int sensor = (int) newInst.value(1);
            double predNB = tree.classifyInstance(newInst);
            String predString = dataToPredict.classAttribute().value((int) predNB);
            writers[sensor * 2].write(String.format("%d %s\n", k, actual));
            writers[sensor * 2 + 1].write(String.format("%d %s\n", k, predString));
        }
        for (int i = 0; i < sensors; i++) {
            writers[2 * i].close();
            writers[2 * i + 1].close();
        }
    }

    private RandomForest trainRandomForest(Instances dataToTrain) throws Exception {
        String[] options1 = new String[14];
        options1[0] = "-P";
        options1[1] = "100";
        options1[2] = "-I";
        options1[3] = "100";
        options1[4] = "-num-slots";
        options1[5] = "1";
        options1[6] = "-K";
        options1[7] = "0";
        options1[8] = "-M";
        options1[9] = "1.0";
        options1[10] = "-V";
        options1[11] = "0.001";
        options1[12] = "-S";
        options1[13] = "-1";
        RandomForest tree = new RandomForest();
        tree.setOptions(options1);
        tree.buildClassifier(dataToTrain);
        return tree;
    }

    private Instances loadData(String file) throws Exception {
        Instances dataToTrain = getData(2, file);
        return transformNumericToNominal(dataToTrain);
    }

    private Instances transformNumericToNominal(Instances data) throws Exception {
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1-2";
        NumericToNominal numericToNominal = new NumericToNominal();
        numericToNominal.setOptions(options);
        numericToNominal.setInputFormat(data);
        return Filter.useFilter(data, numericToNominal);
    }

    private Instances getData(int classIndex, String loc) throws Exception {
        DataSource source = new DataSource(loc);
        Instances data = source.getDataSet();
        data.setClassIndex(classIndex);
        return data;
    }

}
