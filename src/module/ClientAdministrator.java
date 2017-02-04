package module;

import connection.*;

/**
 * Created by Carlos on 03/02/2017.
 */
public class ClientAdministrator extends Module {

    private int k;

    public ClientAdministrator(int maxConnections){
        k = maxConnections;
        moduleNumber = 1;
    }

    public void receiveConnection(Connection connection, double time){
        ConnectionStatistics connectionStatistics = connection.getStatistics();
        connectionStatistics.TimeModule1 = time;
        if (k > 0){
            connectionStatistics.entryTimeModule1 = time;
            double duration = ProbabilityDistributions.Uniform(0.01, 0.05);


        }
    }

    public void endConnection(){

    }


}
