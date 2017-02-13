package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public  class QueryProcessor extends Module {

    public QueryProcessor(int n, Module next){
        availableServers = n;
        moduleCapacity = n;
        nextModule = next;
        queue = new ArrayDeque<>();
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Query Processor module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule3(time);
        statistics.incrementNumberOfArrivals();

        if (availableServers > 0){
            this.attendQuery(query);
        }
        else {
            //Lq: QueueSize change due to entry.
            this.recordQueueChange(query,changeType.ENTRY);
        }
    }

    private double calculateDuration(Query query){
        double x = (Math.random() < 0.7) ? 0.1 : 0.4;
        x += ProbabilityDistributions.Uniform(0, 0.8);
        x += ProbabilityDistributions.Normal(1, 0.5);
        x += ProbabilityDistributions.Exponential(0.7);
        x += (query.getQueryType().readOnly) ? 0.1 : 0.5;
        return x;
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = this.calculateDuration(query);

        //Adjust Statistics
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule3(time);

        //Query came from queue
        if(query.isCurrentlyInQueue()){
            //Lq: QueueSize change due to exit and change in Wq.
            this.recordQueueChange(query,changeType.EXIT);
        }
        //Query was attended immediately.
        //Ls: ServiceSize change due to entry, possible idleTime change.
        this.recordServiceChange(query,changeType.ENTRY);

        //Create ModuleEnd event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        availableServers--;
    }

    @Override
    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited Query Processor module");

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule3(time);
        statistics.incrementTotalServiceTime(queryStatistics.getTimeModule3());
        //Ls: ServiceSize change due to exit, number of queries increases.
        this.recordServiceChange(query,changeType.EXIT);

        //Free a server.
        availableServers++;

        //Attend someone from queue.
        Query anotherQuery = queue.poll();
        if (anotherQuery != null){
            this.attendQuery(anotherQuery);
        }
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

}
