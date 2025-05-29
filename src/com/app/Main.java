package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Graph graph = new Graph(nodes, edges);

    public Main() {
        super("Graph Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton addNodeBtn = new JButton("Add Node");
        JButton removeNodeBtn = new JButton("Remove Node");

        addNodeBtn.addActionListener(this::addNode);
        removeNodeBtn.addActionListener(this::removeNode);

        controlPanel.add(addNodeBtn);
        controlPanel.add(removeNodeBtn);

        add(graph, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addNode(ActionEvent e) {
        Node newNode = new Node(graph);
        for (Node existing : nodes) {
            edges.add(new Edge(existing, newNode));
        }
        nodes.add(newNode);
        newNode.start();
    }

    private void removeNode(ActionEvent e) {
        if (!nodes.isEmpty()) {
            Node removed = nodes.remove(nodes.size() - 1);
            edges.removeIf(edge -> edge.getFrom() == removed || edge.getTo() == removed);
            Node.uidCounter--;
            graph.refresh();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
