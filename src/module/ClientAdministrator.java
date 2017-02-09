package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;


/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {

    private int k;
    private int discardedConnections;

    public ClientAdministrator(int concurrentConnections, Module next){
        k = concurrentConnections;
        this.nextModule = next;
        discardedConnections = 0;
    }

    public void processArrival(Query query){
        double time = DBMS.clock;
        System.out.println("Query entered Client Administrator module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.TimeModule1 = time;
        if (k > 0){
            queryStatistics.entryTimeModule1 = time;
            double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
            Event event = new Event(EventType.MODULE_END, time + duration);
            DBMS.addEvent(event);
        }
        else {
            discardedConnections++;
        }
    }

    @Override
    public void processExit(Query query) {
        super.processExit(query);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.exitTimeModule1 = DBMS.clock;
    }

    public void returnQueryResult(Query query, int R){

    }


}
