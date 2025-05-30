package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class Graph extends JPanel {
	private static final long serialVersionUID = 1L;
    private SignalManager signalManager;

    public Graph() {
        this.signalManager = new SignalManager();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // clears the background

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw all the edges (always gray)
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.GRAY);
        for (Edge edge : Edge.all()) {
            Point p1 = edge.getFrom().getPosition();
            Point p2 = edge.getTo().getPosition();
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // 2. Draw active signal arrows
        drawSignalArrows(g2d);

        // 3. Draw the nodes (circles with ID text)
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        for (Node node : Node.all()) {
            Point pos = node.getPosition();
            g2d.setColor(node.getColor());
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);

            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);

            // Draw node ID centered
            String id = String.valueOf(node.getNodeId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            int textHeight = fm.getAscent();
            g2d.drawString(id, pos.x - textWidth / 2, pos.y + textHeight / 4);
        }
    }


    // Replace activeSignals with signalManager.getActiveSignals()
    private void drawSignalArrows(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(4));
        for (SignalInfo signal : signalManager.getActiveSignals().values()) {
            Point from = signal.from.getPosition();
            Point to = signal.to.getPosition();
            g2d.setColor(signal.signalColor);
            ArrowDrawer.drawArrow(g2d, from, to);
        }
    }

    // Delegate comm methods
    public void showRequest(Node from, Node to) {
        signalManager.showRequest(from, to);
        repaint();
    }

    public void showReply(Node from, Node to) {
        signalManager.showReply(from, to);
        repaint();
    }

    public void resetCommunication(Node from, Node to) {
        signalManager.resetCommunication(from, to);
        repaint();
    }
   
    void addNode(ActionEvent e) {
        Node newNode = Node.push(this);
        
        for (Node other : Node.all()) {
        	if (other == newNode) {continue;}
            Edge.add(other, newNode);
        }
        newNode.start();
    }

    void removeNode(ActionEvent e) {
        if (!Node.all().isEmpty()) {
            Node removed = Node.pull();
            Edge.remove(removed);
        }
    }
}