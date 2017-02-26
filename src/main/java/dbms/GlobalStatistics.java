package dbms;

import query.QueryType;

import java.util.List;

/**
 * Created by Rodrigo on 2/25/2017.
 */
public class GlobalStatistics {
    //Simulator Parameters.
    private double time;
    private int k;
    private int n;
    private int p;
    private int m;
    private double t;

    //1.Average Queue Size by Module.
    private double[] averageQueueSizes;

    //2.Average Query Lifetime.
    private double averageQueryLifeTime;

    //3.Number of discarted Connections.
    private double averageDiscartedConnections;

    //4.Average time spent on each Module by QueryType.
    private double[] averageSelectTimes;
    private double[] averageUpdateTimes;
    private double[] averageJoinTimes;
    private double[] averageDDLTimes;


    //5.Idle time by Module. Contained in ModuleStatistics.
    private double[] averageIdleTimes;

    public GlobalStatistics(double time, int k, int n, int p, int m, double t){
        this.time = time;
        this.k = k;
        this.n = n;
        this.p = p;
        this.m = m;
        this.t = t;
        averageQueueSizes = new double[5];
        averageIdleTimes = new double[5];

        averageSelectTimes = new double[5];
        averageUpdateTimes = new double[5];
        averageJoinTimes = new double[5];
        averageDDLTimes = new double[5];
    }

    public void calculateGlobalStatistics(List<SimulatorStatistics> statisticsList){
        int numberOfIterations = statisticsList.size();
        //Add all statistics.
        for (SimulatorStatistics statistic: statisticsList) {
            averageQueryLifeTime = getAverageQueryLifeTime() + statistic.getAverageQueryLifeTime();
            averageDiscartedConnections = getAverageDiscartedConnections() + statistic.getNumberOfDiscartedConnections();

            double[] selectTimes = statistic.getAverageTimesByQueryType(QueryType.SELECT);
            double[] updateTimes = statistic.getAverageTimesByQueryType(QueryType.UPDATE);
            double[] joinTimes = statistic.getAverageTimesByQueryType(QueryType.JOIN);
            double[] DDLTimes = statistic.getAverageTimesByQueryType(QueryType.DDL);
            for (int i = 0; i <5 ; i++) {
                getAverageQueueSizes()[i] += statistic.getAverageQueueSize(i);
                getAverageIdleTimes()[i] += statistic.getModuleIdleTime(i);
                getAverageSelectTimes()[i] += selectTimes[i];
                getAverageUpdateTimes()[i] += updateTimes[i];
                getAverageJoinTimes()[i] += joinTimes[i];
                getAverageDDLTimes()[i] += DDLTimes[i];
            }
        }

        //Divide by number of iterations.
        averageQueryLifeTime /= numberOfIterations;
        averageDiscartedConnections /= numberOfIterations;
        for (int i = 0; i <5 ; i++) {
            getAverageQueueSizes()[i] /= numberOfIterations;
            getAverageIdleTimes()[i] /= numberOfIterations;
            getAverageSelectTimes()[i] /= numberOfIterations;
            getAverageUpdateTimes()[i] /= numberOfIterations;
            getAverageJoinTimes()[i] /= numberOfIterations;
            getAverageDDLTimes()[i] /= numberOfIterations;
        }
    }

    public double getTime() {
        return time;
    }

    public int getK() {
        return k;
    }

    public int getN() {
        return n;
    }

    public int getP() {
        return p;
    }

    public int getM() {
        return m;
    }

    public double getT() {
        return t;
    }

    public double[] getAverageQueueSizes() {
        return averageQueueSizes;
    }

    public double getAverageQueryLifeTime() {
        return averageQueryLifeTime;
    }

    public double getAverageDiscartedConnections() {
        return averageDiscartedConnections;
    }

    public double[] getAverageSelectTimes() {
        return averageSelectTimes;
    }

    public double[] getAverageUpdateTimes() {
        return averageUpdateTimes;
    }

    public double[] getAverageJoinTimes() {
        return averageJoinTimes;
    }

    public double[] getAverageDDLTimes() {
        return averageDDLTimes;
    }

    public double[] getAverageIdleTimes() {
        return averageIdleTimes;
    }
}
