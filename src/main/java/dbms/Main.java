package dbms;

import interfaces.Interface;
import java.util.*;
/**
 * Created by Carlos on 18/02/2017.
 */
public class Main {

    public static void main(String[] args){
        Interface anInterface = new Interface();
        boolean ready = false;
        List<SimulatorStatistics> statisticsList = new LinkedList<>();
        while (!ready) {
            statisticsList = anInterface.getStatisticsList();
            ready = anInterface.simulationHasEnded();
        }

    }
}
