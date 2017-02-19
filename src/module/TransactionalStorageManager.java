package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.PriorityQueue;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class TransactionalStorageManager extends Module {

    public TransactionalStorageManager(int p, Module next){
        moduleNumber = 3;
        moduleCapacity = p;
        availableServers = p;
        nextModule = next;
        queue = new PriorityQueue<>(Query::compareTo);
    }



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
        if((type == QueryType.DDL && availableServers == moduleCapacity) || (nextType != QueryType.DDL && availableServers > 0)){
            return true;
        }
        return false;

    }

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


    @Override
    protected double calculateDuration(Query query) {
        return moduleCapacity*0.03 + 0.1*this.calculateBlocks(query);
    }

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
