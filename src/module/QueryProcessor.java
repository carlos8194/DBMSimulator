package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public  class QueryProcessor extends Module {

    public QueryProcessor(int n, Module next){
        moduleNumber = 2;
        availableServers = n;
        moduleCapacity = n;
        nextModule = next;
        queue = new ArrayDeque<>();
    }

    @Override
    protected double calculateDuration(Query query){
        double x = (Math.random() < 0.7) ? 0.1 : 0.4;
        x += ProbabilityDistributions.Uniform(0, 0.8);
        x += ProbabilityDistributions.Normal(1, 0.5);
        x += ProbabilityDistributions.Exponential(0.7);
        x += (query.getQueryType().readOnly) ? 0.1 : 0.5;
        return x;
    }


}
