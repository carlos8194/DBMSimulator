package module;

/**
 * Created by Rodrigo on 2/10/2017.
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
    private double ocupationRate;//rho: ρ = λ/μ
    private double arrivalRate;// lambda: λ
    private double serviceRate;// mu: μ

    private double idleTime;
    private int queriesProcessed;

    //Used internally at run time for calculating other statistics.
    private double accumulatedQueueSize;// Used for calculating Lq
    private double accumulatedServiceSize;// Used for calculating Ls
    private double totalQueueTime;// Used for calculating Wq
    private double totalServiceTime;// Used for calculating Ws
    private double queueSizeChangeTime;
    private double serviceSizeChangeTime;


    private boolean finalStatisticsCalculated;


    public ModuleStatistics(Module module){
        this.module = module;
        finalStatisticsCalculated = false;
    }

    public Module getModule() {
        return module;
    }


    //Running time methods.
    public double getQueueSizeChangeTime() {
        return queueSizeChangeTime;
    }

    public void setQueueSizeChangeTime(double queueSizeChangeTime) {
        this.queueSizeChangeTime = queueSizeChangeTime;
    }

    public void incrementQueriesProcessed() {this.queriesProcessed++;}

    public double getIdleTime() {
        return idleTime;
    }

    public void incrementIdleTime(double idleTime) {
        this.idleTime += idleTime;
    }

    public void incrementNumberOfArrivals() {
        this.numberOfArrivals++;
    }

    public double getServiceSizeChangeTime() {
        return serviceSizeChangeTime;
    }

    public void setServiceSizeChangeTime(double serviceSizeChangeTime) {
        this.serviceSizeChangeTime = serviceSizeChangeTime;
    }

    public void incrementTotalQueueTime(double totalQueueTime) {
        this.totalQueueTime += totalQueueTime;
    }

    public void incrementTotalServiceTime(double totalServiceTime) {
        this.totalServiceTime += totalServiceTime;
    }

    public double getAccumulatedQueueSize() {
        return accumulatedQueueSize;
    }

    public void setAccumulatedQueueSize(double accumulatedQueueSize) {
        this.accumulatedQueueSize = accumulatedQueueSize;
    }

    public double getAccumulatedServiceSize() {
        return accumulatedServiceSize;
    }

    public void setAccumulatedServiceSize(double accumulatedServiceSize) {
        this.accumulatedServiceSize = accumulatedServiceSize;
    }




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
        ocupationRate = arrivalRate / serviceRate;
        //Final stability.
        stability = (ocupationRate<1) ? true : false;
        finalStatisticsCalculated = true;
    }

    //After final statistics are calculated this methods can be called.
    public double getAverageQueueTime() {
        assert(finalStatisticsCalculated);
        return averageQueueTime;
    }

    public double getAverageServiceTime() {
        assert(finalStatisticsCalculated);
        return averageServiceTime;
    }

    public double getAverageResponseTime() {
        assert(finalStatisticsCalculated);
        return averageResponseTime;
    }

    public double getAverageQueueSize() {
        assert(finalStatisticsCalculated);
        return averageQueueSize;
    }

    public double getAverageServiceSize() {
        assert(finalStatisticsCalculated);
        return averageServiceSize;
    }

    public double getAverageSystemSize() {
        assert(finalStatisticsCalculated);
        return averageSystemSize;
    }

    public double getOcupationRate() {
        assert(finalStatisticsCalculated);
        return ocupationRate;
    }

    public double getArrivalRate() {
        assert(finalStatisticsCalculated);
        return arrivalRate;
    }

    public double getServiceRate() {
        assert(finalStatisticsCalculated);
        return serviceRate;
    }

    public boolean isStable(){
        assert(finalStatisticsCalculated);
        return stability;
    }

    public double getTotalIdleTime(){
        assert(finalStatisticsCalculated);
        return idleTime;
    }





}
