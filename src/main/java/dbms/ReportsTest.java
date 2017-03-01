package dbms;

import java.util.*;
import interfaces.SimulationReports;

public class ReportsTest {

    public static void main(String[] args) {
        int iterations = 10;
        Simulator simulator = new Simulator(1500, 15, 3, 2, 1, 450);
        List<SimulatorStatistics> statisticsList = new LinkedList<>();
        for (int i = 0; i < iterations; i++){
            simulator.initializeSimulation();
            while (simulator.isSimulationOver()){
                simulator.processNextEvent();
            }
            statisticsList.add(simulator.getSimulatorStatistics());
        }
        SimulatorStatistics globalStatistics = new SimulatorStatistics(1500, 15, 3, 2, 1, 450, statisticsList);
        SimulationReports reports = new SimulationReports();
        try {
            reports.generateReports(globalStatistics);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }

}
