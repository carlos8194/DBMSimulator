package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;
import java.util.ArrayDeque;
import dbms.Simulator;

/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {

    public ClientAdministrator(Simulator dbms, int k){
        DBMS = dbms;
        moduleNumber = 0;
        availableServers = k;
        moduleCapacity = k;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    public void returnQueryResult(Query query){
        //Calculate
        double time = DBMS.getClock() + (double) query.getBlocks() / 6.0;

        //Adjust Statistics
        query.getStatistics().setSystemExitTime(time);
        statistics.incrementQueriesProcessed();

        //Create Query Return event
        Event event = new Event(EventType.QUERY_RETURN, time, query);
        DBMS.addEvent(event);
        availableServers++;
    }

    @Override
    protected Query chooseNextClient() {
        return null;
    }

    @Override
    protected double calculateDuration(Query query) {
        return ProbabilityDistributions.Uniform(0.01, 0.05);
    }

    public void freeConnection(){
        availableServers++;
    }
}
