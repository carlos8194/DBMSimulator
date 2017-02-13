package module;

import dbms.DBMS;
import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.ArrayDeque;


/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {
    
    private int k;

    public ClientAdministrator(int concurrentConnections, Module next){
        moduleNumber=1;
        availableServers = concurrentConnections;
        k = concurrentConnections;
        this.nextModule = next;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    @Override
    public void processArrival(Query query){
        double currentTime = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " entered Client Administrator module");

        //Adjust Statistics.
        query.setCurrentModule(this);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setSystemArrivalTime(currentTime);
        statistics.incrementNumberOfArrivals();

        //Attend if posible, discard otherwise.
        if (availableServers > 0){
            this.attendQuery(query);
        }
        else {
            DBMS.incrementDiscartedConnections();
        }
    }

    public void processExit(Query query) {
        double currentTime = DBMS.getClock();
        System.out.println("Connection " + query.getID() + " exited Client Administrator module");

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleExitTime(moduleNumber,currentTime);
        statistics.incrementTotalServiceTime(queryStatistics.getModuleTime(moduleNumber));

        if (!query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    public void returnQueryResult(Query query){
        //Calculate
        double time = DBMS.getClock() + (double) query.getBlocks() / 6.0;

        //Adjust Statistics
        query.getStatistics().setSystemExitTime(time);
        statistics.incrementQueriesProcessed();

        double timeChange = time - statistics.getServiceSizeChangeTime();
        int currentServiceSize = k - availableServers;
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        statistics.setServiceSizeChangeTime(time);


        //Create Query Return event
        Event event = new Event(EventType.QUERY_RETURN, time, query);
        DBMS.addEvent(event);
        availableServers++;
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();

        //Adjust Statistics.
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setModuleEntryTime(moduleNumber,time);
        //If server was idle increment idle time
        double timeChange = time - statistics.getServiceSizeChangeTime();
        if(availableServers ==k){
            statistics.incrementIdleTime(timeChange);
        }
        //Else adjust averageServiceSize
        else{
            int currentServiceSize = k - availableServers;
            double averageServiceSize = statistics.getAccumulatedServiceSize();
            statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        }
        //Record time when service size changed
        statistics.setServiceSizeChangeTime(time);

        //Create Module End event
        double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        availableServers--;
    }

    public void freeConnection(){
        double time = DBMS.getClock();
        double timeChange = time - statistics.getServiceSizeChangeTime();
        int currentServiceSize = k - availableServers;
        double averageServiceSize = statistics.getAccumulatedServiceSize();
        statistics.setAccumulatedServiceSize(averageServiceSize + (timeChange*currentServiceSize));
        statistics.setServiceSizeChangeTime(time);
        availableServers++;
    }
}
