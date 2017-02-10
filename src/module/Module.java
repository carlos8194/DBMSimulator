package module;

import query.*;
import dbms.DBMS;

import java.util.Queue;

/**
 * Created by Carlos on 03/02/2017.
 */
public abstract class Module {
    protected DBMS DBMS;
    protected ModuleStatistics statistics;
    protected Module nextModule;
    protected Queue<Query> queue;

    public abstract void processArrival(Query query);
    public abstract void processExit(Query query);
    public abstract void queryTimeout(Query query);

}
