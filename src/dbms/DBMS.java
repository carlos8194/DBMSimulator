package dbms;
import event.Event;
import event.EventType;
import module.*;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class DBMS {
    //DBMS variables
    double clock;

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

    //DBMS parameters
    double totalRunningTime;
    private int concurrentConnections;//Module 1: ClientAdministrator k
    private int availableProcesses;//Module 3: QueryProccesor n
    private int simultaneousConsultations;//Module 4: TranasactionalStorageManager p
    private int parallelStatements;//Module 5: QueryExecutor m
    private double queryTimeoutTime;//DBMS: t



    public DBMS(int time,int k, int n, int p, int m, double t){
        //DBms parameters
        totalRunningTime = time;
        concurrentConnections = k;
        availableProcesses = n;
        simultaneousConsultations = p;
        parallelStatements = m;
        queryTimeoutTime = t;
    }

    public void addEvent(Event event){
        eventList.add(event);
    }

    public DBMSStatistics runSimultation(){
        //Initialize system
        initializeDBMS();
        clock = 0.0;
        Event firstArrival = new Event(EventType.NEW_QUERY, ProbabilityDistributions.Exponential(35));
        eventList.add(firstArrival);

        //Run simulation
        while(clock<totalRunningTime){
            //Get nextModule event
            Event currentEvent = eventList.poll();
            //Move clock to event time
            clock = currentEvent.getTime();
            //Process Event
            switch(currentEvent.getType()){
                case NEW_QUERY: processNewQuery();
                case MODULE_END: processModuleEnd();
                case QUERY_RETURN: processQueryReturn();
                case QUERY_TIMEOUT: processQueryTimeout();

            }
        }
        return dbmsStatistics;
    }

    private void initializeDBMS() {
        //DBMS variables
        dbmsStatistics = new DBMSStatistics();
        eventList= new PriorityQueue<>(Event::compareTo);
        //Modules
        clientAdministrator = new ClientAdministrator(concurrentConnections,processManager);
        processManager = new ProcessManager(queryProcessor);
        queryProcessor = new QueryProcessor(availableProcesses,transactionalStorageManager);
        transactionalStorageManager = new TransactionalStorageManager(simultaneousConsultations,queryExecutor);
        queryExecutor = new QueryExecutor(parallelStatements,clientAdministrator);
    }

    private void processQueryTimeout() {
    }

    private void processQueryReturn() {
    }

    private void processModuleEnd() {
    }

    private void processNewQuery() {
    }


}
