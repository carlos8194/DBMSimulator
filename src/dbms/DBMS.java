package dbms;
import event.Event;
import module.*;
import query.Query;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class DBMS {
    //Simulation Statistics
    DBMSStatistics dbmsStatistics;
    //Event List
    private PriorityQueue<Event> eventList;
    //Modules
    private ClientAdministrator clientAdministrator;
    private ProcessManager processManager;
    private QueryProcessor queryProcessor;
    private TransactionalStorageManager transactionalStorageManager;
    private QueryExecutor queryExecutor;

    public DBMS(){
        dbmsStatistics = new DBMSStatistics();
        eventList= new PriorityQueue<>(Event::compareTo);
        clientAdministrator = new ClientAdministrator(0,processManager);
        processManager = new ProcessManager();
        queryProcessor = new QueryProcessor();
        transactionalStorageManager = new TransactionalStorageManager();
        queryExecutor = new QueryExecutor();
    }

    public void addEvent(Event event){
        eventList.add(event);
    }


}
