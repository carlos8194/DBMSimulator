package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class TransactionalStorageManager extends Module {

    public TransactionalStorageManager(int p, Module next){
        moduleNumber = 4;
        moduleCapacity = p;
        availableServers = p;
        nextModule = next;
        queue = new PriorityQueue<>(Query::compareTo);
    }

    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Transactional Storage Manager module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleEntryTime(moduleNumber,time);
        statistics.incrementNumberOfArrivals();

        QueryType type = query.getQueryType();
        QueryType nextType;
        if(queue.peek() != null){
            nextType= queue.peek().getQueryType();
        }
        else{
            nextType=null;
        }

        if((type == QueryType.DDL && availableServers == moduleCapacity) || (nextType != QueryType.DDL && availableServers > 0)){
            attendQuery(query);
        }
        else{
            //Lq: QueueSize change due to entry.
            this.recordQueueChange(query,changeType.ENTRY);
        }

    }

    @Override
    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited Transactional Storage Manager module");

        //Adjust Statistics.
        this.recordServiceChange(query,changeType.EXIT);

        //Free servers
        availableServers = (query.getQueryType() == QueryType.DDL) ? moduleCapacity : availableServers + 1;

        //Attend someone from queue.
        Query anotherQuery = queue.peek();
        if (anotherQuery != null){
            QueryType type = query.getQueryType();
            if (type != QueryType.DDL ){
                this.attendQuery(anotherQuery);
                queue.poll();
            }
            else if (availableServers == moduleCapacity){
                this.attendQuery(anotherQuery);
                queue.poll();
            }
        }
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = moduleCapacity*0.03 + 0.1*this.calculateBlocks(query);

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
        availableServers = (query.getQueryType() == QueryType.DDL) ? 0 : availableServers - 1;
    }

    private int calculateBlocks(Query query){
        double B = 0;
        switch (query.getQueryType()){
            case JOIN:
                B = ProbabilityDistributions.Uniform(1,16) + ProbabilityDistributions.Uniform(1,12);
                break;
            case SELECT:
                B = ProbabilityDistributions.Uniform(1,64);
                break;
        }
        query.setBlocks( (int) B);
        return (int) B;
    }
}
