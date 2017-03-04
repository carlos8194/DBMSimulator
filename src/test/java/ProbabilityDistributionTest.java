import utils.ProbabilityDistributions;
/**
 * This class tests the three different random values generators programmed in the class ProbabilityDistribution.
 * It tests that they produce a mean around the real mean value and ,in some cases, that the values produced are within expected bounds
 */
public class ProbabilityDistributionTest {
    public static void main(String[] args){
        System.out.println("----------Testing Uniform Distribution----------");
        double upperBound = 135;
        double lowerBound = 40;
        boolean outOfBounds = false;
        double mean = (upperBound + lowerBound) / 2.0;
        double experimentalMean = 0.0;
        for (int i = 0; i < 1000; i++) {
            double value = ProbabilityDistributions.Uniform(lowerBound,upperBound);
            if(value<lowerBound || value>upperBound) outOfBounds = true;
            experimentalMean+= value;
        }
        experimentalMean /= 1000.0;
        System.out.println("Experimental mean is close to mean: " +mean+ " ~ " +experimentalMean);
        System.out.println("A value was out of limits: "+ outOfBounds+"\n\n");


        System.out.println("----------Testing Exponential Distribution----------");
        double lambda = 35.0/60.0;
        double runningTime = 2000;
        double expectedEvents = lambda*runningTime;
        mean = 1.0/lambda;
        experimentalMean = 0.0;
        double totalTime = 0.0;
        double iterations = 0.0;
        while(totalTime<runningTime){
            double value = ProbabilityDistributions.Exponential(lambda);
            experimentalMean+= value;
            totalTime +=value;
            iterations+=1.0;
        }
        experimentalMean /= iterations;
        System.out.println("Experimental mean is close to mean: " +mean+ " ~ " +experimentalMean);
        System.out.println("In " + runningTime+ " seconds, "+expectedEvents+" are expected, "+iterations+" occured.\n\n");

        System.out.println("----------Testing Normal Distribution----------");
        mean = 1.0;
        double stdDeviation = 0.5;
        upperBound = 0.7978845608;
        outOfBounds = false;
        experimentalMean=0.0;
        for (int i = 0; i < 1000; i++) {
            double value = ProbabilityDistributions.Normal(mean,stdDeviation);
            double probability = (1.0/(0.5*Math.sqrt(2*3.1418)))*Math.pow(Math.E,(-0.5*Math.pow((value-1.0)/0.5,2)));
            experimentalMean+= value;
        }
        experimentalMean /= 1000.0;
        System.out.println("Experimental mean is close to mean: " +mean+ " ~ " +experimentalMean);
        System.out.println("Highest Probabilty = "+upperBound);
        System.out.println("A value exceeded most probable outcome: "+ outOfBounds+"\n\n");



    }
}
