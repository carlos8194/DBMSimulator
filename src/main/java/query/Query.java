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
     * @return
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

    public boolean isTimeOut(){
        return timeOut;
    }

    public void setTimeOut(boolean timeOut){
        this.timeOut = timeOut;
    }

    public boolean isCurrentlyInQueue(){
        return currentlyInQueue;
    }

    public void setCurrentModule(Module module){
        currentModule = module;
    }

    public Module getCurrentModule(){
        return currentModule;
    }

    public boolean equals(Object o){
        return queryID == o.hashCode();
    }

    public int hashCode(){
        return queryID;
    }


    public int getBlocks() {
        return blocks;
    }

    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    public void setCurrentlyInQueue(boolean currentlyInQueue) {
        this.currentlyInQueue = currentlyInQueue;
    }

    public void setTimeoutEvent(Event timeoutEvent) {
        this.timeoutEvent = timeoutEvent;
    }

    public Event getTimeoutEvent() {
        return timeoutEvent;
    }
}
