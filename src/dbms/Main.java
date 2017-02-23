package dbms;

import interfaces.Interface;

/**
 * Created by Carlos on 18/02/2017.
 */
public class Main {

    public static void main(String[] args){
        /*DBMS dbms = new DBMS(300, 15, 3, 2, 1, 20);
        DBMSStatistics statistics = dbms.runSimulation();
        System.out.println(statistics.getAverageQueryLifeTime());*/
        Interface anInterface = new Interface();
        anInterface.startFirstFrame();

    }
}
