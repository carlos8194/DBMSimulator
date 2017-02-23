package interfaces;

import dbms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/**
 * Created by Rodrigo on 2/7/2017.
 */
public class Interface {

    private JFrame firstFrame;
    private JFrame secondFrame;
    private JFrame thirdFrame;

    private int iterations;
    private boolean delay;
    private double delayTime;
    private double maxTime;
    private int k;
    private int n;
    private int p;
    private int m;
    private double t;

    private DBMS simulator;

    public Interface(){
        this.startFirstFrame();
    }

    public void startFirstFrame(){
        firstFrame = new JFrame("DBMS Simulator configuration.");
        firstFrame.setLocation(200, 300);
        //firstFrame.setSize(1000, 4000);
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
                JOptionPane.showMessageDialog(null, "Version 1.1\nCreated by Carlos Luis Mellado Xatruch.");
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
                    delayTime = Double.parseDouble(delayTimeText.getText());
                    k = Integer.parseInt(kText.getText());
                    n = Integer.parseInt(nText.getText());
                    p = Integer.parseInt(pText.getText());
                    m = Integer.parseInt(mText.getText());
                    t = Double.parseDouble(tText.getText());
                    startSecondFrame();
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

    public void showFirstFrame(){
        firstFrame.setVisible(true);
    }

    public void showSecondFrame(){
        secondFrame.setVisible(true);
    }

    public void startSecondFrame(){
        firstFrame.setVisible(false);
        secondFrame = new JFrame("DBMS Simulator running...");
        secondFrame.setLocation(200, 300);
        //secondFrame.setSize(1000, 4000);
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulator = new DBMS(maxTime, k, n, p, m, t, this);

        secondFrame.setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.add(new JLabel("Iterations: "+iterations+" total time: "+maxTime+" k: "+k+" m: "+m+" n: "+n+" p: "+p+" t: "+t));

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        JLabel clockLabel = new JLabel("clock: 0.0");
        JLabel eventLabel = new JLabel("Current Event: ");
        JLabel discardedConnectionsLabel = new JLabel("Discarded Connections: 0");
        JLabel iterationNumberLabel = new JLabel("Iteration: ");
        southPanel.add(clockLabel); southPanel.add(eventLabel); southPanel.add(discardedConnectionsLabel);
        southPanel.add(iterationNumberLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout());

        JPanel mod0Panel = new JPanel();
        mod0Panel.setLayout(new BoxLayout(mod0Panel, BoxLayout.PAGE_AXIS));
        String serverState = "Ocuppied Servers: 0";
        String queueSize = "Queue Size: 0";
        String servedClients = "Served Clients: 0";
        mod0Panel.add(new JLabel("Module 0:"));
        mod0Panel.add(new JLabel(serverState)); mod0Panel.add(new JLabel(queueSize)); mod0Panel.add(new JLabel(servedClients));

        JPanel mod1Panel = new JPanel();
        mod1Panel.setLayout(new BoxLayout(mod1Panel, BoxLayout.PAGE_AXIS));
        mod1Panel.add(new JLabel("Module 1:"));
        mod1Panel.add(new JLabel(serverState)); mod1Panel.add(new JLabel(queueSize)); mod1Panel.add(new JLabel(servedClients));

        JPanel mod2Panel = new JPanel();
        mod2Panel.setLayout(new BoxLayout(mod2Panel, BoxLayout.PAGE_AXIS));
        mod2Panel.add(new JLabel("Module 2:"));
        mod2Panel.add(new JLabel(serverState)); mod2Panel.add(new JLabel(queueSize)); mod2Panel.add(new JLabel(servedClients));

        JPanel mod3Panel = new JPanel();
        mod3Panel.setLayout(new BoxLayout(mod3Panel, BoxLayout.PAGE_AXIS));
        mod3Panel.add(new JLabel("Module 3:"));
        mod3Panel.add(new JLabel(serverState)); mod3Panel.add(new JLabel(queueSize)); mod3Panel.add(new JLabel(servedClients));

        JPanel mod4Panel = new JPanel();
        mod4Panel.setLayout(new BoxLayout(mod4Panel, BoxLayout.PAGE_AXIS));
        mod4Panel.add(new JLabel("Module 4:"));
        mod4Panel.add(new JLabel(serverState)); mod4Panel.add(new JLabel(queueSize)); mod4Panel.add(new JLabel(servedClients));

        centerPanel.add(mod0Panel); centerPanel.add(mod1Panel); centerPanel.add(mod2Panel);
        centerPanel.add(mod3Panel); centerPanel.add(mod4Panel);

        secondFrame.add(northPanel, BorderLayout.NORTH);
        secondFrame.add(centerPanel, BorderLayout.CENTER);
        secondFrame.add(southPanel, BorderLayout.SOUTH);

        List<DBMSStatistics> list = new LinkedList<>();
        secondFrame.pack();
        this.showSecondFrame();
        for (int i = 0; i < iterations; i++){
            //clean frame
            iterationNumberLabel.setText("Iteration: "+i+1);
            list.add( simulator.runSimulation() );
        }
    }

    public void receiveNotification(InterfaceNotification notification, int m, String text){
        switch (notification){
            case CURRENT_EVENT:
                break;
            case CLOCK:
                break;
            case DISCARDED_CONNECTIONS:
                break;
            case SERVER_STATE:
                break;
            case QUEUE_SIZE:
                break;
            case SERVED_CLIENTS:
                break;
        }
    }





}
