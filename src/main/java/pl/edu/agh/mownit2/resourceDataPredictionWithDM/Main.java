package pl.edu.agh.mownit2.resourceDataPredictionWithDM;

public class Main {
    public static void main(String[] args) throws Exception {
        DataGenerator dataGenerator = new DataGenerator(2);
        DataPrediction dataPrediction = new DataPrediction();
        DataVisualisation.main(null);
    }

}
