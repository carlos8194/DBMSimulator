package query;

import module.Module;

/**
 * Created by Carlos and Rodrigo on 03/02/2017.
 */
public class Query implements Comparable<Query> {
    //Es necesario que sea comparable? no se comparan solo eventos?
    private int queryID;
    private Module currentModule;
    private boolean currentlyInQueue;
    private QueryType queryType;
    private QueryStatistics statistics;

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
    }



    public int compareTo(Query query){
        return getQueryType().compareTo(query.getQueryType());
    }


    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }
}
