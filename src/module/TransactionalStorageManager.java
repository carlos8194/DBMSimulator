package module;

import query.Query;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class TransactionalStorageManager extends Module {
    private int simultaneousConsultations;



    public TransactionalStorageManager(int p, Module next){
        simultaneousConsultations = p;
        nextModule = next;
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
