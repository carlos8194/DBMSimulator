package module;

import event.*;
import query.Query;
import query.QueryStatistics;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private int m;
    private ClientAdministrator administrator;

    public QueryExecutor(int availableProcesses, Module next){
        m = availableProcesses;
        servers = availableProcesses;
        nextModule = next;
        administrator = (ClientAdministrator) next;
    }
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void processExit(Query query) {
        System.out.println("Conecction " + query.getID() + " exited Query Executor module");
        QueryStatistics queryStatistics = query.getStatistics();
        double time = DBMS.getClock();
        queryStatistics.setExitTimeModule5(time);
        if ( !query.isTimeOut() ){
            administrator.returnQueryResult(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {

    }
}
