import query.Query;

import java.util.*;


/**
 * Created by Carlos on 01/03/2017.
 */
public class Test {

    public static void main(String[] args) {
        int DDLs = 0; int JOINs = 0; int SELECTs = 0; int UPDATEs = 0;
        Queue<Query> queue = new PriorityQueue<>(Query::compareTo);
        for (int i = 0; i < 100; i++) {
            Query query = new Query();
            switch (query.getQueryType()) {
                case DDL: DDLs++; break;
                case JOIN: JOINs++; break;
                case SELECT: SELECTs++; break;
                case UPDATE: UPDATEs++; break;
            }
            queue.add(query);
        }
        //Print the number of queries of each Query type.
        System.out.println("We have " + DDLs + "DDLs");
        System.out.println("We have " + JOINs + "JOINs");
        System.out.println("We have " + SELECTs + "SELECTs");
        System.out.println("We have " + UPDATEs + "UPDATEs");
        System.out.println();

        while (!queue.isEmpty()){
            System.out.println(queue.poll().getQueryType());//This lets us test the correct order between Query types
            //the Priority Queue.
        }


    }
}
