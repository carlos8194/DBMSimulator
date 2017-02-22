package dbms;

import module.Module;
import module.ModuleStatistics;
import query.*;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class DBMSStatistics {
    //General(Internal use)
    int numberOfSelects;
    int numberOfUpdates;
    int numberOfJoins;
    int numberOfDDls;
    int totalQueriesProcessed;

    //1.DBMS Parameters.
    double time;
    int k;
    int n;
    int p;
    int m;
    double t;


    //3.Average Queue Size by Module. Contained in moduleStatistics.

    //4.Average Query Lifetime.
    double averageQueryLifeTime;

    //5.Number of discarted Connections.
    int discartedConnections;

    //6.Average time spent on each Module by QueryType.
    double[] averageSelectTimes;
    double[] averageUpdateTimes;
    double[] averageJoinTimes;
    double[] averageDDLTimes;


    //7.Idle time by Module. Contained in ModuleStatistics.


    //8.Module Statistics.
    ModuleStatistics[] moduleStatistics;

    public DBMSStatistics(double time,int k, int n, int p,int m, double t, ModuleStatistics[] moduleStatistics){
        this.time = time;
        this.k = k;
        this.n = n;
        this.p = p;
        this.m = m;
        this.t = t;
        averageSelectTimes = new double[5];
        averageUpdateTimes = new double[5];
        averageJoinTimes = new double[5];
        averageDDLTimes = new double[5];
        this.moduleStatistics = moduleStatistics;


    }

    public void calculateFinalStatistics(){
        for(ModuleStatistics statistics: moduleStatistics){
            statistics.calculateFinalStatistics(time);
        }
        averageQueryLifeTime = averageQueryLifeTime/totalQueriesProcessed;
        for(double time: averageSelectTimes){time = time/numberOfSelects;}
        for(double time: averageDDLTimes){time = time/numberOfDDls;}
        for(double time: averageJoinTimes){time = time/numberOfJoins;}
        for(double time: averageUpdateTimes){time = time/numberOfUpdates;}
    }


    //Running Statistics Methods.
    public void incrementDiscartedConnections(){discartedConnections++;}

    public void processQueryReturn(Query query) {
        averageQueryLifeTime += query.getStatistics().getQueryLifeTime();
        totalQueriesProcessed++;
    }
    public void processModuleEnd(Query query, int moduleNumber) {
        QueryType queryType = query.getQueryType();
        switch (queryType){
            case DDL:
                averageDDLTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfDDls++;
                break;
            case JOIN:
                averageJoinTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfJoins++;
                break;
            case SELECT:
                averageSelectTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfSelects++;
                break;
            case UPDATE:
                averageUpdateTimes[moduleNumber] += query.getStatistics().getModuleTime(moduleNumber);
                numberOfUpdates++;
                break;
        }
    }


    //Interface Statistics.


    //Final Statistics for report.
    //3.Average Queue Size.
    public double getAverageQueueSize(int moduleNumber){
        ModuleStatistics statistics = moduleStatistics[moduleNumber];
        return statistics.getAverageQueueSize();
    }
    //4.Average Query Lifetime
    public double getAverageQueryLifeTime(){
        return averageQueryLifeTime;
    }
    //5.Number of discarted connections.
    public int getNumberOfDiscartedConnections(){
        return discartedConnections;
    }
    //6. Average time spent on each module by queryType
    public double[] getAverageTimesByQueryType(QueryType queryType){
        switch (queryType){
            case DDL:
                return averageDDLTimes;
            case JOIN:
                return averageJoinTimes;
            case SELECT:
                return averageSelectTimes;
            case UPDATE:
                return averageUpdateTimes;
        }
        return null;
    }
    //7.Idle time by Module. Contained in ModuleStatistics.
    public double getModuleIdleTime(int moduleNumber){
        return moduleStatistics[moduleNumber].getIdleTime();
    }

    //8.Module Statistics.
    public ModuleStatistics getModuleStatistics(int moduleNumber){
        return moduleStatistics[moduleNumber];
    }




}
