package module;

import query.Query;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public  class QueryProcessor extends Module {
    private int parallelStatements;

    public QueryProcessor(int m, Module next){
        parallelStatements = m;
        nextModule = next;
    }
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void processExit(Query query) {

    }
}
