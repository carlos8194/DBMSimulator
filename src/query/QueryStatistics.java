package query;

/**
 * Created by Carlos on 03/02/2017.
 */
public class QueryStatistics {
    private Query query;

    private double systemArrivalTime;
    private double systemExitTime;

    private double queueEntryTime;
    private double entryTimeModule1;
    private double exitTimeModule1;
    private double timeModule1;
    private double entryTimeModule2;
    private double exitTimeModule2;
    private double timeModule2;
    private double entryTimeModule3;
    private double exitTimeModule3;
    private double timeModule3;
    private double entryTimeModule4;
    private double exitTimeModule4;
    private double timeModule4;
    private double entryTimeModule5;
    private double exitTimeModule5;
    private double timeModule5;

    public QueryStatistics(Query query){
        this.query = query;
    }

    public double getSystemArrivalTime() {
        return systemArrivalTime;
    }

    public void setSystemArrivalTime(double systemArrivalTime) {
        this.systemArrivalTime = systemArrivalTime;
    }

    public double getSystemExitTime() {
        return systemExitTime;
    }

    public void setSystemExitTime(double systemExitTime) {
        this.systemExitTime = systemExitTime;
    }

    public double getEntryTimeModule1() {
        return entryTimeModule1;
    }

    public void setEntryTimeModule1(double entryTimeModule1) {
        this.entryTimeModule1 = entryTimeModule1;
    }

    public double getExitTimeModule1() {
        return exitTimeModule1;
    }

    public void setExitTimeModule1(double exitTimeModule1) {

        this.exitTimeModule1 = exitTimeModule1;
        this.timeModule1 = exitTimeModule1 - entryTimeModule1;
    }

    public double getTimeModule1() {
        return timeModule1;
    }

    public double getEntryTimeModule2() {
        return entryTimeModule2;
    }

    public void setEntryTimeModule2(double entryTimeModule2) {
        this.entryTimeModule2 = entryTimeModule2;
    }

    public double getExitTimeModule2() {
        return exitTimeModule2;
    }

    public void setExitTimeModule2(double exitTimeModule2) {
        this.exitTimeModule2 = exitTimeModule2;
        this.timeModule2 = exitTimeModule2 - entryTimeModule2;
    }

    public double getTimeModule2() {
        return timeModule2;
    }

    public double getEntryTimeModule3() {
        return entryTimeModule3;
    }

    public void setEntryTimeModule3(double entryTimeModule3) {
        this.entryTimeModule3 = entryTimeModule3;
    }

    public double getExitTimeModule3() {
        return exitTimeModule3;
    }

    public void setExitTimeModule3(double exitTimeModule3) {
        this.exitTimeModule3 = exitTimeModule3;
        this.timeModule3 = exitTimeModule3 - entryTimeModule3;
    }

    public double getTimeModule3() {
        return timeModule3;
    }

    public double getEntryTimeModule4() {
        return entryTimeModule4;
    }

    public void setEntryTimeModule4(double entryTimeModule4) {
        this.entryTimeModule4 = entryTimeModule4;
    }

    public double getExitTimeModule4() {
        return exitTimeModule4;
    }

    public void setExitTimeModule4(double exitTimeModule4) {
        this.exitTimeModule4 = exitTimeModule4;
        this.timeModule4 = exitTimeModule4 - entryTimeModule4;
    }

    public double getTimeModule4() {
        return timeModule4;
    }

    public double getEntryTimeModule5() {
        return entryTimeModule5;
    }

    public void setEntryTimeModule5(double entryTimeModule5) {
        this.entryTimeModule5 = entryTimeModule5;
    }

    public double getExitTimeModule5() {
        return exitTimeModule5;
    }

    public void setExitTimeModule5(double exitTimeModule5) {
        this.exitTimeModule5 = exitTimeModule5;
        this.timeModule5 = exitTimeModule5 - entryTimeModule5;
    }

    public double getTimeModule5() {
        return timeModule5;
    }

    public double getQueueEntryTime() {
        return queueEntryTime;
    }

    public void setQueueEntryTime(double queueEntryTime) {
        this.queueEntryTime = queueEntryTime;
    }
}
