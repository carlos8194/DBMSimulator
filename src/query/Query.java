package query;

/**
 * Created by Carlos and Rodrigo on 03/02/2017.
 */
public class Query implements Comparable<Query> {
    private QueryType queryType;
    private QueryStatistics statistics;

    public Query(double t){
        double random = Math.random();
        if (random < 0.32){
            queryType = QueryType.SELECT;
        }
        else if (random < 0.6){
            queryType = QueryType.UPDATE;
        }
        else if (random < 0.93){
            queryType = QueryType.JOIN;
        }
        else {
            queryType = QueryType.DDL;

        }
    }

    public QueryType getQueryType(){
        return queryType;
    }

    public QueryStatistics getStatistics(){
        return statistics;
    }

    public int compareTo(Query query){
        return queryType.compareTo(query.getQueryType());
    }





}
