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



    /**
     * Created by Carlos on 2/8/2017.
     */
    public static class ModuleStatistics {
        protected int queueClients;
        protected double queueTime;
        protected double idleTime;
        protected double timeService;
        protected int clientsService;
        protected int totalClients;

        public ModuleStatistics(){
            queueClients = 0;
            queueTime = 0;
            idleTime = 0;
            timeService = 0;
            clientsService = 0;
            totalClients = 0;
        }
    }
}
