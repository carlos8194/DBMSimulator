package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public  class QueryProcessor extends Module {
    private int n;

    public QueryProcessor(int availableProcesses, Module next){
        servers = availableProcesses;
        n = availableProcesses;
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

        if (servers > 0){
            this.attendQuery(query);
        }
        else {
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
        if(servers==n){
            statistics.incrementIdleTime(timeChange);
        }
        //Ls: ServiceSize change due to entry.
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        int currentServiceSize = n-servers;
        statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        //Record time when service size changed
        statistics.setServiceSizeChangeTime(time);


        //Create ModuleEnd event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }

    @Override
    public void processExit(Query query) {
        double time = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited Query Processor module");

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule3(time);
        statistics.incrementTotalServiceTime(queryStatistics.getTimeModule3());
        statistics.incrementQueriesProcessed();

        //Ls: ServiceSize change due to exit.
        double timeChange = time - statistics.getServiceSizeChangeTime();
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + timeChange);
        statistics.setServiceSizeChangeTime(time);
        servers++;

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
