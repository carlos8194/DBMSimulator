package module;

/**
 * This class handles the statistics relating to a specific module.
 * Most of the statistics are performance measures derived from Queueing Theory.
 * Implements methods for changing variables used at runtime and a method that calculates the final statistics
 * after the iteration ends.
 *
 */
public class ModuleStatistics {
    private Module module;

    //Performance Measures.
    private int numberOfArrivals;

    private double averageSystemSize;// L = Lq + Ls
    private double averageQueueSize;// Lq
    private double averageServiceSize;// Ls

    private double averageResponseTime;// W = Wq + Ws
    private double averageQueueTime;// Wq
    private double averageServiceTime;// Ws

    private boolean stability; // ρ = λ/μ
    private double occupationRate;//rho: ρ = λ/μ
    private double arrivalRate;// lambda: λ
    private double serviceRate;// mu: μ

    private int queriesProcessed;
    private double idleTime;

    //Used internally at run time for calculating other statistics.
    private double accumulatedQueueSize;// Used for calculating Lq
    private double accumulatedServiceSize;// Used for calculating Ls
    private double totalQueueTime;// Used for calculating Wq
    private double totalServiceTime;// Used for calculating Ws
    private double queueSizeChangeTime;
    private double serviceSizeChangeTime;
    private boolean finalStatisticsCalculated;

    /**
     * Constructor method, initializes the class.
     *
     */
    public ModuleStatistics(Module module){
        this.module = module;
        finalStatisticsCalculated = false;
    }

    /**
     * Returns DBMS module associated with this statistics class.
     * @return Module
     *
     */
    public Module getModule() {
        return module;
    }


    //Running time methods.
    /**
     * Returns the last time someone entered or left this module's queue.
     * @return queueSizeChangeTime
     *
     */
    public double getQueueSizeChangeTime() {
        return queueSizeChangeTime;
    }

    /**
     * Sets the time of the last recorded change in this module's queue.
     * @param queueSizeChangeTime
     *
     */
    public void setQueueSizeChangeTime(double queueSizeChangeTime) {
        this.queueSizeChangeTime = queueSizeChangeTime;
    }

    /**
     * Increments the number of queries processed by this module by one.
     *
     */
    public void incrementQueriesProcessed() {this.queriesProcessed++;}

    /**
     * Returns the amount of time this module has been idle.
     * @return time
     */
    public double getIdleTime() {
        return idleTime;
    }

    /**
     *  Increments the amount of time this module has been idle by the time received as parameter.
     *  @param idleTime
     */
    public void incrementIdleTime(double idleTime) {
        this.idleTime += idleTime;
    }

    /**
     *  Increments the number of arrivals to the module by one.
     *
     */
    public void incrementNumberOfArrivals() {
        this.numberOfArrivals++;
    }

    /**
     *  Returns the last time the service size of the module changed.
     *  Meaning someone was attended or someone completed service time.
     *  @return time
     */
    public double getServiceSizeChangeTime() {
        return serviceSizeChangeTime;
    }

    /**
     *  Sets the last time the service size of the module changed.
     *  Meaning someone was attended or someone completed service time.
     *  @param serviceSizeChangeTime
     */
    public void setServiceSizeChangeTime(double serviceSizeChangeTime) {
        this.serviceSizeChangeTime = serviceSizeChangeTime;
    }

    /**
     *  Increments the total time clients have spent in this module's queue.
     *  @param queueTime spent by the client that just left the queue.
     */
    public void incrementTotalQueueTime(double queueTime) {
        this.totalQueueTime += queueTime;
    }

    /**
     *  Increments the total time clients have spent in this module's service.
     *  @param serviceTime spent by the client that just left service.
     */
    public void incrementTotalServiceTime(double serviceTime) {
        this.totalServiceTime += serviceTime;
    }

    /**
     *  Returns the accumulated queue size up to this point.
     *  accumulatedQueueSize = (qs1*t1) + (qs2*t2) + ... + (qsn*tn)
     *  @return  accumulatedQueueSize
     */
    public double getAccumulatedQueueSize() {
        return accumulatedQueueSize;
    }

    /**
     *  Sets the accumulated queue size to the value received as a parameter.
     *  accumulatedQueueSize = (qs1*t1) + (qs2*t2) + ... + (qsn*tn)
     *  @param accumulatedQueueSize up to this point in time.
     */
    public void setAccumulatedQueueSize(double accumulatedQueueSize) {
        this.accumulatedQueueSize = accumulatedQueueSize;
    }

    /**
     *  Returns the accumulated service size up to this point.
     *  accumulatedServiceSize = (ss1*t1) + (ss2*t2) + ... + (ssn*tn)
     *  @return  accumulatedServiceSize
     */
    public double getAccumulatedServiceSize() {
        return accumulatedServiceSize;
    }

    /**
     *  Sets the accumulated service size up to this point, to the value received as parameter
     *  accumulatedServiceSize = (ss1*t1) + (ss2*t2) + ... + (ssn*tn)
     *  @param  accumulatedServiceSize up to this point in time.
     */
    public void setAccumulatedServiceSize(double accumulatedServiceSize) {
        this.accumulatedServiceSize = accumulatedServiceSize;
    }


    /**
     * This methods does the final work required to be able to present the module's statistic correctly.
     * This means it takes the running time variables does some additional work on them and stores the final statistic
     * which is ready to be returned at the end of this simulation iteration.
     * @param simulationRunningTime
     */
    public void calculateFinalStatistics(double simulationRunningTime){
        //Final Average Service time: Ws
        averageServiceTime = totalServiceTime / queriesProcessed;
        //Final Average Queue time: Wq
        averageQueueTime = totalQueueTime / queriesProcessed;
        //Final Average Response Time : W = Wq + Ws.
        averageResponseTime = averageQueueTime + averageServiceTime;
        //Final Average Queue Size: Lq
        averageQueueSize = accumulatedQueueSize / simulationRunningTime;
        //Final Average Service Size: Ls
        averageServiceSize= accumulatedServiceSize / simulationRunningTime;
        //Final Average System Size : L = Lq + Ls.
        averageSystemSize = averageQueueSize + getAverageServiceSize();
        //Final arrival rate.
        arrivalRate = numberOfArrivals / simulationRunningTime;
        //Final service rate.
        serviceRate = queriesProcessed / simulationRunningTime;
        //Final ocupation rate.
        occupationRate = arrivalRate / serviceRate;
        //Final stability.
        stability = occupationRate < 1 ;
        finalStatisticsCalculated = true;
    }

    //After final statistics are calculated these methods can be called.

    /**
     * Returns the amount of queries this module served during the simulation.
     * @return queriesProcessed
     */
    public int getQueriesProcessed(){
        return queriesProcessed;
    }

    /**
     * Returns the average time a client spent in this module's queue (Wq).
     * Given by the formula: totalQueueTime / queriesProcessed.
     * @return averageQueueTime
     */
    public double getAverageQueueTime() {
        assert(finalStatisticsCalculated);
        return averageQueueTime;
    }

    /**
     * Returns the average time a client spent in this module's service (Ws).
     * Given by the formula: averageServiceTime = totalServiceTime / queriesProcessed.
     * @return averageServiceTime
     */
    public double getAverageServiceTime() {
        assert(finalStatisticsCalculated);
        return averageServiceTime;
    }

    /**
     * Returns the average time a client spent in this module(W).
     * Given by the formula: averageResponseTime = averageQueueTime + averageServiceTime.
     * @return averageResponseTime
     */
    public double getAverageResponseTime() {
        assert(finalStatisticsCalculated);
        return averageResponseTime;
    }

    /**
     * Returns the average size this module's queue had during this simulation iteration (Lq).
     * Given by the formula: averageQueueSize = (qs1*t1) + (qs2*t2) + ... + (qsn*tn) / totalRunningTime.
     * @return averageQueueSize
     */
    public double getAverageQueueSize() {
        assert(finalStatisticsCalculated);
        return averageQueueSize;
    }

    /**
     * Returns the average size this module's service had during this simulation iteration (Ls).
     * Given by the formula: (ss1*t1) + (ss2*t2) + ... + (ssn*tn)/ totalRunningTime.
     * @return averageServiceSize
     */
    public double getAverageServiceSize() {
        assert(finalStatisticsCalculated);
        return averageServiceSize;
    }

    /**
     * Returns the average size this module had during this simulation iteration (L).
     * Given by the formula: averageResponseTime = averageQueueTime + averageServiceTime.
     * @return averageResponseTime
     */
    public double getAverageSystemSize() {
        assert(finalStatisticsCalculated);
        return averageSystemSize;
    }

    /**
     * Returns the fraction of time this module was attending at least one client (ρ).
     * Given by the formula: ρ = λ/μ
     * @return occupationRate
     */
    public double getOccupationRate() {
        assert(finalStatisticsCalculated);
        return occupationRate;
    }

    /**
     * Returns average rate at which clients arrived to this module (λ).
     * @return arrivalRate
     */
    public double getArrivalRate() {
        assert(finalStatisticsCalculated);
        return arrivalRate;
    }

    /**
     * Returns average rate at which clients where attended in this module (μ).
     * @return serviceRate
     */
    public double getServiceRate() {
        assert(finalStatisticsCalculated);
        return serviceRate;
    }

    /**
     * Returns true if this module's occupation rate was less than 1 during this simulation iteration, false otherwise.
     * @return
     */
    public boolean isStable(){
        assert(finalStatisticsCalculated);
        return stability;
    }

    /**
     * Returns the fraction of time this module was idle (no one in service or queue).
     * @return idleTime
     */
    public double getTotalIdleTime(){
        assert(finalStatisticsCalculated);
        return idleTime;
    }





}
