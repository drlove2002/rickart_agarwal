package com.app;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private Graph graph = new Graph();

    public Main() {
        super("Ricart-Agrawala Algorithm Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Control panel
        JPanel controlPanel = new JPanel();
        JButton addNodeBtn = new JButton("Add Node");
        JButton removeNodeBtn = new JButton("Remove Node");

        addNodeBtn.addActionListener(graph::addNode);
        removeNodeBtn.addActionListener(graph::removeNode);

        controlPanel.add(addNodeBtn);
        controlPanel.add(removeNodeBtn);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Algorithm Info"));
        
        JLabel info1 = new JLabel("Ricart-Agrawala Mutual Exclusion Algorithm");
        JLabel info2 = new JLabel("• Green: IDLE state");
        JLabel info3 = new JLabel("• Orange: REQUESTING Critical Section");
        JLabel info4 = new JLabel("• Red: IN Critical Section");
        JLabel info5 = new JLabel("• Blue arrows: REQUEST messages");
        JLabel info6 = new JLabel("• Green arrows: REPLY messages");
        JLabel info7 = new JLabel("Check console for detailed logs");
        
        info1.setFont(info1.getFont().deriveFont(Font.BOLD));
        
        infoPanel.add(info1);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(info2);
        infoPanel.add(info3);
        infoPanel.add(info4);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(info5);
        infoPanel.add(info6);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(info7);

        // Layout
        add(graph, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        System.out.println("=== Ricart-Agrawala Algorithm Visualizer Started ===");
        System.out.println("Add nodes to see the mutual exclusion algorithm in action!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}