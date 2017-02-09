package dbms;

import module.ModuleStatistics;

/**
 * Created by Rodrigo on 2/4/2017.
 */
public class DBMSStatistics {
    //General(Internal use)
    int numberOfQueries;
    int numberOfSelects;
    int numberOfUpdates;
    int numberOfJoins;
    int numberOfDDls;

    //1.DBMS Parameters
    int k;
    int n;
    int p;
    int m;
    int t;
    int time;

    //3.Average Queue Size by Module.
    double averageQueueSizeModule1;
    double averageQueueSizeModule2;
    double averageQueueSizeModule3;
    double averageQueueSizeModule4;
    double averageQueueSizeModule5;

    //4.Average Query Lifetime.
    double averageQueryLifeTime;

    //5.Number of discarted Connections.
    int discartedConnections;

    //6.Average time spent on each Module by QueryType.
    double averageSelectModule1Time;
    double averageSelectModule2Time;
    double averageSelectModule3Time;
    double averageSelectModule4Time;
    double averageSelectModule5Time;
    double averageUpdateModule1Time;
    double averageUpdateModule2Time;
    double averageUpdateModule3Time;
    double averageUpdateModule4Time;
    double averageUpdateModule5Time;
    double averageJoinModule1Time;
    double averageJoinModule2Time;
    double averageJoinModule3Time;
    double averageJoinModule4Time;
    double averageJoinModule5Time;
    double averageDDLModule1Time;
    double averageDDLModule2Time;
    double averageDDLModule3Time;
    double averageDDLModule4Time;
    double averageDDLModule5Time;

    //7.Idle time by Module.
    double idleTimeModule1;
    double idleTimeModule2;
    double idleTimeModule3;
    double idleTimeModule4;
    double idleTimeModule5;

    //8.Module Statistics.
    ModuleStatistics module1Statistics;
    ModuleStatistics module2Statistics;
    ModuleStatistics module3Statistics;
    ModuleStatistics module4Statistics;
    ModuleStatistics module5Statistics;


}
