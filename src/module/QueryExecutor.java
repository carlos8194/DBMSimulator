package module;

import event.*;
import query.Query;
import query.QueryStatistics;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private ClientAdministrator administrator;

    public QueryExecutor(int m, Module next){
        moduleCapacity = m;
        availableServers = m;
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

        if (availableServers > 0){
            this.attendQuery(query);
        }
        else {
            //Lq: QueueSize change due to entry.
            this.recordQueueChange(query,changeType.ENTRY);
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
        //Ls: ServiceSize change due to exit, number of queries increases.
        this.recordServiceChange(query,changeType.EXIT);

        //Free a server.
        availableServers++;

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
            //Lq: QueueSize change due to exit and change in Wq.
            this.recordQueueChange(query,changeType.EXIT);
        }
        //Query was attended immediately.
        //Ls: ServiceSize change due to entry, possible idleTime change.
        this.recordServiceChange(query,changeType.ENTRY);

        //Add Module End event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        availableServers--;
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
