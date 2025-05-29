package com.app;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class Graph extends JPanel {
	private static final long serialVersionUID = 1L;
	private List<Node> nodes;
    private List<Edge> edges;

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Thicker edge stroke
        g2d.setStroke(new BasicStroke(3));

        // Draw edges
        for (Edge edge : edges) {
            Point p1 = edge.getFrom().getPosition();
            Point p2 = edge.getTo().getPosition();
            g2d.setColor(edge.getColor());
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Bigger node labels
        g2d.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font size for IDs

        // Draw nodes
        for (Node node : nodes) {
            Point pos = node.getPosition();
            g2d.setColor(node.getColor());
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);

            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);

            // Draw ID centered
            String id = String.valueOf(node.getNodeId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            int textHeight = fm.getAscent();
            g2d.drawString(id, pos.x - textWidth / 2, pos.y + textHeight / 4);
        }
    }

    /**
     * Change color of the edge between two nodes
     */
    public void colorEdge(Node a, Node b, Color c) {
        for (Edge edge : edges) {
            if (edge.connects(a, b)) {
                edge.setColor(c);
                break;
            }
        }
        refresh();
    }
    
    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }
    
    public void refresh() {
        repaint();
    }
}