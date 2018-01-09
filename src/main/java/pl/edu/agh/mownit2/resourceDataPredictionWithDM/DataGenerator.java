package pl.edu.agh.mownit2.resourceDataPredictionWithDM;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private ArrayList<Attribute> atts;
    private double[] vals;
    private File file;
    private Instances data;
    private int numOfSensors;

    private List<String> getNominalListOf(int limit) {
        List<String> nominals = new LinkedList<>();
        for (int i = 0; i < limit; i++) {
            DecimalFormat df = new DecimalFormat("#");
            nominals.add(df.format(i));
        }
        return nominals;
    }

    public static void main(String[] args) {
        new DataGenerator(2);
    }

    DataGenerator(int numOfSensors) {
        this.numOfSensors = numOfSensors;
        // set up attributes
        List<String> p = new LinkedList<>();
        p.add("0");
        atts = new ArrayList<Attribute>();
        atts.add(new Attribute("dateAttr", getNominalListOf(1440)));
        atts.add(new Attribute("userAttr", getNominalListOf(numOfSensors)));
        atts.add(new Attribute("cpuUsage", getNominalListOf(100)));

        // create Instance Object
        data = new Instances("SampleRelation", atts, 0);
        generateSetOfData();

        file = new File("./data/trainingData.arff");
        saveDataIntoARFFFile();

        data = new Instances("SampleRelation", atts, 0);
        generateSetOfData();

        file = new File("./data/dataToPredict.arff");
        saveDataIntoARFFFile();
    }

    private void generateSetOfData() {
        //generate function of daily usage of resources by user
        int[] func = polynomialFunc();
        double maxVal = findMaxForFunc(func);

        //kValues are coefficients of scalability
        double kValueForCPU = ThreadLocalRandom.current().nextDouble();

        for (int i = 0; i < 1440; i++) {
            for (int j = 0; j < numOfSensors; j++) {
                vals = new double[data.numAttributes()];
                vals[0] = (double) i;
                vals[1] = (double) j;
                vals[2] = calculateMappedVal(func, i + j * 20, maxVal, kValueForCPU) * 100;
                data.add(new DenseInstance(1.0, vals));
            }
        }
    }

    private int[] polynomialFunc() {
        int[] borderValues = {360, 720, 960, 1320};
        int[] borderUserValues = new int[4];
        for (int i = 0; i < 4; i++) {
            borderUserValues[i] = ThreadLocalRandom.current().nextInt(borderValues[i] - 60, borderValues[i] + 60 + 1);
        }
        return borderUserValues;
    }

    private double findMaxForFunc(int[] array) {
        double max = 0;
        for (int i = array[0]; i < array[3]; i++) {
            double val = calculateVal(array, i, 1);
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    private double calculateVal(int[] array, int i, double k) {
        return -1 * k * (i - array[0]) * (i - array[1]) * (i - array[2]) * (i - array[3]);
    }

    private double calculateMappedVal(int[] array, int i, double max, double k) {
        return (calculateVal(array, i, k)) > 0 ?
                (calculateVal(array, i, k) / max) : 0;
    }

    private void saveDataIntoARFFFile() {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        try {
            saver.setFile(file);
            saver.writeBatch();
        } catch (IOException e) {
            System.out.println("Couldn't write to file");
        }
    }


    public File getFile() {
        return file;
    }

    public Instances getData() {
        return data;
    }
}
