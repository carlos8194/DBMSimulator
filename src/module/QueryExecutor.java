package module;

import query.Query;
import query.QueryStatistics;

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
        System.out.println("Conecction " + query.getID() + " exited Query Executor module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule5(DBMS.getClock());
        if (!query.isTimeOut() ) {
            ClientAdministrator module1 = (ClientAdministrator) nextModule;
            module1.returnQueryResult(query, 0); // cambiar el 0 por el numero de bloques
        }
    }

    @Override
    public void queryTimeout(Query query) {

    }
}
