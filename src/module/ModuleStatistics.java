package module;

/**
 * Created by Rodrigo on 2/8/2017.
 */
public class ModuleStatistics {
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
