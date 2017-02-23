package query;

/**
 * Created by Carlos on 03/02/2017.
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



    public QueryStatistics(Query query){
        this.query = query;
        moduleQueueTimes = new double[5];
        moduleEntryTimes = new double[5];
        moduleExitTimes = new double[5];
        moduleTimes = new double[5];
    }

    public void setSystemArrivalTime(double systemArrivalTime) {
        this.systemArrivalTime = systemArrivalTime;
    }

    public void setSystemExitTime(double systemExitTime) {
        this.systemExitTime = systemExitTime;
    }

    public void setModuleEntryTime(int moduleNumber, double moduleEntryTime){
        moduleEntryTimes[moduleNumber] = moduleEntryTime;
    }
    public void setModuleExitTime(int modduleNumber, double moduleExitTime){
        moduleExitTimes[modduleNumber] = moduleExitTime;
        moduleTimes[modduleNumber] = moduleExitTime - moduleEntryTimes[modduleNumber];
    }

    public void setModuleQueueTime(int moduleNumber, double queueTime){
        moduleQueueTimes[moduleNumber] = queueTime;
    }

    public double getModuleTime(int moduleNumber){
        return moduleTimes[moduleNumber];
    }

    public double getQueueTime(int moduleNumber ){return moduleQueueTimes[moduleNumber];}

    public double getQueueEntryTime() {
        return queueEntryTime;
    }

    public void setQueueEntryTime(double queueEntryTime) {
        this.queueEntryTime = queueEntryTime;
    }

    public double getQueryLifeTime(){
        return systemExitTime - systemArrivalTime;
    }
}
