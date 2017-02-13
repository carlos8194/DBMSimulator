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
        moduleNumber = 2;
        nextModule = next;
        queue = new ArrayDeque<>();
        availableServers = 1;
        moduleCapacity = 1;
        queue = new ArrayDeque<>();
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Process Manager module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleEntryTime(moduleNumber,time);
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
        System.out.println("Connection " + query.getID() + " exited Process Manager module");

        //Adjust Statistics.
        //Ls: ServiceSize change due to exit, number of queries increases.
        this.recordServiceChange(query,changeType.EXIT);

        //Free a server
        availableServers++;

        //Attend someone from queue.
        Query anotherQuery = queue.poll();
        if (anotherQuery != null){
            this.attendQuery(anotherQuery);
        }
        //
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = ProbabilityDistributions.Normal(1.5, 0.1);

        //Adjust statistics.
        //Query came from queue
        if(query.isCurrentlyInQueue()){
            //Lq: QueueSize change due to exit and change in Wq.
            this.recordQueueChange(query,changeType.EXIT);
        }
        //Query was attended immediately.
        //Ls: ServiceSize change due to entry, possible idleTime change.
        this.recordServiceChange(query,changeType.ENTRY);

        //Create module end event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        availableServers--;
    }
}
