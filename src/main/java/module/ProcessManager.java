package module;

import dbms.Simulator;
import query.*;
import utils.ProbabilityDistributions;


import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class ProcessManager extends Module {

    public ProcessManager(Simulator dbms) {
        DBMS = dbms;
        moduleNumber = 1;
        queue = new ArrayDeque<>();
        availableServers = 1;
        moduleCapacity = 1;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }



    @Override
    protected double calculateDuration(Query query) {
        return ProbabilityDistributions.Normal(1.5, 0.1);
    }
}
