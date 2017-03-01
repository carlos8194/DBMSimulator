package module;

import dbms.Simulator;
import query.Query;

import java.util.ArrayDeque;

/**
 * Module number 4. It is in charge of the process of loading blocks to memory and executing the corresponding algorithm
 * based on the queries content. It has a max "m" module capacity.
 */
public class QueryExecutor extends Module {

    /**
     * Query Executor constructor.
     * @param dbms: The simulator object that manages the modules.
     * @param m: this modules capacity.
     */
    public QueryExecutor(Simulator dbms, int m){
        DBMS = dbms;
        moduleNumber = 4;
        moduleCapacity = m;
        availableServers = m;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    /**
     * It calculates the duration of service based on the the queries type and the number of blocks that are going to
     * be loaded to memory.
     * @param query: The object to which service is going to be given.
     * @return service time duration as a double value.
     */
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
                duration = Math.pow(query.getBlocks(), 2)/1000;
                break;
        }
        return duration;
    }
}
