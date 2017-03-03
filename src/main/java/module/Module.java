package module;

import dbms.Simulator;
import event.Event;
import event.EventType;
import query.*;

import java.util.Queue;
/**
 * General representation of a module.
 */
public abstract class Module {
    protected int moduleNumber; //Unique number that correspond to a specific module
    protected Simulator DBMS; //Pointer to the DBMS data base that possesses the modules
    protected ModuleStatistics statistics; //An object that keeps track on the statistics
    protected int moduleCapacity; //Max number of servers on this module
    protected int availableServers; //Current number of servers available
    protected Module nextModule; //Pointer to the next module object
    protected Queue<Query> queue; //A queue that keeps queries that can not be attended right away
    protected enum ChangeType {ENTRY,EXIT}; //A enum to identify if a query comes from or out of the queue

    /**
     * This method does basic things such as attending a client, setting statistics, add queries to the queue...
     * @param query the one entering the module
     */
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleEntryTime(moduleNumber, time);
        statistics.incrementNumberOfArrivals();
        if(moduleNumber == 0) queryStatistics.setSystemArrivalTime(time);

        //Attend if posible.
        if (this.attendImmediately(query)) this.attendQuery(query);
        //else if(moduleNumber == 0) DBMS.incrementDiscardedConnections();
        else {
            //Lq: QueueSize change due to entry.
            this.recordQueueChange(query, ChangeType.ENTRY);
        }
    }

    /**
     * This method attends the query, sets the MODULE_END event, sets statistics and decreases the available servers.
     * @param query: the one about to be attended.
     */
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
            case 3: availableServers = (query.getQueryType() == QueryType.DDL) ? 0 : availableServers--;
                    break;
            default: availableServers--;
        }

    }

    /**
     * This method does things such as attending someone from the queue, free one or more servers, set statistics and
     * send the query exiting to the next module.
     * @param query: The one about to leave the module
     */
    public void processExit(Query query) {
        double time = DBMS.getClock();
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

    /**
     * This is the general version of how a module chooses the next client, the Transactional Storage Manager overrides
     * it with its own implementation and the Client Administrator by always returning null (because it does not have a
     * queue).
     * @return The next client Query object.
     */
    protected Query chooseNextClient() {
        return queue.poll();}


    /**
     * Each module implement this abstract method with according to their service time patterns.
     * @param query: The object to which service is going to be given.
     * @return Service time as a double value.
     */
    protected abstract double calculateDuration(Query query);

    /**
     * This is the general test to know if a query should be attended or be sent to the queue.
     * Only the Transactional Storage Manager overrides it.
     * @param query: The query that could possibly be attended.
     * @return true if the query can be attended right away, false otherwise.
     */
    protected boolean attendImmediately(Query query) {
        return availableServers > 0;
    }

    /**
     * This method handles the query time out events sent by the interface, by removing them from the queue (if thats
     * where they are), or by setting up their timeOut flag if they are currently in service.
     * @param query: The Query whose time is over.
     */
    public  void queryTimeout(Query query){
        if (query.isCurrentlyInQueue() ){
            queue.remove(query);
            this.recordQueueChange(query, ChangeType.EXIT);
        }
        else {
            query.setTimeOut(true);
        }
    }

    /**
     * This method is called when a Query interacts with the queue, by setting up the ModuleStatistics from this, and
     * setting the queries flag, and in case of an ENTRY, it also adds the query to the queue.
     * @param query: The query that comes from or goes to the queue.
     * @param changeType: ENTRY : Goes to the queue. EXIT : Comes from the queue.
     */
    private void recordQueueChange(Query query, ChangeType changeType){
        double time = DBMS.getClock();
        //Lq: QueueSize change
        double timeChange = time - statistics.getQueueSizeChangeTime();
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
            double queueTime = time -  queryStatistics.getQueueEntryTime();
            queryStatistics.setModuleQueueTime(moduleNumber, queueTime);
            statistics.incrementTotalQueueTime(queueTime);
        }
    }

    /**
     * This method is called when a Query interacts with service, by setting up the ModuleStatistics from this, and
     * setting the queries statistics.
     * @param query: The query that comes from or goes to service.
     * @param changeType: ENTRY : Goes to service. EXIT : Comes from service.
     */
    private void recordServiceChange(Query query, ChangeType changeType){
        double time = DBMS.getClock();
        //Ls: ServiceSize change due to entry.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double accumulatedServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = moduleCapacity - availableServers;

        if(currentServiceSize<0) System.out.println("negative service size,  modulo: "+ moduleNumber);
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

    /**
     * This modules own id number.
     * @return An integer that works as this modules id.
     */
    public int getModuleNumber(){return moduleNumber;}

    /**
     * Setter method for the modules next.
     * @param module: The new next of this module.
     */
    public void setNextModule(Module module){nextModule = module;}

    /**
     * Getter method for this modules statistics.
     * @return: this modules statistics object.
     */
    public ModuleStatistics getModuleStatistics(){return statistics;}

    /**
     * Calculates the servers that are currently attending someone.
     * @return The number of occupied servers.
     */
    public int getOccupiedServers(){
        return moduleCapacity - availableServers;
    }

    /**
     * @return The amount of queries currently in queue.
     */
    public int getQueueSize(){
        return queue.size();
    }

}


