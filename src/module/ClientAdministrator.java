package module;

import connection.*;
import event.*;



/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {

    private int k;
    private int discardedConnections;

    public ClientAdministrator(int maxConnections, Module next){
        k = maxConnections;
        moduleNumber = 1;
        this.next = next;
        discardedConnections = 0;
    }

    public void receiveConnection(Connection connection, double time){
        System.out.println("Connection entered Client Administrator module");
        ConnectionStatistics connectionStatistics = connection.getStatistics();
        connectionStatistics.TimeModule1 = time;
        if (k > 0){
            connectionStatistics.entryTimeModule1 = time;
            double duration = ProbabilityDistributions.Uniform(0.01, 0.05);
            Event event = new Event(EventType.MODULE_END, time + duration);


        }
        else {
            discardedConnections++;
        }
    }

    public void endConnection(Connection connection, double time){
        System.out.println("Conecction exited Client Administrator module");
        next.receiveConnection(connection,time);
    }


}
