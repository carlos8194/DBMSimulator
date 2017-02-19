package interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

    public Interface(){
        this.startFirstFrame();
    }

    public void startFirstFrame(){
        firstFrame = new JFrame("DBMS Simulator configuration.");
        firstFrame.setLocation(200, 300);
        //firstFrame.setSize(1000, 4000);
        firstFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        firstFrame.setLayout(new FlowLayout());

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
        JTextField iterationsText = new JTextField("Number of iterations");
        JTextField maxTimeText = new JTextField("Max time per iteration");
        JTextField delayTimeText = new JTextField("Delay time");
        JTextField kText = new JTextField("k", 5);
        JTextField nText = new JTextField("n", 5);
        JTextField pText = new JTextField("p", 5);
        JTextField mText = new JTextField("m", 5);
        JTextField tText = new JTextField("Query Time Out");

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
                //Missing code, boolean delay
            }
        });
        firstFrame.add(label);
        firstFrame.add(iterationsText);
        firstFrame.add(maxTimeText);
        firstFrame.add(delayTimeText);
        firstFrame.add(kText);
        firstFrame.add(nText);
        firstFrame.add(pText);
        firstFrame.add(mText);
        firstFrame.add(tText);
        firstFrame.add(button);

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
        secondFrame = new JFrame("DBMS Simulator running.");
        secondFrame.setLocation(200, 300);
        //secondFrame.setSize(1000, 4000);
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        secondFrame.pack();
        this.showSecondFrame();
    }





}
