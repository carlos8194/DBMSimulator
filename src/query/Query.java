package query;

import module.Module;

/**
 * Created by Carlos and Rodrigo on 03/02/2017.
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



    public int compareTo(Query query){
        return getQueryType().compareTo(query.getQueryType());
    }

    public int getID(){
        return queryID;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

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
}
