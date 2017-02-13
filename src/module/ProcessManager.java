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
        queue = new ArrayDeque<>();
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Process Manager module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule2(time);
        statistics.incrementNumberOfArrivals();

        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            //Lq: QueueSize change due to entry.
            double timeChange = statistics.getQueueSizeChangeTime()-time;
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
        System.out.println("Connection " + query.getID() + " exited Process Manager module");

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule2(time);
        statistics.incrementTotalServiceTime(queryStatistics.getTimeModule2());
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
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule2(time);

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
        if(servers==1){
            statistics.incrementIdleTime(timeChange);
        }
        //Ls: ServiceSize change due to entry.
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + timeChange);
        //Record time when service size changed
        statistics.setServiceSizeChangeTime(time);

        //Create module end event.
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }
}
