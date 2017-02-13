package module;

import query.*;
import dbms.DBMS;

import java.util.Queue;

/**
 * Created by Carlos on 03/02/2017.
 */
public abstract class Module {
    protected int moduleNumber;
    protected DBMS DBMS;
    protected ModuleStatistics statistics;
    protected int moduleCapacity;
    protected int availableServers;
    protected Module nextModule;
    protected Queue<Query> queue;
    protected enum changeType {ENTRY,EXIT};

    public abstract void processArrival(Query query);
    public abstract void processExit(Query query);
    protected abstract void attendQuery(Query query);


    public  void queryTimeout(Query query){
        if (query.isCurrentlyInQueue() ){
            queue.remove(query);
        }
        else {
            query.setTimeOut(true);
        }
    }

    public void recordQueueChange(Query query, changeType changeType){
        double time = DBMS.getClock();
        //Lq: QueueSize change
        double timeChange = statistics.getQueueSizeChangeTime()- time;
        double currentAccumulate = statistics.getAccumulatedQueueSize();
        statistics.setAccumulatedQueueSize(currentAccumulate + (queue.size()*timeChange));
        statistics.setQueueSizeChangeTime(time);

        QueryStatistics queryStatistics = query.getStatistics();
        if(changeType== Module.changeType.ENTRY) {
            query.setCurrentlyInQueue(true);
            queryStatistics.setQueueEntryTime(time);
            queue.add(query);
        }
        else{
            query.setCurrentlyInQueue(false);
            //Wq: TotalQueueTime change.
            double queuetime = queryStatistics.getQueueEntryTime()-time;
            queryStatistics.setModuleQueueTime(moduleNumber,queuetime);
            statistics.incrementTotalQueueTime(queuetime);
        }
    }

    public void recordServiceChange(Query query, changeType changeType){
        double time = DBMS.getClock();
        //Ls: ServiceSize change due to entry.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double accumulatedServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = moduleCapacity- availableServers;
        statistics.setAccumulatedServiceSize(accumulatedServiceSize + (timeChange*currentServiceSize));
        statistics.setServiceSizeChangeTime(time);

        //Possible idleTime change.
        if(changeType==changeType.ENTRY && availableServers == moduleCapacity){
            statistics.incrementIdleTime(timeChange);
        }
        else if (changeType==changeType.EXIT){
            QueryStatistics queryStatistics = query.getStatistics();
            queryStatistics.setModuleExitTime(moduleNumber,time);
            //Ws: average service time change.
            statistics.incrementTotalServiceTime(queryStatistics.getModuleTime(moduleNumber));

            statistics.incrementQueriesProcessed();
        }
    }

    public int getModuleNumber(){return moduleNumber;}

}


