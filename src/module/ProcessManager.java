package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;


import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class ProcessManager extends Module {

    public ProcessManager(Module next){
        nextModule = next;
        queue = new ArrayDeque<>();
        servers = 1;
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Process Manager module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule2(time);
        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            queue.add(query);
        }
    }

    @Override
    public void processExit(Query query) {
        System.out.println("Connection " + query.getID() + " exited Process Manager module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule2(DBMS.getClock());
        servers++;
        Query anotherQuery = queue.poll();
        if (anotherQuery != null){
            this.attendQuery(anotherQuery);
        }
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = ProbabilityDistributions.Normal(1.5, 0.1);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule2(time);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }
}
