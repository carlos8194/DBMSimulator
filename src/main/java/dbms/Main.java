package dbms;

import interfaces.Interface;
import interfaces.SimulationReports;

import java.util.List;
/**
 * Created by Carlos on 18/02/2017.
 */
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

    }
}
