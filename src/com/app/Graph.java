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

        // 1. Draw all the edges with appropriate colors
        g2d.setStroke(new BasicStroke(2));
        for (Edge edge : Edge.all()) {
            Point p1 = edge.getFrom().getPosition();
            Point p2 = edge.getTo().getPosition();
            
            // Check if there's an active signal on this edge
            String key1 = edge.getFrom().getNodeId() + "-" + edge.getTo().getNodeId();
            String key2 = edge.getTo().getNodeId() + "-" + edge.getFrom().getNodeId();
            
            SignalInfo signal1 = signalManager.getActiveSignals().get(key1);
            SignalInfo signal2 = signalManager.getActiveSignals().get(key2);
            
            if (signal1 != null) {
                g2d.setColor(signal1.signalColor);
            } else if (signal2 != null) {
                g2d.setColor(signal2.signalColor);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
            }
            
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // 2. Draw active signal arrows
        drawSignalArrows(g2d);

        // 3. Draw the nodes (circles with ID text)
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        for (Node node : Node.all()) {
            Point pos = node.getPosition();
            
            // Draw node circle
            g2d.setColor(node.getColor());
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);

            // Draw node border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);

            // Draw node ID centered
            String id = String.valueOf(node.getNodeId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            int textHeight = fm.getAscent();
            g2d.drawString(id, pos.x - textWidth / 2, pos.y + textHeight / 4);
        }
        
        // 4. Draw legend
        drawLegend(g2d);
    }

    private void drawLegend(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        int x = 10, y = 20;
        
        // Node states
        g2d.setColor(Color.GREEN);
        g2d.fillOval(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("IDLE", x + 20, y + 12);
        
        y += 20;
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("REQUESTING", x + 20, y + 12);
        
        y += 20;
        g2d.setColor(Color.RED);
        g2d.fillOval(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("IN CRITICAL SECTION", x + 20, y + 12);
        
        // Communication arrows
        y += 30;
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y, x + 15, y);
        ArrowDrawer.drawArrow(g2d, new Point(x, y), new Point(x + 15, y));
        g2d.setColor(Color.BLACK);
        g2d.drawString("REQUEST", x + 20, y + 5);
        
        y += 20;
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y, x + 15, y);
        ArrowDrawer.drawArrow(g2d, new Point(x, y), new Point(x + 15, y));
        g2d.setColor(Color.BLACK);
        g2d.drawString("REPLY", x + 20, y + 5);
    }

    private void drawSignalArrows(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(4));
        for (SignalInfo signal : signalManager.getActiveSignals().values()) {
            Point from = signal.from.getPosition();
            Point to = signal.to.getPosition();
            g2d.setColor(signal.signalColor);
            ArrowDrawer.drawArrow(g2d, from, to);
        }
    }

    // Communication methods integrated with visual feedback
    public void showRequest(Node from, Node to) {
        signalManager.showRequest(from, to);
        repaint();
        
        // Longer display time for better visualization
        Timer timer = new Timer(2000, e -> {
            signalManager.resetCommunication(from, to);
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void showReply(Node from, Node to) {
        signalManager.showReply(from, to);
        repaint();
        
        // Longer display time for better visualization
        Timer timer = new Timer(2000, e -> {
            signalManager.resetCommunication(from, to);
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void resetCommunication(Node from, Node to) {
        signalManager.resetCommunication(from, to);
        repaint();
    }
   
    void addNode(ActionEvent e) {
        if(Node.all().size() >= 10) {
            JOptionPane.showMessageDialog(
                    null,
                    "Max number of nodes (10) reached for better visualization!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
                );
            return;
        }
        
        System.out.println("=== Adding new node ===");
        Node newNode = Node.push(this);
        
        // Create edges to all existing nodes
        for (Node other : Node.all()) {
            if (other == newNode) continue;
            Edge.add(other, newNode);
        }
        
        newNode.start();
        repaint();
        System.out.println("Node " + newNode.getNodeId() + " added and started");
    }

    void removeNode(ActionEvent e) {
        if (Node.all().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No node to remove!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
                );
            return;
        }
        
        System.out.println("=== Removing node ===");
        Node removed = Node.pull();
        if (removed != null) {
            Edge.remove(removed);
            signalManager.clearNode(removed);
            repaint();
            System.out.println("Node " + removed.getNodeId() + " removed");
        }
    }
}