package module;

import query.Query;

import java.util.ArrayDeque;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class ProcessManager extends Module {

    public ProcessManager(Module next){
        nextModule = next;
        queue = new ArrayDeque<>();
    }
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void processExit(Query query) {


    }

    @Override
    public void queryTimeout(Query query) {

    }
}
