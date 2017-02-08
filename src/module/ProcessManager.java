package module;

import query.Query;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class ProcessManager extends Module {

    public ProcessManager(Module next){
        nextModule = next;
    }
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void processExit(Query query) {

    }
}
