package dbms;

import java.util.*;

public class Analisis {

    public static void main(String[] args) throws Exception {


        String[] parameters = {"k","n","p","m"};
        System.out.println("What happens if all modules only have one server?");
        int[] oneServer = {1,1,1,1};
        evaluate(oneServer,15);
        System.out.println("\n\n\nBASELINE");
        int[] baseline = {5,5,5,5};
        double baselineNote = evaluate(baseline,30);

        //
        System.out.println("What parameter influences the performance the most?");
        for (int i = 0; i <4 ; i++) {
            int[] test1 = baseline.clone();
            test1[i] += 2;
            double note = evaluate(test1,30);
            double improvement = 100*((note-baselineNote)/baselineNote);
            System.out.println("Improving parameter "+parameters[i]+" alone, resulted in an improvement of: "+improvement+"%.\n");

        }

        //Algorithm inspired by previous loop
        int[] optimal = greedyAlgorithm(baseline,baselineNote,1,82);
        Simulator simulator = new Simulator(10000,13,9,7,8,30);
        SimulatorStatistics statistics = simulator.runSimulation(15);
        System.out.println("Using parameters given by greedy algorithm produces < 5% average queries timeouts"+ (statistics.getAverageTimeouts()/statistics.getAverageArrivals())*100);


    }

    public static double evaluateConfiguration( int[] config,double timeout) throws Exception {
        Simulator simulator = new Simulator(10000,config[0],config[1],config[2],config[3], timeout);
        SimulatorStatistics statistics = simulator.runSimulation(15);
        double averageArrivals = statistics.getAverageArrivals();
        double queriesProcessedNote = (double) statistics.getAverageQueriesProcessed()/averageArrivals;
        double queryLifetimeNote = 1.0-(statistics.getAverageQueryLifeTime() / timeout);
        double discartedConnectionsNote =1.0 -(statistics.getAverageDiscartedConnections()/averageArrivals);


        //System.out.println("Notes: "+queriesProcessedNote*100+" "+queryLifetimeNote*100+" "+discartedConnectionsNote*100);
        return 100*(0.4*queriesProcessedNote + 0.4*queryLifetimeNote + 0.2*discartedConnectionsNote);


    }
    public static double evaluate(int[] config,double timeout) throws Exception {
        System.out.println("--------------------------------------------");
        System.out.printf("Configuration: %d,%d,%d,%d,Timeout: %f s.\n",config[0],config[1],config[2],config[3],timeout);
        double note = evaluateConfiguration(config,timeout);
        System.out.printf("Note: %f.\n",note);
        System.out.println("--------------------------------------------");
        return note;
    }

    public static int[] greedyAlgorithm(int[]baseline, double baselineNote, int increment, int acceptableNote) throws Exception{
        for (int i = 0; i <4 ; i++) System.out.print(baseline[i]+" ");
        System.out.println(" "+baselineNote);
        //If criteria is met return result.
        if(baselineNote>acceptableNote) {
            System.out.println("Solution Found");
            for (int i = 0; i <4 ; i++) System.out.print(baseline[i]+" ");
            System.out.println(" Note: "+baselineNote);
            return baseline;
        }
        //Calculate next best decision
        Map<Double,int[]> dictionary = new TreeMap<>();
        for (int i = 0; i <4 ; i++) {
            int[] node = baseline.clone();
            node[i] += increment;
            double nodeNote = evaluateConfiguration(node,30);
            dictionary.put(nodeNote,node);

        }
        //Take Best Step
        for(double note:dictionary.keySet()){
            int[] node = dictionary.get(note);
            double improvement = 100*((note-baselineNote)/baselineNote);
            if(improvement>0){
                return greedyAlgorithm(node,note,increment,acceptableNote);
            }
        }
        return null;


    }



}

