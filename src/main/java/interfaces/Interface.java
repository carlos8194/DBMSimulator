package interfaces;

import dbms.*;
import module.Module;
import module.ModuleStatistics;
import query.QueryType;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Interface {

    private JFrame firstFrame;//A frame that shows some boxes for the user to write information.
    private JFrame secondFrame;//A frame that shows the development of the simulation in delay mode.
    private JFrame thirdFrame;//A frame that shows information about an iteration when it is over.

    //Simulator parameters
    private int iterations;//Number of iterations.
    private boolean delay;//A boolean telling whether the simulation will display in delay mode or not.
    private double delayTime;//Delay time in seconds.
    private double maxTime;//Total running time of each iteration.
    private int k;//Module capacity of Client Administrator module.
    private int n;//Module capacity of the Query Processor module.
    private int p;//Module capacity of the Transactional Storage Manager module.
    private int m;//Module capacity of the Query Executor module.
    private double t;//Query time out.

    private static int iterationNumber;//Current iteration number.
    private boolean simulationEnded;//A boolean telling if the simulation has ended or not.
    private Simulator simulator;//Pointer to the Simulator object.
    private Map<String, JLabel> labelMap;//A map to keep access to the labels in the second frame.
    private Map<String, JLabel> otherMap;//A map to keep access to the labels in the third frame.
    private List<SimulatorStatistics> statisticsList;//A pointer to the list of statistics gathered by this interface.
    private SimulatorStatistics globalStatistics;//A pointer to the global statistics of the simulation.
    private Timer timer;//A timer to manage the delay time events.

    /**
     * Interface constructor.
     */
    public Interface(){
        labelMap = new HashMap<>();
        otherMap = new HashMap<>();
        statisticsList = new LinkedList<>();
        iterationNumber = 1;
        simulationEnded = false;
    }

    /**
     * This method initializes the first frame, adds all the necessary components and displays it for the user.
     */
    public void startFirstFrame(){
        firstFrame = new JFrame("DBMS Simulator configuration.");
        firstFrame.setLocation(200, 300);
        firstFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        firstFrame.add(panel);

        JMenuBar menuBar = new JMenuBar();
        firstFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenu infoMenu = new JMenu("Info");
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exit);

        JMenuItem info = new JMenuItem("Info");
        info.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Version 1.1\nCreated by Carlos Luis Mellado Xatruch.\nRodrigo Acuña Fernandez");
            }
        });
        infoMenu.add(info);

        JLabel label = new JLabel("Please fill up the following parameters.");
        JTextField iterationsText = new JTextField(5);
        JTextField maxTimeText = new JTextField(5);
        JTextField delayTimeText = new JTextField(5);
        JTextField kText = new JTextField(5);
        JTextField nText = new JTextField(5);
        JTextField pText = new JTextField(5);
        JTextField mText = new JTextField(5);
        JTextField tText = new JTextField(5);

        JButton button = new JButton("Start simulation");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    iterations = Integer.parseInt(iterationsText.getText());
                    maxTime = Double.parseDouble(maxTimeText.getText());
                    delayTime = (delay) ? Double.parseDouble(delayTimeText.getText()) : 0;
                    k = Integer.parseInt(kText.getText());
                    n = Integer.parseInt(nText.getText());
                    p = Integer.parseInt(pText.getText());
                    m = Integer.parseInt(mText.getText());
                    t = Double.parseDouble(tText.getText());
                    hideFirstFrame();
                    initializeSimulator();
                    startThirdFrame();
                    if (delay) {
                        startSecondFrame();
                        initializeTimer();
                        runSimulationWithDelay();
                    }
                    else runSimulation();
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Please write the numbers properly.");
                }
            }
        });
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout());
        p1.add(new JLabel("Number of iterations"));
        p1.add(iterationsText);

        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout());
        p2.add(new JLabel("Max time per iteration"));
        p2.add(maxTimeText);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout());
        p3.add(new JLabel("Delay time"));
        p3.add(delayTimeText);

        JPanel p4 = new JPanel();
        p4.setLayout(new FlowLayout());
        p4.add(new JLabel("k"));
        p4.add(kText);

        JPanel p5 = new JPanel();
        p5.setLayout(new FlowLayout());
        p5.add(new JLabel("n"));
        p5.add(nText);

        JPanel p6 = new JPanel();
        p6.setLayout(new FlowLayout());
        p6.add(new JLabel("p"));
        p6.add(pText);

        JPanel p7 = new JPanel();
        p7.setLayout(new FlowLayout());
        p7.add(new JLabel("m"));
        p7.add(mText);

        JPanel p8 = new JPanel();
        p8.setLayout(new FlowLayout());
        p8.add(new JLabel("t"));
        p8.add(tText);

        JPanel p9 = new JPanel();
        p9.setLayout(new FlowLayout());
        p9.add(new JLabel("Delay mode"));
        JToggleButton toggleButton = new JToggleButton("OFF");
        toggleButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                delay = (ev.getStateChange() == ItemEvent.SELECTED);
                if(delay) toggleButton.setText("ON");
                else toggleButton.setText("OFF");
            }
        });
        p9.add(toggleButton);

        panel.add(label);
        panel.add(p1);
        panel.add(p2);
        panel.add(p3);
        panel.add(p4);
        panel.add(p5);
        panel.add(p6);
        panel.add(p7);
        panel.add(p8);
        panel.add(p9);
        panel.add(button);

        firstFrame.pack();
        this.showFirstFrame();
    }

    /**
     * Initialize the simulator object and its attributes.
     */
    private void initializeSimulator(){
        simulator = new Simulator(maxTime, k, n, p, m, t);
        simulator.initializeSimulation();
    }

    /**
     * Initializes the timer object and defines its Action Listener object.
     */
    private void initializeTimer(){
        int time = (int) delayTime*1000;
        timer = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                runSimulationWithDelay();
            }
        });
    }

    /**
     * Shows the first frame.
     */
    private void showFirstFrame(){
        firstFrame.setVisible(true);
    }

    /**
     * Shows the second frame.
     */
    private void showSecondFrame(){
        secondFrame.setVisible(true);
    }

    /**
     * Initializes the second frame, adds all the necessary components, and adds most of them to the map.
     */
    private void startSecondFrame()  {
        secondFrame = new JFrame("DBMS Simulator running...");
        secondFrame.setLocation(200, 300);
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        secondFrame.setLayout(new BorderLayout());

        //Simulation parameters
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.add(new JLabel("Iterations: "+iterations+"   total time: "+maxTime+"   k: "+k+"   m: "+m+"   n: "+n+"   p: "+p+"   t: "+t));

        //Simulator variables
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        JLabel clockLabel = new JLabel("  Clock: 0.0  "); labelMap.put("clock", clockLabel);
        JLabel eventLabel = new JLabel("  Current Event:   "); labelMap.put("event", eventLabel);
        JLabel discardedConnectionsLabel = new JLabel("  Discarded Connections: 0  "); labelMap.put("discardedConnections", discardedConnectionsLabel);
        JLabel iterationNumberLabel = new JLabel("  Iteration: " + iterationNumber + "  ");  labelMap.put("iterations", iterationNumberLabel);
        southPanel.add(clockLabel); southPanel.add(eventLabel); southPanel.add(discardedConnectionsLabel);
        southPanel.add(iterationNumberLabel);

        //A grid with some of the modules current data.
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2,3,10,20));

        JPanel mod0Panel = new JPanel();
        mod0Panel.setLayout(new BoxLayout(mod0Panel, BoxLayout.PAGE_AXIS));
        String serverState = "  Ocuppied Servers: 0  ";
        String queueSize = "  Queue Size: 0  ";
        String servedClients = "  Served Clients: 0  ";
        mod0Panel.add(new JLabel("  Client Administrator:  "));
        JLabel mod0ServerState = new JLabel(serverState); labelMap.put("mod0Server", mod0ServerState);
        JLabel mod0QueueSize = new JLabel(queueSize); labelMap.put("mod0Queue", mod0QueueSize);
        JLabel mod0ServedClients = new JLabel(servedClients); labelMap.put("mod0Clients", mod0ServedClients);
        mod0Panel.add(mod0ServerState); mod0Panel.add(mod0QueueSize); mod0Panel.add(mod0ServedClients);

        JPanel mod1Panel = new JPanel();
        mod1Panel.setLayout(new BoxLayout(mod1Panel, BoxLayout.PAGE_AXIS));
        mod1Panel.add(new JLabel("  Process Manager:  "));
        JLabel mod1ServerState = new JLabel(serverState); labelMap.put("mod1Server", mod1ServerState);
        JLabel mod1QueueSize = new JLabel(queueSize); labelMap.put("mod1Queue", mod1QueueSize);
        JLabel mod1ServedClients = new JLabel(servedClients); labelMap.put("mod1Clients", mod1ServedClients);
        mod1Panel.add(mod1ServerState); mod1Panel.add(mod1QueueSize); mod1Panel.add(mod1ServedClients);

        JPanel mod2Panel = new JPanel();
        mod2Panel.setLayout(new BoxLayout(mod2Panel, BoxLayout.PAGE_AXIS));
        mod2Panel.add(new JLabel("  Query Processor:  "));
        JLabel mod2ServerState = new JLabel(serverState); labelMap.put("mod2Server", mod2ServerState);
        JLabel mod2QueueSize = new JLabel(queueSize); labelMap.put("mod2Queue", mod2QueueSize);
        JLabel mod2ServedClients = new JLabel(servedClients); labelMap.put("mod2Clients", mod2ServedClients);
        mod2Panel.add(mod2ServerState); mod2Panel.add(mod2QueueSize); mod2Panel.add(mod2ServedClients);

        JPanel mod3Panel = new JPanel();
        mod3Panel.setLayout(new BoxLayout(mod3Panel, BoxLayout.PAGE_AXIS));
        mod3Panel.add(new JLabel("  Transactional Storage Manager:  "));
        JLabel mod3ServerState = new JLabel(serverState); labelMap.put("mod3Server", mod3ServerState);
        JLabel mod3QueueSize = new JLabel(queueSize); labelMap.put("mod3Queue", mod3QueueSize);
        JLabel mod3ServedClients = new JLabel(servedClients); labelMap.put("mod3Clients", mod3ServedClients);
        mod3Panel.add(mod3ServerState); mod3Panel.add(mod3QueueSize); mod3Panel.add(mod3ServedClients);

        JPanel mod4Panel = new JPanel();
        mod4Panel.setLayout(new BoxLayout(mod4Panel, BoxLayout.PAGE_AXIS));
        mod4Panel.add(new JLabel("  Query Executor:  "));
        JLabel mod4ServerState = new JLabel(serverState); labelMap.put("mod4Server", mod4ServerState);
        JLabel mod4QueueSize = new JLabel(queueSize); labelMap.put("mod4Queue", mod4QueueSize);
        JLabel mod4ServedClients = new JLabel(servedClients); labelMap.put("mod4Clients", mod4ServedClients);
        mod4Panel.add(mod4ServerState); mod4Panel.add(mod4QueueSize); mod4Panel.add(mod4ServedClients);

        centerPanel.add(mod0Panel); centerPanel.add(mod1Panel); centerPanel.add(mod2Panel);
        centerPanel.add(mod3Panel); centerPanel.add(mod4Panel);

        secondFrame.add(northPanel, BorderLayout.NORTH);
        secondFrame.add(centerPanel, BorderLayout.CENTER);
        secondFrame.add(southPanel, BorderLayout.SOUTH);
        secondFrame.pack();
    }

    /**
     * Simulation running algorithm in delay mode, processes one event at a time and updates the second frame.
     * If the simulation is over then it displays the iteration end statistics which come in the third frame.
     */
    private void runSimulationWithDelay(){
        this.hideThirdFrame();
        this.showSecondFrame();
        simulator.processNextEvent();
        this.updateSecondFrame(simulator.getSimulatorStatistics());

        if (simulator.isSimulationOver()) {
            SimulatorStatistics statistics = simulator.getSimulatorStatistics();
            statisticsList.add(statistics);
            this.updateThirdFrame(statistics, iterationNumber + "");
            this.hideSecondFrame();
            this.showThirdFrame();
            iterationNumber++;
        }
        else timer.restart();
    }

    /**
     * Simulation algorithm when delay mode is off, it runs all events at once, updates the third frame and then shows
     * iteration end statistics.
     */
    private void runSimulation(){
        this.hideThirdFrame();
        while ( !simulator.isSimulationOver() ){
            simulator.processNextEvent();
        }
        SimulatorStatistics statistics = simulator.getSimulatorStatistics();
        statisticsList.add(statistics);
        this.updateThirdFrame(statistics, iterationNumber + "");
        this.showThirdFrame();
        iterationNumber++;
    }

    /**
     * Updates the second frame using the current statistics found in the simulator.
     * @param statistics: The SimulatorStatistics object used to update each label.
     */
    public void updateSecondFrame(SimulatorStatistics statistics){
        this.hideSecondFrame();
        this.setNewText(labelMap.get("clock"), simulator.getClock()+"");
        this.setNewText(labelMap.get("discardedConnections"), statistics.getNumberOfDiscartedConnections()+"");
        this.setNewText(labelMap.get("event"), simulator.getCurrentEvent());

        for (int j = 0; j < 5; j++){
            ModuleStatistics moduleStatistics = statistics.getModuleStatistics(j);
            Module module = moduleStatistics.getModule();
            this.setNewText(labelMap.get("mod"+j+"Server"), module.getOccupiedServers()+"");
            this.setNewText(labelMap.get("mod"+j+"Queue"), module.getQueueSize()+"");
            this.setNewText(labelMap.get("mod"+j+"Clients"), moduleStatistics.getQueriesProcessed()+"");
        }
        secondFrame.pack();
        this.showSecondFrame();
    }

    /**
     * Updates the text of a given label with part of its text plus a new text given.
     * @param label: the label to update.
     * @param newText: the new value to replace an old value in a labels text.
     */
    private void setNewText(JLabel label, String newText){
        String oldText = label.getText();
        int i = oldText.lastIndexOf(':');
        label.setText( oldText.substring(0, i + 1) + " " + newText);
    }

    /**
     * Getter method for the statistics list gathered during the simulation.
     * @return a list with the statistics.
     */
    public List<SimulatorStatistics> getStatisticsList(){
        return statisticsList;
    }

    /**
     * An average of the statistics found inside the list of statistics.
     * @return Global Statistics.
     */
    public SimulatorStatistics getGlobalStatistics(){
        return globalStatistics;
    }

    /**
     * Hides the second frame.
     */
    private void hideSecondFrame(){
        secondFrame.setVisible(false);
    }

    /**
     * Shows the third frame.
     */
    private void showThirdFrame(){
        thirdFrame.setVisible(true);
    }

    /**
     * Hides the third frame.
     */
    private void hideThirdFrame(){
        thirdFrame.setVisible(false);
    }

    /**
     * Hides the first frame.
     */
    private void hideFirstFrame(){
        firstFrame.setVisible(false);
    }

    /**
     * Initializes the third frame, adds the necessary components and adds most of them to the map.
     */
    private void startThirdFrame(){
        thirdFrame = new JFrame("Iteration end");
        thirdFrame.setLocation(200, 300);
        thirdFrame.setLayout(new BorderLayout());
        thirdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Simulation parameters.
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        JLabel averageLifeTimeLabel = new JLabel("Average Query Lifetime: "); otherMap.put("averageLifeTime", averageLifeTimeLabel);
        JLabel discardedConnectionsLabel = new JLabel("Discarded Connections: ");otherMap.put("discardedConnections", discardedConnectionsLabel);
        JLabel iterationLabel = new JLabel("Iteration : "); otherMap.put("iterations", iterationLabel);
        northPanel.add(iterationLabel); northPanel.add(averageLifeTimeLabel); northPanel.add(discardedConnectionsLabel);

        //A grid with the statistics of the modules.
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2,3,10,20));
        String averageQueueSize = "  Average Queue Size:   "; String idleTime = "  Idle time:   ";
        String DDLtime = "  DDL Lifetime:   "; String JOINtime = "  JOIN Lifetime:   ";
        String SELECTtime = "  SELECT Lifetime:   "; String UPDATEtime = "  UPDATE Lifetime:   ";

        JPanel mod0 = new JPanel();
        mod0.setLayout(new BoxLayout(mod0, BoxLayout.PAGE_AXIS));
        mod0.add(new JLabel("  Client Administrator:  "));
        JLabel mod0AverageQueueSize = new JLabel(averageQueueSize); otherMap.put("mod0AverageQueueSize", mod0AverageQueueSize);
        JLabel mod0IdleTime = new JLabel(idleTime); otherMap.put("mod0IdleTime", mod0IdleTime);
        JLabel mod0DDLtime = new JLabel(DDLtime); otherMap.put("mod0DDLtime", mod0DDLtime);
        JLabel mod0JOINtime = new JLabel(JOINtime); otherMap.put("mod0JOINtime", mod0JOINtime);
        JLabel mod0SELECTtime = new JLabel(SELECTtime); otherMap.put("mod0SELECTtime", mod0SELECTtime);
        JLabel mod0UPDATEtime= new JLabel(UPDATEtime); otherMap.put("mod0UPDATEtime", mod0UPDATEtime);
        mod0.add(mod0AverageQueueSize); mod0.add(mod0IdleTime); mod0.add(mod0DDLtime);
        mod0.add(mod0JOINtime); mod0.add(mod0SELECTtime); mod0.add(mod0UPDATEtime);

        JPanel mod1 = new JPanel();
        mod1.setLayout(new BoxLayout(mod1, BoxLayout.PAGE_AXIS));
        mod1.add(new JLabel("  Process Manager:  "));
        JLabel mod1AverageQueueSize = new JLabel(averageQueueSize); otherMap.put("mod1AverageQueueSize", mod1AverageQueueSize);
        JLabel mod1IdleTime = new JLabel(idleTime); otherMap.put("mod1IdleTime", mod1IdleTime);
        JLabel mod1DDLtime = new JLabel(DDLtime); otherMap.put("mod1DDLtime", mod1DDLtime);
        JLabel mod1JOINtime = new JLabel(JOINtime); otherMap.put("mod1JOINtime", mod1JOINtime);
        JLabel mod1SELECTtime = new JLabel(SELECTtime); otherMap.put("mod1SELECTtime", mod1SELECTtime);
        JLabel mod1UPDATEtime= new JLabel(UPDATEtime); otherMap.put("mod1UPDATEtime", mod1UPDATEtime);
        mod1.add(mod1AverageQueueSize); mod1.add(mod1IdleTime); mod1.add(mod1DDLtime);
        mod1.add(mod1JOINtime); mod1.add(mod1SELECTtime); mod1.add(mod1UPDATEtime);

        JPanel mod2 = new JPanel();
        mod2.setLayout(new BoxLayout(mod2, BoxLayout.PAGE_AXIS));
        mod2.add(new JLabel("  Query Processor:  "));
        JLabel mod2AverageQueueSize = new JLabel(averageQueueSize); otherMap.put("mod2AverageQueueSize", mod2AverageQueueSize);
        JLabel mod2IdleTime = new JLabel(idleTime); otherMap.put("mod2IdleTime", mod2IdleTime);
        JLabel mod2DDLtime = new JLabel(DDLtime); otherMap.put("mod2DDLtime", mod2DDLtime);
        JLabel mod2JOINtime = new JLabel(JOINtime); otherMap.put("mod2JOINtime", mod2JOINtime);
        JLabel mod2SELECTtime = new JLabel(SELECTtime); otherMap.put("mod2SELECTtime", mod2SELECTtime);
        JLabel mod2UPDATEtime= new JLabel(UPDATEtime); otherMap.put("mod2UPDATEtime", mod2UPDATEtime);
        mod2.add(mod2AverageQueueSize); mod2.add(mod2IdleTime); mod2.add(mod2DDLtime);
        mod2.add(mod2JOINtime); mod2.add(mod2SELECTtime); mod2.add(mod2UPDATEtime);

        JPanel mod3 = new JPanel();
        mod3.setLayout(new BoxLayout(mod3, BoxLayout.PAGE_AXIS));
        mod3.add(new JLabel("  Transactional Storage Manager:  "));
        JLabel mod3AverageQueueSize = new JLabel(averageQueueSize); otherMap.put("mod3AverageQueueSize", mod3AverageQueueSize);
        JLabel mod3IdleTime = new JLabel(idleTime); otherMap.put("mod3IdleTime", mod3IdleTime);
        JLabel mod3DDLtime = new JLabel(DDLtime); otherMap.put("mod3DDLtime", mod3DDLtime);
        JLabel mod3JOINtime = new JLabel(JOINtime); otherMap.put("mod3JOINtime", mod3JOINtime);
        JLabel mod3SELECTtime = new JLabel(SELECTtime); otherMap.put("mod3SELECTtime", mod3SELECTtime);
        JLabel mod3UPDATEtime= new JLabel(UPDATEtime); otherMap.put("mod3UPDATEtime", mod3UPDATEtime);
        mod3.add(mod3AverageQueueSize); mod3.add(mod3IdleTime); mod3.add(mod3DDLtime);
        mod3.add(mod3JOINtime); mod3.add(mod3SELECTtime); mod3.add(mod3UPDATEtime);

        JPanel mod4 = new JPanel();
        mod4.setLayout(new BoxLayout(mod4, BoxLayout.PAGE_AXIS));
        mod4.add(new JLabel("  Query Executor:  "));
        JLabel mod4AverageQueueSize = new JLabel(averageQueueSize); otherMap.put("mod4AverageQueueSize", mod4AverageQueueSize);
        JLabel mod4IdleTime = new JLabel(idleTime); otherMap.put("mod4IdleTime", mod4IdleTime);
        JLabel mod4DDLtime = new JLabel(DDLtime); otherMap.put("mod4DDLtime", mod4DDLtime);
        JLabel mod4JOINtime = new JLabel(JOINtime); otherMap.put("mod4JOINtime", mod4JOINtime);
        JLabel mod4SELECTtime = new JLabel(SELECTtime); otherMap.put("mod4SELECTtime", mod4SELECTtime);
        JLabel mod4UPDATEtime= new JLabel(UPDATEtime); otherMap.put("mod4UPDATEtime", mod4UPDATEtime);
        mod4.add(mod4AverageQueueSize); mod4.add(mod4IdleTime); mod4.add(mod4DDLtime);
        mod4.add(mod4JOINtime); mod4.add(mod4SELECTtime); mod4.add(mod4UPDATEtime);

        centerPanel.add(mod0); centerPanel.add(mod1); centerPanel.add(mod2); centerPanel.add(mod3); centerPanel.add(mod4);

        //Proceed button takes us to the next iteration, the average or the end of simulation.
        JButton button = new JButton("Proceed");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (iterationNumber <= iterations) {
                    simulator.initializeSimulation();
                    if (delay) {
                        cleanFrame();
                        runSimulationWithDelay();
                    }
                    else runSimulation();
                }
                else if (iterationNumber == iterations + 1){
                    globalStatistics = new SimulatorStatistics(maxTime, k, n, p, m, t, statisticsList);
                    hideThirdFrame();
                    updateThirdFrame(globalStatistics, "average");
                    showThirdFrame();
                    ++iterationNumber;
                }
                else {
                    simulationEnded = true;
                    hideThirdFrame();
                    SimulationReports reports = new SimulationReports();
                    try {
                        reports.generateReports(globalStatistics);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        System.exit(1);
                    }
                    System.exit(0);
                }
            }
        });
        thirdFrame.add(northPanel, BorderLayout.NORTH);
        thirdFrame.add(centerPanel, BorderLayout.CENTER);
        thirdFrame.add(button, BorderLayout.SOUTH);
        thirdFrame.pack();
    }

    /**
     * Updates the content of the third frame with the given Simulator Statistics and a iteration String object.
     * @param statistics: Iteration end statistics.
     * @param iteration: A String including the number of the iteration, or the word average.
     */
    private void updateThirdFrame(SimulatorStatistics statistics, String iteration){
        //General statistics
        this.setNewText(otherMap.get("averageLifeTime"), statistics.getAverageQueryLifeTime()+"");
        this.setNewText(otherMap.get("discardedConnections"), statistics.getNumberOfDiscartedConnections()+"");
        this.setNewText(otherMap.get("iterations"), iteration);

        //Average Queue Size per module
        double[] averageQueueSizes = statistics.getAverageQueueSizes();
        this.setNewText(otherMap.get("mod0AverageQueueSize"), averageQueueSizes[0]+"");
        this.setNewText(otherMap.get("mod1AverageQueueSize"), averageQueueSizes[1]+"");
        this.setNewText(otherMap.get("mod2AverageQueueSize"), averageQueueSizes[2]+"");
        this.setNewText(otherMap.get("mod3AverageQueueSize"), averageQueueSizes[3]+"");
        this.setNewText(otherMap.get("mod4AverageQueueSize"), averageQueueSizes[4]+"");

        //Idle time per module
        this.setNewText(otherMap.get("mod0IdleTime"), statistics.getModuleIdleTime(0)+"");
        this.setNewText(otherMap.get("mod1IdleTime"), statistics.getModuleIdleTime(1)+"");
        this.setNewText(otherMap.get("mod2IdleTime"), statistics.getModuleIdleTime(2)+"");
        this.setNewText(otherMap.get("mod3IdleTime"), statistics.getModuleIdleTime(3)+"");
        this.setNewText(otherMap.get("mod4IdleTime"), statistics.getModuleIdleTime(4)+"");

        //DDL Life time per module
        double[] DDLtimes = statistics.getAverageTimesByQueryType(QueryType.DDL);
        this.setNewText(otherMap.get("mod0DDLtime"), DDLtimes[0]+"");
        this.setNewText(otherMap.get("mod1DDLtime"), DDLtimes[1]+"");
        this.setNewText(otherMap.get("mod2DDLtime"), DDLtimes[2]+"");
        this.setNewText(otherMap.get("mod3DDLtime"), DDLtimes[3]+"");
        this.setNewText(otherMap.get("mod4DDLtime"), DDLtimes[4]+"");

        //JOIN Life time per module
        double[] JOINtimes = statistics.getAverageTimesByQueryType(QueryType.JOIN);
        this.setNewText(otherMap.get("mod0JOINtime"), JOINtimes[0]+"");
        this.setNewText(otherMap.get("mod1JOINtime"), JOINtimes[1]+"");
        this.setNewText(otherMap.get("mod2JOINtime"), JOINtimes[2]+"");
        this.setNewText(otherMap.get("mod3JOINtime"), JOINtimes[3]+"");
        this.setNewText(otherMap.get("mod4JOINtime"), JOINtimes[4]+"");

        //SELECT Life time per module
        double[] SELECTtimes = statistics.getAverageTimesByQueryType(QueryType.SELECT);
        this.setNewText(otherMap.get("mod0SELECTtime"), SELECTtimes[0]+"");
        this.setNewText(otherMap.get("mod1SELECTtime"), SELECTtimes[1]+"");
        this.setNewText(otherMap.get("mod2SELECTtime"), SELECTtimes[2]+"");
        this.setNewText(otherMap.get("mod3SELECTtime"), SELECTtimes[3]+"");
        this.setNewText(otherMap.get("mod4SELECTtime"), SELECTtimes[4]+"");

        //UPDATE Life time per module
        double[] UPDATEtimes = statistics.getAverageTimesByQueryType(QueryType.UPDATE);
        this.setNewText(otherMap.get("mod0UPDATEtime"), UPDATEtimes[0]+"");
        this.setNewText(otherMap.get("mod1UPDATEtime"), UPDATEtimes[1]+"");
        this.setNewText(otherMap.get("mod2UPDATEtime"), UPDATEtimes[2]+"");
        this.setNewText(otherMap.get("mod3UPDATEtime"), UPDATEtimes[3]+"");
        this.setNewText(otherMap.get("mod4UPDATEtime"), UPDATEtimes[4]+"");
        thirdFrame.pack();
    }

    /**
     * This method removes the old values from the previous iteration from the second frame, and replaces them either
     * with the default ones or an empty space.
     */
    private void cleanFrame(){
        Set<String> stringSet = labelMap.keySet();
        Iterator it = stringSet.iterator();
        while (it.hasNext()){
            this.setNewText( labelMap.get( it.next() ), "");
        }
        labelMap.get("iterations").setText("  Iteration: "+ iterationNumber + "  ");
        secondFrame.pack();
    }

    /**
     * @return true if this Interface has ended showing the results of the simulation, false otherwise.
     */
    public boolean simulationHasEnded(){
        return simulationEnded;
    }





}
