package module;

import query.Query;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private int availableProcesses;

    public QueryExecutor(int n, Module next){
        availableProcesses = n;
        nextModule = next;
    }
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void processExit(Query query) {
        super.processExit(query);

    }
}
