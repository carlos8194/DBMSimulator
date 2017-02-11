package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public  class QueryProcessor extends Module {
    private int n;

    public QueryProcessor(int availableProcesses, Module next){
        servers = availableProcesses;
        n = availableProcesses;
        nextModule = next;
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Query Processor module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule3(time);
        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            queue.add(query);
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
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule3(time);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }

    @Override
    public void processExit(Query query) {
        System.out.println("Connection " + query.getID() + " exited Query Processor module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule3(DBMS.getClock());
        servers++;
        Query anotherQuery = queue.poll();
        if (anotherQuery != null){
            this.attendQuery(anotherQuery);
        }
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

}
