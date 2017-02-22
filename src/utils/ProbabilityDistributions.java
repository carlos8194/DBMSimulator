package utils;

/**
 * Created by Carlos on 01/02/2017.
 */
public class ProbabilityDistributions {

    public static double Exponential(double mean){
        return (-Math.log(Math.random()) / mean);

    }

    public static double Normal(double mean, double standardDeviation){
        double Z = 0;
        for (int i = 0; i < 12; i++){
            Z += Math.random();
        }
        Z -= 6;
        return mean + standardDeviation*Z;
    }

    public static double Uniform(double a, double b){
        return a + (b - a)*Math.random();
    }
}
