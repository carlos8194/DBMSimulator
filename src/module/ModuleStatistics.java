package module;

/**
 * Created by Rodrigo on 2/10/2017.
 */
public class ModuleStatistics {
    private Module module;

    //Performance Measures.
    private double averageSystemSize;// L = Lq + Ls
    private double averageQueueSize;// Lq
    private double averageServiceSize;// Ls

    private double averageResponseTime;// W= Wq + Ws
    private double averageQueueTime;// Wq
    private double averageServiceTime;// Ws

    private boolean stability; // ρ = λ/μ
    private double ocupationRate;//rho: ρ = λ/μ
    private double arrivalRate;// lambda: λ
    private double serviceRate;// mu: μ

    private double lastEventTime;
    private double idleTime;

    //Current Server State.
    private int currentQueueSize;
    private int currentServiceSize;
    private int queriesProcessed;

    public ModuleStatistics(Module module){
        this.setModule(module);

    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public double getAverageSystemSize() {
        return averageSystemSize;
    }

    public void setAverageSystemSize(double averageSystemSize) {
        this.averageSystemSize = averageSystemSize;
    }

    public double getAverageQueueSize() {
        return averageQueueSize;
    }

    public void setAverageQueueSize(double averageQueueSize) {
        this.averageQueueSize = averageQueueSize;
    }

    public double getAverageServiceSize() {
        return averageServiceSize;
    }

    public void setAverageServiceSize(double averageServiceSize) {
        this.averageServiceSize = averageServiceSize;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public double getAverageQueueTime() {
        return averageQueueTime;
    }

    public void setAverageQueueTime(double averageQueueTime) {
        this.averageQueueTime = averageQueueTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public boolean isStability() {
        return stability;
    }

    public void setStability(boolean stability) {
        this.stability = stability;
    }

    public double getOcupationRate() {
        return ocupationRate;
    }

    public void setOcupationRate(double ocupationRate) {
        this.ocupationRate = ocupationRate;
    }

    public double getArrivalRate() {
        return arrivalRate;
    }

    public void setArrivalRate(double arrivalRate) {
        this.arrivalRate = arrivalRate;
    }

    public double getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(double serviceRate) {
        this.serviceRate = serviceRate;
    }

    public double getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(double lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public int getCurrentQueueSize() {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(int currentQueueSize) {
        this.currentQueueSize = currentQueueSize;
    }

    public int getCurrentServiceSize() {
        return currentServiceSize;
    }

    public void setCurrentServiceSize(int currentServiceSize) {
        this.currentServiceSize = currentServiceSize;
    }

    public int getQueriesProcessed() {
        return queriesProcessed;
    }

    public void setQueriesProcessed(int queriesProcessed) {
        this.queriesProcessed = queriesProcessed;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }
}
