package utils;

/**
 * A class with static methods to randomly generate a value following a specific probability distribution.
 */
public class ProbabilityDistributions {

    /**
     * @param mean: An average value of the distribution.
     * @return A pseudo-random exponential value.
     */
    public static double Exponential(double mean){
        return (-Math.log(Math.random()) / mean);
    }

    /**
     * @param mean: An average value of the distribution.
     * @param standardDeviation: An average value of how much we normally differ from the average.
     * @return A normal pseudo-random value.
     */
    public static double Normal(double mean, double standardDeviation){
        double Z = 0;
        for (int i = 0; i < 12; i++){
            Z += Math.random();
        }
        Z -= 6;
        return mean + standardDeviation*Z;
    }

    /**
     * @param a: the inferior limit.
     * @param b: the superior limit.
     * @return A psudo_random value uniformly distributed between "a" and "b".
     */
    public static double Uniform(double a, double b){
        return a + (b - a)*Math.random();
    }
}
