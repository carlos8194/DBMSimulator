package dbms;
import event.Event;
import event.EventType;
import module.*;
import query.Query;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class DBMS {
    //DBMS variables
    private double clock;

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
    private double totalRunningTime;
    private int concurrentConnections;//Module 1: ClientAdministrator k
    private int availableProcesses;//Module 3: QueryProccesor n
    private int simultaneousConsultations;//Module 4: TranasactionalStorageManager p
    private int parallelStatements;//Module 5: QueryExecutor m
    private double queryTimeoutTime;//DBMS: t



    public DBMS(int time,int k, int n, int p, int m, double t){
        //DBMs parameters
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
        clock = 0;
        Event firstArrival = new Event(EventType.NEW_QUERY, ProbabilityDistributions.Exponential(35) );
        eventList.add(firstArrival);

        //Run simulation
        while(getClock() <totalRunningTime){
            //Get nextModule event
            Event currentEvent = eventList.poll();
            //Move clock to event time
            clock = currentEvent.getTime();
            //Process Event
            Query query = currentEvent.getQuery();
            switch(currentEvent.getType()){
                case NEW_QUERY: processNewQuery();break;
                case MODULE_END: processModuleEnd(query);break;
                case QUERY_RETURN: processQueryReturn(query);break;
                case QUERY_TIMEOUT: processQueryTimeout(query);break;
            }
        }
        dbmsStatistics.calculateFinalStatistics();
        return dbmsStatistics;
    }

    private void initializeDBMS() {
        //DBMS variables
        dbmsStatistics = new DBMSStatistics(totalRunningTime,concurrentConnections,availableProcesses,simultaneousConsultations,parallelStatements,queryTimeoutTime);
        eventList= new PriorityQueue<>(Event::compareTo);
        //Modules
        clientAdministrator = new ClientAdministrator(concurrentConnections, processManager);
        processManager = new ProcessManager(queryProcessor);
        queryProcessor = new QueryProcessor(availableProcesses,transactionalStorageManager);
        transactionalStorageManager = new TransactionalStorageManager(simultaneousConsultations, queryExecutor);
        queryExecutor = new QueryExecutor(parallelStatements, clientAdministrator);
    }

    private void processQueryTimeout(Query query) {
        query.getCurrentModule().queryTimeout(query);
        clientAdministrator.freeConnection();
    }

    private void processQueryReturn(Query query) {
        dbmsStatistics.processQueryReturn(query);
        query.getStatistics().setSystemExitTime(clock);
    }

    private void processModuleEnd(Query query) {
        Module currentModule = query.getCurrentModule();
        int moduleNumber = currentModule.getModuleNumber();
        currentModule.processExit(query);
        dbmsStatistics.processModuleEnd(query,moduleNumber);
    }

    private void processNewQuery() {
        Query query = new Query();
        query.getStatistics().setSystemArrivalTime(clock);
        Event queryTimeOut = new Event(EventType.QUERY_TIMEOUT, queryTimeoutTime, query);
        Event nextArrival = new Event(EventType.NEW_QUERY, ProbabilityDistributions.Exponential(35) );
        eventList.add(nextArrival);
        eventList.add(queryTimeOut);
        clientAdministrator.processArrival(query);
    }

    public double getClock() {
        return clock;
    }
    public void incrementDiscartedConnections(){
        dbmsStatistics.incrementDiscartedConnections();
    }
    
}
