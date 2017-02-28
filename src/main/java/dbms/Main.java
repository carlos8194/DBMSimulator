package dbms;

import interfaces.Interface;
import interfaces.SimulationReports;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Interface anInterface = new Interface();
        anInterface.startFirstFrame();
        boolean ready = false;
        List<SimulatorStatistics> statisticsList = null;
        SimulatorStatistics globalStatistics = null;
        while (!ready) {
            ready = anInterface.simulationHasEnded();
            statisticsList = anInterface.getStatisticsList();
            globalStatistics = anInterface.getGlobalStatistics();
        }
        System.out.println("simulation ended, making reports");


    }
}
