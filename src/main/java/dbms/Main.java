package dbms;

import interfaces.Interface;
import interfaces.SimulationReports;

import java.util.*;
/**
 * Created by Carlos on 18/02/2017.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Interface anInterface = new Interface();
        boolean ready = false;
        List<SimulatorStatistics> statisticsList = new LinkedList<>();
        while (!ready) {
            statisticsList = anInterface.getStatisticsList();
            ready = anInterface.simulationHasEnded();
        }
        SimulationReports reports = new SimulationReports();
        reports.generateReports(statisticsList);

    }
}
