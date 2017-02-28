package module;

import dbms.Simulator;
import query.*;
import utils.ProbabilityDistributions;


import java.util.ArrayDeque;

/**
 * Module number, it simulates the service of creating a new thread for a query, through a system call. It can only
 * handle a system call at a time, which means that it only has one server.
 */
public class ProcessManager extends Module {

    /**
     * Constructor for the Process Manager module.
     * @param dbms: The simulator object that manages the modules.
     */
    public ProcessManager(Simulator dbms) {
        DBMS = dbms;
        moduleNumber = 1;
        queue = new ArrayDeque<>();
        availableServers = 1;
        moduleCapacity = 1;
        queue = new ArrayDeque<>();
        statistics = new ModuleStatistics(this);
    }

    /**
     * Uses the normal distribution to calculate the time it takes to perform the system call.
     * @param query: The object to which service is going to be given.
     * @return The service time as a double value.
     */
    @Override
    protected double calculateDuration(Query query) {
        return ProbabilityDistributions.Normal(1.5, 0.1);
    }
}
