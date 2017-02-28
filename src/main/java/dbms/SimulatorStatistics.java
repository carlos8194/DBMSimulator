package dbms;

import module.Module;
import module.ModuleStatistics;
import query.*;

import java.util.List;

/**
 * This class handles the statistics relating to the whole dbms simulation.
 * The class can be used as the statistics of an individual iteration, creating one each iteration, or it can be used
 * as the global simulation statistics, calculating the average of the variables from  a list of all the iterations
 * statistics.
 * It implements methods for changing variables used at runtime,a method that calculates the final statistics of an
 * iteration, and a method that calculates the final statistics of the whole simulation.
 * The class stores the simulation parameters as well.
 *
 */
public class SimulatorStatistics {
    //General(Internal use)
    private int numberOfSelects;
    private int numberOfUpdates;
    private int numberOfJoins;
    private int numberOfDDls;
    private int totalQueriesProcessed;

    //1.Simulator Parameters.
    private double time;
    private int k;
    private int n;
    private int p;
    private int m;
    private double t;


    //3.Average Queue Size by Module. Contained in moduleStatistics.
    private double[] queueSizes;
    //4.Average Query Lifetime.
    private double averageQueryLifeTime;

    //5.Number of discarted Connections.
    private int discartedConnections;

    //6.Average time spent on each Module by QueryType.
    private double[] averageSelectTimes;
    private double[] averageUpdateTimes;
    private double[] averageJoinTimes;
    private double[] averageDDLTimes;


    //7.Idle time by Module. Contained in ModuleStatistics.
    private double[] idleTimes;

    //8.Module Statistics.
    private ModuleStatistics[] moduleStatistics;

    private int numberOfIterations;
    private List<SimulatorStatistics> statisticsList;

    /**
     * This class constructor is used when the object is intended to hold the dbms statistics of a specific iteration run.
     * It receives an array containing the statistics of each module.
     * @param time
     * @param k
     * @param n
     * @param p
     * @param m
     * @param t
     * @param moduleStatistics each module statistics as an array.
     */
    public SimulatorStatistics(double time, int k, int n, int p, int m, double t, ModuleStatistics[] moduleStatistics){
        this.time = time;
        this.k = k;
        this.n = n;
        this.p = p;
        this.m = m;
        this.t = t;
        queueSizes = new double[5];
        idleTimes = new double[5];
        averageSelectTimes = new double[5];
        averageUpdateTimes = new double[5];
        averageJoinTimes = new double[5];
        averageDDLTimes = new double[5];
        this.moduleStatistics = moduleStatistics;
    }

    /**
     * This class constructor is used when the object is intended to contain the average statistics of the whole simulation.
     * It automatically calls the method for calculating the global statistics.
     * @param time
     * @param k
     * @param n
     * @param p
     * @param m
     * @param t
     * @param statisticsList list of each iteration statistics.
     */
    public SimulatorStatistics(double time, int k, int n, int p, int m, double t, List<SimulatorStatistics> statisticsList){
        this.time = time;
        this.k = k;
        this.n = n;
        this.p = p;
        this.m = m;
        this.t = t;
        queueSizes = new double[5];
        idleTimes = new double[5];
        averageSelectTimes = new double[5];
        averageUpdateTimes = new double[5];
        averageJoinTimes = new double[5];
        averageDDLTimes = new double[5];
        this.statisticsList = statisticsList;
        calculateGlobalStatistics();
    }


    public void calculateFinalStatistics(){
        for (int i = 0; i <5 ; i++) {
            ModuleStatistics statistics = moduleStatistics[i];
            statistics.calculateFinalStatistics(time);
            queueSizes[i] = statistics.getAverageQueueSize();
            idleTimes[i] = statistics.getIdleTime();
        }
        averageQueryLifeTime = averageQueryLifeTime/totalQueriesProcessed;
        for(double time: averageSelectTimes){time = time/numberOfSelects;}
        for(double time: averageDDLTimes){time = time/numberOfDDls;}
        for(double time: averageJoinTimes){time = time/numberOfJoins;}
        for(double time: averageUpdateTimes){time = time/numberOfUpdates;}

    }

    public void calculateGlobalStatistics(){
        numberOfIterations = statisticsList.size();

        //Add all statistics.
        for (SimulatorStatistics statistic: statisticsList) {
            averageQueryLifeTime = averageQueryLifeTime + statistic.getAverageQueryLifeTime();
            discartedConnections = discartedConnections + statistic.getNumberOfDiscartedConnections();
            double [] select = statistic.getAverageTimesByQueryType(QueryType.SELECT);
            double [] update = statistic.getAverageTimesByQueryType(QueryType.UPDATE);
            double [] join = statistic.getAverageTimesByQueryType(QueryType.JOIN);
            double [] ddl = statistic.getAverageTimesByQueryType(QueryType.DDL);

            for (int i = 0; i <5 ; i++) {
                queueSizes[i] += statistic.getAverageQueueSize(i);
                idleTimes[i] += statistic.getModuleIdleTime(i);
                averageSelectTimes[i] += select[i];
                averageUpdateTimes[i] += update[i];
                averageJoinTimes[i] += join[i];
                averageDDLTimes[i] += ddl[i];
            }
        }

        //Divide by number of iterations.
        averageQueryLifeTime /= numberOfIterations;
        discartedConnections /= numberOfIterations;
        for (int i = 0; i <5 ; i++) {
            queueSizes[i] /= numberOfIterations;
            idleTimes[i] /= numberOfIterations;
            averageSelectTimes[i] /= numberOfIterations;
            averageUpdateTimes[i] /= numberOfIterations;
            averageJoinTimes[i] /= numberOfIterations;
            averageDDLTimes[i] /= numberOfIterations;
        }
    }


    //Running Statistics Methods.
    public void incrementDiscartedConnections(){discartedConnections++;}

    public void processQueryReturn(Query query) {
        averageQueryLifeTime += query.getStatistics().getQueryLifeTime();
        totalQueriesProcessed++;
    }
    public void processModuleEnd(Query query, int moduleNumber) {
        QueryType queryType = query.getQueryType();
        switch (queryType){
            case DDL:
                averageDDLTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfDDls++;
                break;
            case JOIN:
                averageJoinTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfJoins++;
                break;
            case SELECT:
                averageSelectTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfSelects++;
                break;
            case UPDATE:
                averageUpdateTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfUpdates++;
                break;
        }
    }


    //Final Statistics for report.
    //3.Average Queue Size.
    public double getAverageQueueSize(int moduleNumber){
        return queueSizes[moduleNumber];
    }
    //4.Average Query Lifetime
    public double getAverageQueryLifeTime(){
        return averageQueryLifeTime;
    }
    //5.Number of discarted connections.
    public int getNumberOfDiscartedConnections(){
        return discartedConnections;
    }
    //6. Average time spent on each module by queryType
    public double[] getAverageTimesByQueryType(QueryType queryType){
        switch (queryType){
            case DDL:
                return averageDDLTimes;
            case JOIN:
                return averageJoinTimes;
            case SELECT:
                return averageSelectTimes;
            case UPDATE:
                return averageUpdateTimes;
        }
        return null;
    }
    //7.Idle time by Module. Contained in ModuleStatistics.
    public double getModuleIdleTime(int moduleNumber){
        return idleTimes[moduleNumber];
    }

    //8.Module Statistics.
    public ModuleStatistics getModuleStatistics(int moduleNumber){
        return moduleStatistics[moduleNumber];
    }


    //GlobalStatistics methods. Methods used when object is used for averaging all iterations statistics.

    /**
     * When used as a global statistics class, returns the average queueSizes of each module as an array.
     * @return queueSizes array.
     */
    public double[] getAverageQueueSizes() {
        return queueSizes;
    }

    /**
     * When used as a global statistics class, returns the average number of queries discarted by the system due to it
     * being fully occupied
     * @return average discartedConnections.
     */
    public double getAverageDiscartedConnections() {
        return discartedConnections;
    }

    /**
     * When used as a global statistics class, returns the average idle times of each module as an array.
     * @return idleTimes array.
     */
    public double[] getAverageIdleTimes() {
        return idleTimes;
    }


    /**
     * When used as a global statistics class, returns the list of statistics considered for the averages.
     * @return statisticsList containing a simulator statistics for each iteration.
     */
    public List<SimulatorStatistics> getStatisticsList(){
        return statisticsList;}



    //Simulation Parameters

    /**
     * Returns the number of times the simulation runs.
     * @return numberOfIterations
     */
    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    /**
     * Returns the amount of time the simulation runs.
     * @return time
     */
    public double getTime() {
        return time;
    }

    /**
     * Returns the number of connections the system can handle as a whole.
     * @return k
     */
    public int getK() {
        return k;
    }

    /**
     * Returns the number of queries the QueryProcessor module can handle at a time.
     * @return n
     */
    public int getN() {
        return n;
    }

    /**
     * Returns the number of queries the TransactionalStorageManager can process at a time.
     * @return
     */
    public int getP() {
        return p;
    }

    /**
     * Returns the number of processes available in the QueryExecutor module.
     * @return m
     */
    public int getM() {
        return m;
    }

    /**
     * Returns the amount of time after which a query is killed.
     * @return
     */
    public double getT() {return t;}




}
