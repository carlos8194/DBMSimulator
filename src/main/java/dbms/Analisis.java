package dbms;

import java.util.*;
import interfaces.SimulationReports;

public class ReportsTest {

    public static void main(String[] args) {
        int iterations = 15;
        double totalRunningTime = 15000;
        int k = 15;
        int n = 3;
        int p = 2;
        int m = 1;
        int t = 15;
        Simulator simulator = new Simulator(totalRunningTime, k, n, p, m, t);
        List<SimulatorStatistics> statisticsList = new LinkedList<>();
        for (int i = 0; i < iterations; i++){
            simulator.initializeSimulation();
            while (!simulator.isSimulationOver()){
                simulator.processNextEvent();
            }
            statisticsList.add(simulator.getSimulatorStatistics());
        }
        SimulatorStatistics globalStatistics = new SimulatorStatistics(totalRunningTime, k, n, p, m, t, statisticsList);
        SimulationReports reports = new SimulationReports();
        try {
            reports.generateReports(globalStatistics);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }

}
