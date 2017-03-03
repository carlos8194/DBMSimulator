package dbms;

import module.ModuleStatistics;
import query.*;

import java.util.List;

/**
 * This class handles the statistics related to the whole dbms simulation.
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
    private double averageQueriesProcessed;

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
    private double averageDiscartedConnections;

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
     * @param time: total running time per iteration.
     * @param k: Servers in the Client Administrator module.
     * @param n: Servers in the Query Processor module.
     * @param p: Servers in the Transactional Storage Manager module.
     * @param m: Servers in the Query Executor module.
     * @param t: Time out per query.
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
     * @param time: total running time per iteration.
     * @param k: Servers in the Client Administrator module.
     * @param n: Servers in the Query Processor module.
     * @param p: Servers in the Transactional Storage Manager module.
     * @param m: Servers in the Query Executor module.
     * @param t: Time out per query.
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


    //Running Statistics Methods.
    /**
     * Running time statistic increments the number of discarted connections by one
     */
    public void incrementDiscartedConnections(){discartedConnections++;}

    /**
     * Running time statistic, processes a query return by adding its lifetime to the average and increasing the amount
     * of queries processed by the system
     * @param query query that just returned.
     */
    public void processQueryReturn(Query query) {
        averageQueryLifeTime += query.getStatistics().getQueryLifeTime();
        totalQueriesProcessed = totalQueriesProcessed + 1;
    }

    /**
     * Running time statistic, processes a query module end by adding the time spent at this module to the average and
     * increasing the amount of queries processed of its specific QueryType.
     * of queries processed by the system
     * @param query query that exited the module
     * @param moduleNumber module identifier
     */
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
    /**
     * When used as an individual iteration statistics class, this method does some final calculation on each varaiable
     * so that the final statistic is ready to be presented by the get methods. It also calls the calculateFinalStatistic
     * method of each module, so that every moduleStatistic is ready to be presented as well.
     */
    public void calculateFinalStatistics(){
        for (int i = 0; i <5 ; i++) {
            ModuleStatistics statistics = moduleStatistics[i];
            statistics.calculateFinalStatistics(time);
            queueSizes[i] = statistics.getAverageQueueSize();
            idleTimes[i] = statistics.getIdleTime();
        }
        averageQueryLifeTime = averageQueryLifeTime/ getTotalQueriesProcessed();
        for(double time: averageSelectTimes){time = time/numberOfSelects;}
        for(double time: averageDDLTimes){time = time/numberOfDDls;}
        for(double time: averageJoinTimes){time = time/numberOfJoins;}
        for(double time: averageUpdateTimes){time = time/numberOfUpdates;}

    }

    //3.Average Queue Size.
    /**
     * When used as an individual iteration statistics class, returns the averageSize of the queue of the module specified
     * by the moduleNumber parameter, in this iteration.
     * @param moduleNumber module identifier.
     * @return module's average queueSize.
     */
    public double getAverageQueueSize(int moduleNumber){
        return queueSizes[moduleNumber];
    }

    //4.Average Query Lifetime

    /**
     * When used as an individual iteration statistics class, returns the average lifetime of a query. Does not take
     * into account queries killed by timeouts.
     * When used as a global statistics class, returns the same as an average of all iterations.
     * @return averageQueryLifeTime.
     */
    public double getAverageQueryLifeTime(){
        return averageQueryLifeTime;
    }

    //5.Number of discarted connections.
    /**
     * When used as an individual iteration statistics class, returns the number of connections discarted by the system
     * due to full capacity this iteration.
     * @return discartedConnections
     */
    public int getNumberOfDiscartedConnections(){
        return discartedConnections;
    }

    //6. Average time spent on each module by queryType
    /**
     * When used as an individual iteration statistics class, returns the average amount of time an specific QueryType
     * spent in each module as an array.
     * When used as a global statistics class, returns the same as an average of all iterations.
     * @param queryType
     * @return average time spent in each module by a QueryType as an array.
     */
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
    /**
     * When used as an individual iteration statistics class, returns the amount of time the module specified by the
     * moduleNumber parameter was idle.
     * @param moduleNumber module identifier.
     * @return idleTime amount of time the module was idle.
     */
    public double getModuleIdleTime(int moduleNumber){
        return idleTimes[moduleNumber];
    }

    //8.Module Statistics.
    /**
     * When used as an individual iteration statistics class, returns the module's statistic specified by the
     * moduleNumber parameter
     * @param moduleNumber module identifier.
     * @return ModuleStatistics
     */
    public ModuleStatistics getModuleStatistics(int moduleNumber){
        return moduleStatistics[moduleNumber];
    }

    /**
     * When used as an individual iteration statistics class, returns the total amount of queries that successfully
     * exited the system during this iteration.
     * @return totalQueriesProcessed
     */
    public int getTotalQueriesProcessed() {
        return totalQueriesProcessed;
    }



    //GlobalStatistics methods. Methods used when object is used for averaging all iterations statistics.
    /**
     * When the class is used as a global statistics class, this method calculates the average of all the statistics
     * contained in the statisticsList class field.
     * It does so by adding each statistic to create a total, and then dividing each variable by the number of iterations.
     */
    public void calculateGlobalStatistics(){
        numberOfIterations = statisticsList.size();

        //Add all statistics.
        for (SimulatorStatistics statistic: statisticsList) {
            averageQueryLifeTime = averageQueryLifeTime + statistic.getAverageQueryLifeTime();
            discartedConnections = discartedConnections + statistic.getNumberOfDiscartedConnections();
            totalQueriesProcessed = totalQueriesProcessed + statistic.getTotalQueriesProcessed();
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
        averageDiscartedConnections = discartedConnections / numberOfIterations;
        averageQueriesProcessed = totalQueriesProcessed / numberOfIterations;
        for (int i = 0; i <5 ; i++) {
            queueSizes[i] /= numberOfIterations;
            idleTimes[i] /= numberOfIterations;
            averageSelectTimes[i] /= numberOfIterations;
            averageUpdateTimes[i] /= numberOfIterations;
            averageJoinTimes[i] /= numberOfIterations;
            averageDDLTimes[i] /= numberOfIterations;
        }
    }

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
     * When used as a global statistics class, returns the average amount of queries that successfully
     * exited the system.
     * @return totalQueriesProcessed
     */
    public double getAverageQueriesProcessed() {
        return averageQueriesProcessed;
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
     * @return t
     */
    public double getT() {return t;}



}
