package dbms;
import event.*;
import interfaces.*;
import module.*;
import query.Query;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class Simulator {
    //Simulator variables
    private double clock;

    //Simulation Statistics
    private SimulatorStatistics simulatorStatistics;
    //Event List
    private PriorityQueue<Event> eventList;

    //Modules
    private ClientAdministrator clientAdministrator;
    private ProcessManager processManager;
    private QueryProcessor queryProcessor;
    private TransactionalStorageManager transactionalStorageManager;
    private QueryExecutor queryExecutor;

    //Simulator parameters
    private double totalRunningTime;
    private int concurrentConnections;//Module 0: ClientAdministrator k
    private int availableProcesses;//Module 2: Query Processor n
    private int simultaneousConsultations;//Module 3: Transactional Storage Manager p
    private int parallelStatements;//Module 4: QueryExecutor m
    private double queryTimeoutTime;//Simulator: t
    private boolean delayMode;
    private double delayTime;

    //Interface
    private Interface anInterface;
    private String currentEventAsString;

    public Simulator(double time, int k, int n, int p, int m, double t, Interface anInterface, boolean delayMode, double delayTime){
        //DBMs parameters
        totalRunningTime = time;
        concurrentConnections = k;
        availableProcesses = n;
        simultaneousConsultations = p;
        parallelStatements = m;
        queryTimeoutTime = t;
        this.anInterface = anInterface;
        this.delayMode = delayMode;
        this.delayTime = delayTime;
    }

    public Simulator(double time, int k, int n, int p, int m, double t){
        //DBMs parameters
        totalRunningTime = time;
        concurrentConnections = k;
        availableProcesses = n;
        simultaneousConsultations = p;
        parallelStatements = m;
        queryTimeoutTime = t;
        this.delayMode = false;

    }

    public void addEvent(Event event){
        eventList.add(event);
    }

    public SimulatorStatistics runSimulation()  {
        //Initialize system
        initializeDBMS();
        clock = 0;
        Event firstArrival = new Event(EventType.NEW_QUERY, ProbabilityDistributions.Exponential(35.0/60.0) );
        eventList.add(firstArrival);
        double time = clock;

        //Run simulation
        while(clock < totalRunningTime){
            //Get nextModule event
            Event currentEvent = eventList.poll();
            currentEventAsString = currentEvent.toString();
            //Move clock to event time
            clock = currentEvent.getTime();
            if (clock - time >= delayTime && delayMode) {
                anInterface.updateSecondFrame(simulatorStatistics);
                time = clock;
            }
            //Process Event
            Query query = currentEvent.getQuery();
            switch(currentEvent.getType()){
                case NEW_QUERY: processNewQuery();break;
                case MODULE_END: processModuleEnd(query);break;
                case QUERY_RETURN: processQueryReturn(query);break;
                case QUERY_TIMEOUT: processQueryTimeout(query);break;
            }
        }
        simulatorStatistics.calculateFinalStatistics();
        return simulatorStatistics;
    }

    private void initializeDBMS() {
        //Simulator variables
        eventList= new PriorityQueue<>(Event::compareTo);
        //Modules
        clientAdministrator = new ClientAdministrator(this, concurrentConnections);
        processManager = new ProcessManager(this);
        queryProcessor = new QueryProcessor(this, availableProcesses);
        transactionalStorageManager = new  TransactionalStorageManager(this, simultaneousConsultations);
        queryExecutor = new QueryExecutor(this, parallelStatements);
        clientAdministrator.setNextModule(processManager);
        processManager.setNextModule(queryProcessor);
        queryProcessor.setNextModule(transactionalStorageManager);
        transactionalStorageManager.setNextModule(queryExecutor);
        queryExecutor.setNextModule(clientAdministrator);

        //Statistics
        ModuleStatistics[] moduleStatistics = {
                clientAdministrator.getModuleStatistics(),
                processManager.getModuleStatistics(),
                queryProcessor.getModuleStatistics(),
                transactionalStorageManager.getModuleStatistics(),
                queryExecutor.getModuleStatistics()
        };
        simulatorStatistics = new SimulatorStatistics(totalRunningTime, concurrentConnections, availableProcesses, simultaneousConsultations, parallelStatements, queryTimeoutTime, moduleStatistics);

    }

    private void processQueryTimeout(Query query) {
        query.getCurrentModule().queryTimeout(query);
        clientAdministrator.freeConnection();
    }

    private void processQueryReturn(Query query) {
        simulatorStatistics.processQueryReturn(query);
        eventList.remove(query.getTimeoutEvent());
    }

    private void processModuleEnd(Query query) {
        Module currentModule = query.getCurrentModule();
        int moduleNumber = currentModule.getModuleNumber();
        currentModule.processExit(query);
        simulatorStatistics.processModuleEnd(query, moduleNumber);
    }

    private void processNewQuery() {
        Query query = new Query();
        Event queryTimeOut = new Event(EventType.QUERY_TIMEOUT, clock + queryTimeoutTime, query);
        query.setTimeoutEvent(queryTimeOut);
        Event nextArrival = new Event(EventType.NEW_QUERY, clock + ProbabilityDistributions.Exponential(35.0/60.0) );
        eventList.add(nextArrival);
        eventList.add(queryTimeOut);
        clientAdministrator.processArrival(query);
    }

    public double getClock() {
        return clock;
    }

    public void incrementDiscartedConnections()  {
        simulatorStatistics.incrementDiscartedConnections();
    }

    public String getCurrentEvent(){
        return currentEventAsString;
    }



    
}
