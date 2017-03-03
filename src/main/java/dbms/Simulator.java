package dbms;
import event.*;
import module.*;
import query.Query;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * This is the most important class of the project, it holds the logic of the simulation, and interacts with all
 * modules, entering queries and the interface.
 */
public class Simulator {
    //Simulator variables
    private double clock;

    private SimulatorStatistics simulatorStatistics;//Simulation Statistics
    private PriorityQueue<Event> eventList;//Event List

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

    //A String representation of the current event, used to notify the interface of what is currently happening.
    private String currentEventAsString;
    private boolean simulationIsOver;

    /**
     * Simulator constructor.
     * @param time: total running time per iteration.
     * @param k: Servers in the Client Administrator module.
     * @param n: Servers in the Query Processor module.
     * @param p: Servers in the Transactional Storage Manager module.
     * @param m: Servers in the Query Executor module.
     * @param t: Time out per query.
     */
    public Simulator(double time, int k, int n, int p, int m, double t){
        //DBMs parameters
        totalRunningTime = time;
        concurrentConnections = k;
        availableProcesses = n;
        simultaneousConsultations = p;
        parallelStatements = m;
        queryTimeoutTime = t;
    }

    /**
     * This method adds an event to the Simulators event queue. Modules need as they process queries.
     * @param event: The event to be added.
     */
    public void addEvent(Event event){
        eventList.add(event);
    }

    /**
     * This method runs a complete iteration of the simulation, and at the same time creates and updates at ruuning time
     * a Simulator Statistics object that holds all the statistics of the iteration, including the one from queries and
     * the modules.
     * @return A SimulatorStatistics object, that holds all statistics of the iteration.
     */
    public void initializeSimulation() {
        //Initialize system
        initializeDBMS();
        clock = 0;
        Event firstArrival = new Event(EventType.NEW_QUERY, ProbabilityDistributions.Exponential(35.0/60.0) );
        eventList.add(firstArrival);
    }

    /**
     * This method enlarges the constructor, by initializing the remaining attributes of the Simulator.
     */
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
        simulationIsOver = false;

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

    /**
     * This method manages the TIME_OUT event, by informing the corresponding module that the query has no time left.
     * @param query: The object whose time is over.
     */
    private void processQueryTimeout(Query query) {
        if (!query.isOutOfSystem()) {
            query.getCurrentModule().queryTimeout(query);
            clientAdministrator.freeConnection();
        }
    }

    /**
     * This method manages the QUERY_RETURN event, it updates statistics with the ones from the query, and removes
     * the time out event from the corresponding query.
     * @param query: The object about to leave the system.
     */
    private void processQueryReturn(Query query) {
        clientAdministrator.freeConnection();
        simulatorStatistics.processQueryReturn(query);
        query.setOutOfSystem(true);
    }

    /**
     * This method manages the MODULE_END event by telling the module that a query is done with its service and by
     * updating the corresponding statistics.
     * @param query: The query that is about to leave a module.
     */
    private void processModuleEnd(Query query) {
        Module currentModule = query.getCurrentModule();
        int moduleNumber = currentModule.getModuleNumber();
        currentModule.processExit(query);
        simulatorStatistics.processModuleEnd(query, moduleNumber);
    }

    /**
     * This method manages the NEW_QUERY event, by creating a query sending it to the Client Administrator and creating
     * the next NEW_QUERY event, which is added to the queue, as well as the TIME_OUT event of the just created query.
     */
    private void processNewQuery() {
        if(clientAdministrator.getOccupiedServers()==concurrentConnections) this.incrementDiscardedConnections();
        else {
            Query query = new Query();
            Event queryTimeOut = new Event(EventType.QUERY_TIMEOUT, clock + queryTimeoutTime, query);
            query.setTimeoutEvent(queryTimeOut);

            eventList.add(queryTimeOut);
            clientAdministrator.processArrival(query);
        }
        Event nextArrival = new Event(EventType.NEW_QUERY, clock + ProbabilityDistributions.Exponential(35.0 / 60.0));
        eventList.add(nextArrival);
    }

    /**
     * Getter method for the current clock value. Modules need to know it to create new events.
     * @return the current clock value.
     */
    public double getClock() {
        return clock;
    }

    /**
     * This method calls the SimulatorStatistics of this and tell it to increase by one the number of discarded
     * connections.
     */
    public void incrementDiscartedConnections()  {
        simulatorStatistics.incrementDiscartedConnections();
    }

    /**
     * Getter method for the event being processed currently.
     * @return A String representation of the current event.
     */
    public String getCurrentEvent(){
        return currentEventAsString;
    }

    /**
     * This method processes a single event, but if total running time has expired then it calculates final statistics
     * and sets the corresponding flag.
     */
    public void processNextEvent() {
        if (clock < totalRunningTime) {
            //Get nextModule event
            Event currentEvent = eventList.poll();
            currentEventAsString = currentEvent.toString();
            //Move clock to event time
            clock = currentEvent.getTime();
            //Verify if a notification to the interface should be sent
            //Process Event
            Query query = currentEvent.getQuery();
            switch (currentEvent.getType()) {
                case NEW_QUERY:
                    processNewQuery();
                    break;
                case MODULE_END:
                    processModuleEnd(query);
                    break;
                case QUERY_RETURN:
                    processQueryReturn(query);
                    break;
                case QUERY_TIMEOUT:
                    processQueryTimeout(query);
                    break;
            }
        } else {
            simulatorStatistics.calculateFinalStatistics();
            simulationIsOver = true;
        }
    }

    /**
     * This method tells whether if the simulation has ended or not, the interface needs to know if this has happened
     * or not.
     * @return a boolean, true if running time has expired, false otherwise.
     */
    public boolean isSimulationOver() {
        return simulationIsOver;
    }

    /**
     * Getter method for the simulator statistics.
     * @return SimulatorStatistics object.
     */
    public SimulatorStatistics getSimulatorStatistics() {
        return simulatorStatistics;
    }
}
