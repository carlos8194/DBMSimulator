package dbms;

import java.util.*;
import interfaces.SimulationReports;

public class Analisis {

    public static void main(String[] args) throws Exception {


        //k,n,p,m
        int[] config1 = {1,1,1,1};
        double note1 = evaluateConfiguration(config1,15);
        System.out.printf("Configuration: %d,%d,%d,%d,%ds, Note: %f",config1[0],config1[1],config1[2],config1[3],15, note1);

    }

    public static double evaluateConfiguration( int[] config,double timeout) throws Exception {
        Simulator simulator = new Simulator(10000,config[0],config[1],config[2],config[3], timeout);
        SimulatorStatistics statistics = simulator.runSimulation(15);
        double averageArrivals = statistics.getAverageArrivals();
        System.out.println(averageArrivals);
        double queriesProcessedNote = (double) statistics.getAverageQueriesProcessed()/averageArrivals;
        double queryLifetimeNote = 1.0-(statistics.getAverageQueryLifeTime() / 30);
        double discartedConnectionsNote =1.0 -(statistics.getAverageDiscartedConnections()/averageArrivals);

        SimulationReports reports = new SimulationReports();
        reports.generateReports(statistics);

        System.out.println("notes: "+queriesProcessedNote+" "+queryLifetimeNote+" "+discartedConnectionsNote);
        return 100*(0.4*queriesProcessedNote + 0.4*queryLifetimeNote + 0.2*discartedConnectionsNote);
    }

}
