package module;

import dbms.Simulator;
import query.Query;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private ClientAdministrator administrator;

    public QueryExecutor(Simulator dbms, int m){
        DBMS = dbms;
        moduleNumber = 4;
        moduleCapacity = m;
        availableServers = m;
        administrator = (ClientAdministrator) nextModule;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    @Override
    protected double calculateDuration(Query query){
        double duration;
        switch ( query.getQueryType() ){
            case DDL:
                duration = 0.5;
                break;
            case UPDATE:
                duration = 1;
                break;
            default:
                duration = Math.pow(query.getBlocks(), 2);
                break;
        }
        return duration;
    }
}
