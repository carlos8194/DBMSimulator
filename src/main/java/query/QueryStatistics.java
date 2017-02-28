package query;

/**
 * This class handles the statistics relating to a specific query.
 * It records each time a query enters or leaves an specific module queue or service, and the system as a whole.
 */
public class QueryStatistics {
    private Query query;

    private double systemArrivalTime;
    private double systemExitTime;
    private double queueEntryTime;

    private double[] moduleQueueTimes;
    private double[] moduleEntryTimes;
    private double[] moduleExitTimes;
    private double[] moduleTimes;


    /**
     * This constructor method simply initializes the arrays used for storing times and asocciates the statistics with
     * an specific query.
     * @param query
     */
    public QueryStatistics(Query query){
        this.query = query;
        moduleQueueTimes = new double[5];
        moduleEntryTimes = new double[5];
        moduleExitTimes = new double[5];
        moduleTimes = new double[5];
    }

    /**
     * Sets the query's arrival time to the system as a whole.
     * @param systemArrivalTime
     */
    public void setSystemArrivalTime(double systemArrivalTime) {
        this.systemArrivalTime = systemArrivalTime;
    }

    /**
     * Sets the query's exit time of the system as a whole.
     * @param systemExitTime
     */
    public void setSystemExitTime(double systemExitTime) {
        this.systemExitTime = systemExitTime;
    }

    /**
     * Sets the query's entry time to the module specified by the parameter moduleNumber.
     * @param moduleNumber
     * @param moduleEntryTime
     */
    public void setModuleEntryTime(int moduleNumber, double moduleEntryTime){
        moduleEntryTimes[moduleNumber] = moduleEntryTime;
    }

    /**
     * Sets the query's exit time to the module specified by the parameter moduleNumber.
     * @param modduleNumber
     * @param moduleExitTime
     */
    public void setModuleExitTime(int modduleNumber, double moduleExitTime){
        moduleExitTimes[modduleNumber] = moduleExitTime;
        moduleTimes[modduleNumber] = moduleExitTime - moduleEntryTimes[modduleNumber];
    }

    /**
     * Sets the amount of time this query spent in the module specified by module number queue.
     * @param moduleNumber
     * @param queueTime
     */
    public void setModuleQueueTime(int moduleNumber, double queueTime){
        moduleQueueTimes[moduleNumber] = queueTime;
    }

    /**
     * Returns the amount of time this query spent in the module specified by module number.
     * @param moduleNumber
     * @return moduleTime
     */
    public double getModuleTime(int moduleNumber){
        return moduleTimes[moduleNumber];
    }

    /**
     * Returns the amount of time this query spent in the module specified by module number queue.
     * @param moduleNumber
     * @return queueTime
     */
    public double getQueueTime(int moduleNumber ){return moduleQueueTimes[moduleNumber];}

    /**
     * Returns the time at which the queue entered the system.
     * @return queueEntryTime
     */
    public double getQueueEntryTime() {
        return queueEntryTime;
    }

    /**
     * Sets the time at which the queue entered the system.
     * @param queueEntryTime
     */
    public void setQueueEntryTime(double queueEntryTime) {
        this.queueEntryTime = queueEntryTime;
    }

    /**
     * Returns the total amount of time the query spent in the DBMS.
     * @return queryLifeTime
     */
    public double getQueryLifeTime(){
        return systemExitTime - systemArrivalTime;
    }
}
