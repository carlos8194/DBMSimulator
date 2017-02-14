package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;


import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class ProcessManager extends Module {

    public ProcessManager(Module next) {
        moduleNumber = 1;
        nextModule = next;
        queue = new ArrayDeque<>();
        availableServers = 1;
        moduleCapacity = 1;
        queue = new ArrayDeque<>();
    }



    @Override
    protected double calculateDuration(Query query) {
        return ProbabilityDistributions.Normal(1.5, 0.1);
    }
}
