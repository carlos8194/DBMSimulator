package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;


/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {
    
    private int k;
    private int discardedConnections;

    public ClientAdministrator(int concurrentConnections, Module next){
        servers = concurrentConnections;
        k = concurrentConnections;
        this.nextModule = next;
        discardedConnections = 0;
    }

    @Override
    public void processArrival(Query query){
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " entered Client Administrator module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule1(time);
        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            discardedConnections++;
        }
    }

    public void processExit(Query query) {
        System.out.println("Connection " + query.getID() + " exited Client Administrator module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule1(DBMS.getClock() );
        if (!query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    public void returnQueryResult(Query query){
        double time = DBMS.getClock() + (double) query.getBlocks() / 6.0;
        Event event = new Event(EventType.QUERY_RETURN, time, query);
        DBMS.addEvent(event);
        servers++;
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule1(time);
        double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }
}
