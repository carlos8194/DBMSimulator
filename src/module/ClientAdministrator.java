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
        k = concurrentConnections;
        this.nextModule = next;
        discardedConnections = 0;
    }

    @Override
    public void processArrival(Query query){
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Client Administrator module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule1(time);
        if (k > 0){
            queryStatistics.setEntryTimeModule1(time);
            double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
            Event event = new Event(EventType.MODULE_END, time + duration, query);
            DBMS.addEvent(event);
            k--;
        }
        else {
            discardedConnections++;
        }
    }

    public void processExit(Query query) {
        System.out.println("Conecction " + query.getID() + " exited Client Administrator module");
        if (! query.isTimeOut() ) {
            nextModule.processArrival(query);
        }

        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule1(DBMS.getClock());
    }

    void returnQueryResult(Query query, int B){
        double time = DBMS.getClock() + (double) B/6;
        Event event = new Event(EventType.QUERY_RETURN, time, query);
        DBMS.addEvent(event);
        k++;
    }

    @Override
    public void queryTimeout(Query query) {}
}
