package pl.edu.agh.mownit2.resourceDataPredictionWithDM;

public class Main {
    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator(15);
        System.out.println(dataGenerator.getData());
    }
}
