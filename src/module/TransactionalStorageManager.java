package module;

import query.*;
import utils.ProbabilityDistributions;
import event.*;

/**
 * Created by Rodrigo on 2/7/2017.
 */
public class TransactionalStorageManager extends Module {
    private int p;

    public TransactionalStorageManager(int simultaneousConsultations, Module next){
        p = simultaneousConsultations;
        servers = simultaneousConsultations;
        nextModule = next;
    }

    @Override
    public void processArrival(Query query) {
        double time = DBMS.getClock();
        System.out.println("Conecction " + query.getID() + " entered Transactional Storage Manager module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setTimeModule4(time);
        if (servers > 0){
            QueryType type = query.getQueryType();
            if (type != QueryType.DDL ){
                this.attendQuery(query);
            }
            else if (servers == p){
                this.attendQuery(query);
            }
        }
        else {
            queue.add(query);
        }
    }

    @Override
    public void processExit(Query query) {
        System.out.println("Connection " + query.getID() + " exited Transactional Storage Manager module");
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setExitTimeModule4(DBMS.getClock());
        servers = (query.getQueryType() == QueryType.DDL) ? p : servers + 1;
        Query anotherQuery = queue.peek();
        if (anotherQuery != null){
            QueryType type = query.getQueryType();
            if (type != QueryType.DDL ){
                this.attendQuery(anotherQuery);
                queue.poll();
            }
            else if (servers == p){
                this.attendQuery(anotherQuery);
                queue.poll();
            }
        }
        if ( !query.isTimeOut() ) {
            nextModule.processArrival(query);
        }
    }

    @Override
    protected void attendQuery(Query query) {
        double time = DBMS.getClock();
        double duration = p*0.03 + 0.1*this.calculateBlocks(query);
        QueryStatistics queryStatistics = query.getStatistics();
        queryStatistics.setEntryTimeModule4(time);
        Event event = new Event(EventType.MODULE_END, time + duration, query);
        DBMS.addEvent(event);
        servers = (query.getQueryType() == QueryType.DDL) ? 0 : servers - 1;
    }

    private int calculateBlocks(Query query){
        double B = 0;
        switch (query.getQueryType()){
            case JOIN:
                B = ProbabilityDistributions.Uniform(1,16) + ProbabilityDistributions.Uniform(1,12);
                break;
            case SELECT:
                B = ProbabilityDistributions.Uniform(1,64);
                break;
        }
        query.setBlocks( (int) B);
        return (int) B;
    }
}
