package com.app;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
    private Graph graph = new Graph();

    public Main() {
        super("Graph Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton addNodeBtn = new JButton("Add Node");
        JButton removeNodeBtn = new JButton("Remove Node");

        addNodeBtn.addActionListener(graph::addNode);
        removeNodeBtn.addActionListener(graph::removeNode);

        controlPanel.add(addNodeBtn);
        controlPanel.add(removeNodeBtn);

        add(graph, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
