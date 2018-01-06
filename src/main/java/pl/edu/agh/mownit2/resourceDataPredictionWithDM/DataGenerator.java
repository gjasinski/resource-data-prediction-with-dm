package pl.edu.agh.mownit2.resourceDataPredictionWithDM;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private ArrayList<Attribute>    atts;
    double[]            vals;
    private File file;
    private Instances data;

    public DataGenerator(int numOfDays){
        // set up attributes
        atts = new ArrayList<Attribute>();
        atts.add(new Attribute("dateAttr"));
        atts.add(new Attribute("cpuUsage"));
        atts.add(new Attribute("diskWrite"));
        atts.add(new Attribute("diskRead"));
        // create Instance Object
        data = new Instances("SampleRelation", atts, 0);
        for(int i = 0; i < numOfDays; i++) {
            generateSetOfData();
        }

        file = new File("./data/test.arff");
        saveDataIntoARFFFile();
    }

    private void generateSetOfData(){
        //generate function of daily usage of resources by user
        int[] func = polynomialFunc();
        double maxVal = findMaxForFunc(func);

        //kValues are coefficients of scalability
        double kValueForCPU = ThreadLocalRandom.current().nextDouble();
        double kValueForDataRead = ThreadLocalRandom.current().nextDouble();
        double kValueForDataWrite = ThreadLocalRandom.current().nextDouble();

        //max Values in Mb/s
        int maxReadValue = 550;
        int maxWriteValue = 320;

        for(int i = 0; i < 1440; i++) {
            vals = new double[data.numAttributes()];
            vals[0] = (double) i;
            vals[1] = calculateMappedVal(func, i, maxVal, kValueForCPU)*100;
            vals[2] = calculateMappedVal(func, i, maxVal, kValueForDataRead)*maxReadValue;
            vals[3] = calculateMappedVal(func, i, maxVal, kValueForDataWrite)*maxWriteValue;
            data.add(new DenseInstance(1.0, vals));
        }
    }

    private int[] polynomialFunc(){
        int[] borderValues = {360, 720, 960, 1320};
        int[] borderUserValues = new int[4];
        for(int i = 0; i < 4; i++){
            borderUserValues[i] = ThreadLocalRandom.current().nextInt(borderValues[i]-60, borderValues[i]+60 + 1);
        }
        return borderUserValues;
    }

    private double findMaxForFunc(int[] array){
        double max = 0;
        for(int i = array[0]; i < array[3]; i++){
            double val = calculateVal(array, i, 1);
            if(val > max){
                max = val;
            }
        }
        return max;
    }

    private double calculateVal(int[] array, int i, int k){
        return -1 * k * (i-array[0]) * (i-array[1]) * (i-array[2]) * (i-array[3]);
    }

    private double calculateMappedVal(int[] array, int i, double max, double k){
        return (-1 * k * (i-array[0]) * (i-array[1]) * (i-array[2]) * (i-array[3])) > 0 ?
                (-1 * k * (i-array[0]) * (i-array[1]) * (i-array[2]) * (i-array[3])/max) : 0;
    }

    private void saveDataIntoARFFFile(){
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
