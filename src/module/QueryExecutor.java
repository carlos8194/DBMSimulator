package module;

import event.*;
import query.Query;
import query.QueryStatistics;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private int m;
    private ClientAdministrator administrator;

    public QueryExecutor(int availableProcesses, Module next){
        m = availableProcesses;
        servers = availableProcesses;
        nextModule = next;
        administrator = (ClientAdministrator) next;
        queue = new ArrayDeque<>();
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Query Executor module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule5(time);
        statistics.incrementNumberOfArrivals();

        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            //Lq: QueueSize change due to entry.
            double timeChange = statistics.getQueueSizeChangeTime()- time;
            double currentAverage = statistics.getAccumulatedQueueSize();
            statistics.setAccumulatedQueueSize(currentAverage + (queue.size()*timeChange));
            statistics.setQueueSizeChangeTime(time);
            query.setCurrentlyInQueue(true);
            queryStatistics.setQueueEntryTime(time);

            queue.add(query);
        }
    }

    @Override
    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " exited Query Executor module");

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule5(time);
        statistics.incrementTotalServiceTime(queryStatistics.getTimeModule5());
        statistics.incrementQueriesProcessed();

        //Ls: ServiceSize change due to exit.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + timeChange);
        statistics.setServiceSizeChangeTime(time);
        servers++;

        //Attend someone from queue.
        Query anotherQuery = queue.poll();
        if (anotherQuery != null){
            this.attendQuery(anotherQuery);
        }
        if ( !query.isTimeOut() ){
            administrator.returnQueryResult(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = this.calculateDuration(query);

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule5(time);

        //Query came from queue
        if(query.isCurrentlyInQueue()){
            //Change in Wq
            double queuetime = queryStatistics.getQueueEntryTime()-time;
            query.setCurrentlyInQueue(false);
            statistics.incrementTotalQueueTime(queuetime);
            //Lq: QueueSize change due to exit.
            double timeChange = statistics.getQueueSizeChangeTime()-time;
            double currentAverage = statistics.getAccumulatedQueueSize();
            statistics.setAccumulatedQueueSize(currentAverage + (queue.size()*timeChange));
            statistics.setQueueSizeChangeTime(time);
        }
        //Query was attended immediately.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        //If server was idle increment idle time
        if(servers==m){
            statistics.incrementIdleTime(timeChange);
        }
        //Ls: ServiceSize change due to entry.
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = m-servers;
        statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        //Record time when service size changed
        statistics.setServiceSizeChangeTime(time);

        //Add Module End event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }

    private double calculateDuration(Query query){
        double duration;
        switch ( query.getQueryType() ){
            case DDL:
                duration = 0.5;
                break;
            case UPDATE:
                duration = 1;
                break;
            default:
                duration = Math.pow(query.getBlocks(), 2);
                break;
        }
        return duration;
    }
}
