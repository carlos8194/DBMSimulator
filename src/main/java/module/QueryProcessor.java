package module;

import dbms.Simulator;
import query.*;
import utils.ProbabilityDistributions;

import java.util.ArrayDeque;

/**
 *Module number 2. It represents the process of validations and verifications that come through different stages.
 * It has a max number of servers, defined as a parameter.
 */
public  class QueryProcessor extends Module {

    /**
     * Query Processor constructor
     * @param dbms: The simulator object that manages the modules.
     * @param n: max number of servers in this module.
     */
    public QueryProcessor(Simulator dbms, int n){
        DBMS = dbms;
        moduleNumber = 2;
        availableServers = n;
        moduleCapacity = n;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    /**
     * Calculates service time as a sum of the time it takes to perform each of the stages.
     * @param query: The object to which service is going to be given.
     * @return Service time as a double.
     */
    @Override
    protected double calculateDuration(Query query){
        double x = (Math.random() < 0.7) ? 0.1 : 0.4; //Lexical validation
        x += ProbabilityDistributions.Uniform(0, 0.8); //Syntax validation.
        x += ProbabilityDistributions.Normal(1, 0.5); //Semantic validation.
        x += ProbabilityDistributions.Exponential((0.7)); //Permission verification.
        x += (query.getQueryType().readOnly) ? 0.1 : 0.5; //Optimization process.
        return x;
    }


}
