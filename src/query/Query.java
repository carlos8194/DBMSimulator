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

    public Query(double t){
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
        statistics = new QueryStatistics();
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

    public void setCurrentModule(Module module){
        currentModule = module;
    }

    public Module getCurrentModule(){
        return currentModule;
    }

    public boolean equals(Object o){
        boolean comparison;
        if (! (o instanceof Query) ){
            comparison = false;
        }
        else {
            Query query = (Query) o;
            comparison = (queryID == query.queryID);
        }
        return comparison;
    }

    public int hashCode(){
        return queryID;
    }
}
