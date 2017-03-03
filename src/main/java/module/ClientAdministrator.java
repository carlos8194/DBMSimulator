package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;
import java.util.ArrayDeque;
import dbms.Simulator;
/**
 * Module number 0. A module in charge of establishing a connection with the DBMS and the query. It has a limit with
 * max number of concurrent connections. And it does not have a queue, so when the max number of connections is reached
 * any other entrants are lost.
 */
public class ClientAdministrator extends Module {

    /**
     * Client Administrator constructor.
     * @param dbms: The simulator object that manages the modules.
     * @param k: The max number of concurrent connections.
     */
    public ClientAdministrator(Simulator dbms, int k){
        DBMS = dbms;
        moduleNumber = 0;
        availableServers = k;
        moduleCapacity = k;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    /**
     * Whenever a query reaches the end of the system, this method updates the modules statistics and creates the query
     * return event for the corresponding query.
     * @param query: The query about to leave the system.
     */
    void returnQueryResult(Query query){
        //Calculate
        double time = DBMS.getClock() + (double) query.getBlocks() / 6.0;

        //Adjust Statistics
        query.getStatistics().setSystemExitTime(time);
        statistics.incrementQueriesProcessed();

        //Create timeout event or kill query
        if(time < query.getTimeoutEvent().getTime()) {
            //Create Query Return event
            Event event = new Event(EventType.QUERY_RETURN, time, query);
            DBMS.addEvent(event);
        }
    }

    /**
     * This method chooses the next client to be attended from the queue. Since it does not have one then it will
     * always return null.
     * @return null.
     */
    @Override
    protected Query chooseNextClient() {
        return null;
    }

    /**
     * Uses the Uniform Distribution to calculate the duration it takes to establish connection.
     * @param query: The object to which service is going to be given.
     * @return the service time.
     */
    @Override
    protected double calculateDuration(Query query) {
        return ProbabilityDistributions.Uniform(0.01, 0.05);
    }

    /**
     * Increments the available servers in one. Called by the DBMS whenever a query leaves the system.
     */
    public void freeConnection(){
        availableServers++;
    }
}
