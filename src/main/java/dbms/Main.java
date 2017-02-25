package dbms;

import interfaces.Interface;

import java.util.List;

/**
 * Created by Carlos on 18/02/2017.
 */
public class Main {

    public static void main(String[] args){
        Interface anInterface = new Interface();
        List<SimulatorStatistics> statisticsList = anInterface.getStatisticsList();

    }
}
