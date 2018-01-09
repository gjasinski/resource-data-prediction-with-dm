package pl.edu.agh.mownit2.resourceDataPredictionWithDM;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DataVisualisation {
    public static void main(String[] args) throws Exception {
        createSctiptForSensor(1);
    }

    private static void createSctiptForSensor(int sensor) throws FileNotFoundException {
        PrintWriter gnuplotWriter = new PrintWriter("data/gnuplot.data");
        String gnuplotData = "set term png\n" +
                "set xlabel \"time in min\"\n" +
                "set ylabel \"CPU usage %\"\n" +
                "set output \"sensor" + sensor + ".png\"\n" +
                "plot \"actual" + sensor + ".data\" title \"actual\", \"predicted" + sensor + ".data\" title \"predicted\"";
        gnuplotWriter.write(gnuplotData);
        gnuplotWriter.close();

        PrintWriter scriptWriter = new PrintWriter("data/script.sh");
        String script = "gnuplot gnuplot.data";
        scriptWriter.write(script);
        scriptWriter.close();
    }
}
