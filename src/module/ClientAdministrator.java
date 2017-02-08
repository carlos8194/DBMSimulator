package module;

import query.*;


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
        /*
        Lo comente por que borre time como parametro de este metodo.

        System.out.println("Query entered Client Administrator module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.TimeModule1 = time;
        if (k > 0){
            queryStatistics.entryTimeModule1 = time;
            double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
            Event event = new Event(EventType.MODULE_END, time + duration);


        }
        else {
            discardedConnections++;
        }*/
    }

    public void processExit(Query query){
        System.out.println("Conecction exited Client Administrator module");
        nextModule.processArrival(query);
    }

    public void returnQueryResult(){}


}
