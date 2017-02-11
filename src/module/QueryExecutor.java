package module;

import event.*;
import query.Query;
import query.QueryStatistics;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class QueryExecutor extends Module {
    private int m;
    private ClientAdministrator administrator;

    public QueryExecutor(int availableProcesses, Module next){
        m = availableProcesses;
        servers = availableProcesses;
        nextModule = next;
        administrator = (ClientAdministrator) next;
    }
    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Query Executor module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule5(time);
        if (servers > 0){
            this.attendQuery(query);
        }
        else {
            queue.add(query);
        }
    }

    @Override
    public void processExit(Query query) {
        System.out.println("Conecction " + query.getID() + " exited Query Executor module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule5( DBMS.getClock() );
        servers++;
        if ( !query.isTimeOut() ){
            administrator.returnQueryResult(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = this.calculateDuration(query);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule5(time);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers--;
    }

    private double calculateDuration(Query query){
        double duration;
        switch ( query.getQueryType() ){
            case DDL:
                duration = 0.5;
                break;
            case UPDATE:
                duration = 1;
                break;
            default:
                duration = Math.pow(query.getBlocks(), 2);
                break;
        }
        return duration;
    }
}
