package query;

import event.Event;
import module.Module;

/**
 * This class implements the connections used in the simulation.
 * A query most importantly contains a QueryType, a timeout event and some statistics asociated to it.
 * The timeout event is deleted if the query is able to go through all the modules before the timeout time.
 */
public class Query implements Comparable<Query> {
    private static int queryNumber = 1;

    private int queryID;
    private Module currentModule;
    private boolean currentlyInQueue;
    private QueryType queryType;
    private QueryStatistics statistics;
    private boolean timeOut;
    private int blocks;
    private Event timeoutEvent;

    /**
     * The constructor method for Query creates a query and gives it a QueryType with the appropiate probabilities for
     * each queryType.
     */
    public Query(){
        double random = Math.random();
        if (random < 0.32){
            setQueryType(QueryType.SELECT);
        }
        else if (random < 0.6){
            setQueryType(QueryType.UPDATE);
        }
        else if (random < 0.93){
            setQueryType(QueryType.JOIN);
        }
        else {
            setQueryType(QueryType.DDL);

        }
        timeOut = false;
        queryID = queryNumber;
        queryNumber++;
        statistics = new QueryStatistics(this);
        currentlyInQueue = false;
    }


    /**
     * Implementation of the Comparable method for queries, it simply compares this query to another one by the order
     * specified in the enum class QueryType: DDL,UPDATE,JOIN,SELECT.
     * @param query
     * @return 1 if this query has higher priority, 0 if it has the same priority, -1 if this query has lower priority.
     */
    public int compareTo(Query query){
        return getQueryType().compareTo(query.getQueryType());
    }

    /**
     * Returns this query unique identifier
     * @return queryID
     */
    public int getID(){
        return queryID;
    }

    /**
     * Returns the QueryType of this query.
     * @return queryType
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * Sets the QueryType of this query to the one specified in the parameter.
     * @param queryType
     */
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    /**
     * Returns the statistics asocciated to this query. Containing entry and exit times to each module and the DBMS.
     * @return statistics.
     */
    public QueryStatistics getStatistics(){
        return statistics;
    }

    /**
     * Returns true if this query's time is up, indicating not to pass it to the next module.
     * @return boolean
     */
    public boolean isTimeOut(){
        return timeOut;
    }

    /**
     * Sets the parameter timeOut to true if this query's time is up, indicating not to pass it to the next module.
     * @param timeOut
     */
    public void setTimeOut(boolean timeOut){
        this.timeOut = timeOut;
    }

    /**
     * Returns true if the query is currently in a module's queue.
     * @return boolean
     */
    public boolean isCurrentlyInQueue(){
        return currentlyInQueue;
    }

    /**
     * Sets the currentModule field to the Module received as a parameter.
     * @param module module where the query is currently located.
     */
    public void setCurrentModule(Module module){
        currentModule = module;
    }

    /**
     * Returns the module in which the query is currently locate.
     * @return Module at which this query is currently located.
     */
    public Module getCurrentModule(){
        return currentModule;
    }

    /**
     * Compares this query object to another and returns true if they are the same, meaning its of class Query
     * and it has the same queryID.
     * @param o another object.
     * @return true if the object received as parameter is the same as this object.
     */
    public boolean equals(Object o){
        return queryID == o.hashCode();
    }

    /**
     * Overwrites the HashCode of this class with the unique query identifier.
     * @return unique query identifier.
     */
    public int hashCode(){
        return queryID;
    }


    /**
     * Returns the number of blocks retrieved from disk by this query.
     * @return blocks asocciated to this query
     */
    public int getBlocks() {
        return blocks;
    }

    /**
     * Sets the number of blocks retrieved from disk by this query.
     * @param blocks
     */
    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    /**
     * Sets currentlyInQueue true if the query is currently in a module's queue.
     * @param currentlyInQueue boolean
     */
    public void setCurrentlyInQueue(boolean currentlyInQueue) {
        this.currentlyInQueue = currentlyInQueue;
    }

    /**
     * Sets the timeoutEvent asocciated with this query, which is t seconds after entering the system.
     * @param timeoutEvent: event that would kill this query.
     */
    public void setTimeoutEvent(Event timeoutEvent) {
        this.timeoutEvent = timeoutEvent;
    }

    /**
     * Returns this queryTimeout event so that the DBMS system can remove it from the event list if the query exits
     * succesfully the system.
     * @return Event event that would kill the query.
     */
    public Event getTimeoutEvent() {
        return timeoutEvent;
    }
}
