package module;

import connection.Connection;
import event.Event;

/**
 * Created by Carlos on 03/02/2017.
 */
public abstract class Module {
    protected ModuleStatistics statistics;
    protected int moduleNumber;
    protected Module next;

    public abstract void receiveConnection(Connection connection, double time);
    public abstract void endConnection(Connection connection, double time);


    protected class ModuleStatistics {
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
