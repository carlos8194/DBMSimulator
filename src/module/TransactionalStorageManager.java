package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class TransactionalStorageManager extends Module {
    private int p;

    public TransactionalStorageManager(int simultaneousConsultations, Module next){
        p = simultaneousConsultations;
        servers = simultaneousConsultations;
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
        queryStatistics.setEntryTimeModule4(time);
        statistics.incrementNumberOfArrivals();

        QueryType type = query.getQueryType();
        QueryType nextType;
        if(queue.peek() != null){
            nextType= queue.peek().getQueryType();
        }
        else{
            nextType=null;
        }

        if((type == QueryType.DDL && servers == p) || (nextType != QueryType.DDL && servers > 0)){
            attendQuery(query);
        }
        else{
            //Lq: QueueSize change due to entry.
            double timeChange = statistics.getQueueSizeChangeTime()- time;
            double currentAverage = statistics.getAccumulatedQueueSize();
            statistics.setAccumulatedQueueSize(currentAverage + (queue.size()*timeChange));
            statistics.setQueueSizeChangeTime(time);
            query.setCurrentlyInQueue(true);
            queryStatistics.setQueueEntryTime(time);
            queue.add(query);
        }

    }

    @Override
    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited Transactional Storage Manager module");


        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule4(time);
        statistics.incrementTotalServiceTime(queryStatistics.getTimeModule4());
        statistics.incrementQueriesProcessed();

        //Ls: ServiceSize change due to exit.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + timeChange);
        statistics.setServiceSizeChangeTime(time);
        servers = (query.getQueryType() == QueryType.DDL) ? p : servers + 1;

        //Attend someone from queue.
        Query anotherQuery = queue.peek();
        if (anotherQuery != null){
            QueryType type = query.getQueryType();
            if (type != QueryType.DDL ){
                this.attendQuery(anotherQuery);
                queue.poll();
            }
            else if (servers == p){
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
        double duration = p*0.03 + 0.1*this.calculateBlocks(query);

        //Adjust statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule4(time);

        //Query came from queue
        if(query.isCurrentlyInQueue()){
            //Change in Wq
            double queuetime = queryStatistics.getQueueEntryTime()-time;
            query.setCurrentlyInQueue(false);
            statistics.incrementTotalQueueTime(queuetime);
            //Lq: QueueSize change due to exit.
            double timeChange = statistics.getQueueSizeChangeTime()-time;
            double currentAverage = statistics.getAccumulatedQueueSize();
            statistics.setAccumulatedQueueSize(currentAverage + (queue.size()*timeChange));
            statistics.setQueueSizeChangeTime(time);
        }
        //Query was attended immediately.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        //If server was idle increment idle time
        if(servers==p){
            statistics.incrementIdleTime(timeChange);
        }
        //Ls: ServiceSize change due to entry.
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = p-servers;
        statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        //Record time when service size changed
        statistics.setServiceSizeChangeTime(time);

        //Create ModuleEnd event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers = (query.getQueryType() == QueryType.DDL) ? 0 : servers - 1;
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
