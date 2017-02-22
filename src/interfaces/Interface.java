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
                //Missing code, boolean delay
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


        panel.add(label);
        panel.add(p1);
        panel.add(p2);
        panel.add(p3);
        panel.add(p4);
        panel.add(p5);
        panel.add(p6);
        panel.add(p7);
        panel.add(p8);
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

        secondFrame.pack();
        this.showSecondFrame();
    }





}
