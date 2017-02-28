package module;

import dbms.Simulator;
import query.*;
import utils.ProbabilityDistributions;

import java.util.PriorityQueue;

/**
 * Module number 3. This module is in charge of accessing data blocks from the DBMS and load them in main memory, this
 * process depends on the module capacity and the query type. This module uses o Priority queue instead of an ordinary
 * one, and the order depends on the query type.
 */
public class TransactionalStorageManager extends Module {

    /**
     * Constructor of a Transactional Storage Manager.
     * @param dbms: The simulator object that manages the modules.
     * @param p: Max number of queries that can be processed at once.
     */
    public TransactionalStorageManager(Simulator dbms, int p){
        DBMS = dbms;
        moduleNumber = 3;
        moduleCapacity = p;
        availableServers = p;
        queue = new PriorityQueue<>(Query::compareTo);
        statistics = new ModuleStatistics(this);
    }

    /**
     * It tells whether or not a query can be attended right away. It depends on the fact that a DDL query could be in
     * the queues top.
     * @param query: The query that could possibly be attended.
     * @return true if the query can be attended, false otherwise.
     */
    @Override
    protected boolean attendImmediately(Query query){
        QueryType type = query.getQueryType();
        QueryType nextType;
        if(queue.peek() != null){
            nextType = queue.peek().getQueryType();
        }
        else{
            nextType = null;
        }
        return  (type == QueryType.DDL && availableServers == moduleCapacity) || (nextType != QueryType.DDL && availableServers > 0);
    }

    /**
     * This method chooses the next query that can be attended. If a DDL is waiting in the top and the available number
     * of servers does not equal module capacity then it can not attend anyone at the moment.
     * @return the next query to be attended.
     */
    @Override
    protected Query chooseNextClient() {
        Query anotherQuery = queue.peek();
        Query returnQuery = null;
        if (anotherQuery != null){
            QueryType type = anotherQuery.getQueryType();
            if (type != QueryType.DDL || availableServers == moduleCapacity){
                returnQuery = queue.poll();
            }
        }
        return returnQuery;
    }

    /**
     * Service time duration, it depends on the number of blocks loaded, and this modules capacity.
     * @param query: The object to which service is going to be given.
     * @return Service time as a double.
     */
    @Override
    protected double calculateDuration(Query query) {
        return moduleCapacity*0.03 + 0.1*this.calculateBlocks(query);
    }

    /**
     * Calculates the number of blocks depending on query type and using the Uniform Distribution.
     * @param query: The query whose blocks are going to be loaded.
     * @return the number of blocks.
     */
    private int calculateBlocks(Query query){
        double B = 0;
        switch (query.getQueryType()){
            case JOIN:
                B = ProbabilityDistributions.Uniform(1,16) + ProbabilityDistributions.Uniform(1,12);
                break;
            case SELECT:
                B = ProbabilityDistributions.Uniform(1,64);
                break;
        }
        int blocks = (int) B;
        query.setBlocks(blocks);
        return blocks;
    }
}
