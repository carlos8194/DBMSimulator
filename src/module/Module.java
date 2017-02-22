package module;

import com.sun.org.apache.xpath.internal.operations.Mod;
import event.Event;
import event.EventType;
import query.*;
import dbms.DBMS;
import utils.ProbabilityDistributions;

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
    protected enum ChangeType {ENTRY,EXIT};

    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Module: " + moduleNumber);

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleEntryTime(moduleNumber, time);
        statistics.incrementNumberOfArrivals();
        if(moduleNumber == 0) queryStatistics.setSystemArrivalTime(time);

        //Attend if posible.
        if (this.attendImmediately(query)) this.attendQuery(query);
        else if(moduleNumber == 0) DBMS.incrementDiscartedConnections();
        else {
            //Lq: QueueSize change due to entry.
            this.recordQueueChange(query, ChangeType.ENTRY);
        }
    }

    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = this.calculateDuration(query);

        //Adjust statistics.
        //Query came from queue
        if (query.isCurrentlyInQueue()) {
            //Lq: QueueSize change due to exit and change in Wq.
            this.recordQueueChange(query, ChangeType.EXIT);
        }
        //Query was attended immediately.
        //Ls: ServiceSize change due to entry, possible idleTime change.
        this.recordServiceChange(query, ChangeType.ENTRY);

        //Create module end event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        //Occupy Servers
        switch(moduleNumber){
            //case 0: break;//??
            case 3: availableServers = (query.getQueryType() == QueryType.DDL) ? 0 : availableServers--;
                    break;
            default: availableServers--;
        }

    }

    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited module " + moduleNumber );

        //Adjust Statistics.
        //Ls: ServiceSize change due to exit, number of queries increases.
        this.recordServiceChange(query, ChangeType.EXIT);

        //Free a server
        switch(moduleNumber){
            case 0: break;
            case 3: availableServers = (query.getQueryType() == QueryType.DDL) ? moduleCapacity : availableServers++;
                break;
            default: availableServers++;
        }

        //Attend someone from queue.
        Query anotherQuery = this.chooseNextClient();
        if (anotherQuery != null) {
            this.attendQuery(anotherQuery);
        }
        //Possibly kill connection
        if (!query.isTimeOut()) {
            if(moduleNumber == 4) ((ClientAdministrator)nextModule).returnQueryResult(query);
            else nextModule.processArrival(query);
        }
    }

    protected Query chooseNextClient() {return queue.poll();}


    protected abstract double calculateDuration(Query query);

    protected boolean attendImmediately(Query query) {
        return availableServers > 0;
    }

    public  void queryTimeout(Query query){
        if (query.isCurrentlyInQueue() ){
            queue.remove(query);
            this.recordQueueChange(query, ChangeType.EXIT);
        }
        else {
            query.setTimeOut(true);
        }
    }

    public void recordQueueChange(Query query, ChangeType changeType){
        double time = DBMS.getClock();
        //Lq: QueueSize change
        double timeChange = statistics.getQueueSizeChangeTime() - time;
        double currentAccumulate = statistics.getAccumulatedQueueSize();
        statistics.setAccumulatedQueueSize(currentAccumulate + (queue.size() * timeChange));
        statistics.setQueueSizeChangeTime(time);

        QueryStatistics queryStatistics = query.getStatistics();
        if(changeType == ChangeType.ENTRY) {
            query.setCurrentlyInQueue(true);
            queryStatistics.setQueueEntryTime(time);
            queue.add(query);
        }
        else{
            query.setCurrentlyInQueue(false);
            //Wq: TotalQueueTime change.
            double queuetime = queryStatistics.getQueueEntryTime() - time;
            queryStatistics.setModuleQueueTime(moduleNumber, queuetime);
            statistics.incrementTotalQueueTime(queuetime);
        }
    }

    public void recordServiceChange(Query query, ChangeType changeType){
        double time = DBMS.getClock();
        //Ls: ServiceSize change due to entry.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double accumulatedServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = moduleCapacity- availableServers;
        statistics.setAccumulatedServiceSize(accumulatedServiceSize + (timeChange*currentServiceSize));
        statistics.setServiceSizeChangeTime(time);

        //Possible idleTime change.
        if(changeType == ChangeType.ENTRY && availableServers == moduleCapacity){
            statistics.incrementIdleTime(timeChange);
        }
        else if (changeType == ChangeType.EXIT){
            QueryStatistics queryStatistics = query.getStatistics();
            queryStatistics.setModuleExitTime(moduleNumber,time);
            //Ws: average service time change.
            statistics.incrementTotalServiceTime(queryStatistics.getModuleTime(moduleNumber));

            statistics.incrementQueriesProcessed();
        }
    }

    public int getModuleNumber(){return moduleNumber;}

    public void setNextModule(Module module){nextModule = module;}

    public ModuleStatistics getModuleStatistics(){return statistics;}

}


