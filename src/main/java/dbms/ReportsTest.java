package dbms;

import interfaces.Interface;
import interfaces.SimulationReports;
import query.Query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/25/2017.
 */
public class ReportsTest {
    public static void main(String[]args) throws Exception {
        //System.out.println(System.getProperty("user.dir"));
        PriorityQueue<Query> cola = new PriorityQueue<>(Query::compareTo);
        for (int i = 0; i <100; i++) {
            cola.add(new Query());
        }

        for (int i = 0; i <100 ; i++) {
            System.out.println(cola.poll().getQueryType());
        }


    }
}
