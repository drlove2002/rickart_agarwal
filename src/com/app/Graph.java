package com.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Graph extends JPanel {
    private static final long serialVersionUID = 1L;
    private final SignalManager signalManager;
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color EDGE_COLOR = new Color(149, 165, 166);
    private final Color ACTIVE_EDGE_COLOR = new Color(52, 152, 219);

    public Graph() {
        this.signalManager = new SignalManager();
        setPreferredSize(new Dimension(900, 650));
        setBackground(BACKGROUND_COLOR);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawEdges(g2d);
        drawSignalArrows(g2d);
        drawNodes(g2d);
        drawLegend(g2d);
        drawStatistics(g2d);
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (Edge edge : Edge.all()) {
            Point p1 = edge.getFrom().getPosition();
            Point p2 = edge.getTo().getPosition();
            
            // Check for active signals
            String key1 = edge.getFrom().getNodeId() + "-" + edge.getTo().getNodeId();
            String key2 = edge.getTo().getNodeId() + "-" + edge.getFrom().getNodeId();
            
            SignalInfo signal1 = signalManager.getActiveSignals().get(key1);
            SignalInfo signal2 = signalManager.getActiveSignals().get(key2);
            
            if (signal1 != null || signal2 != null) {
                g2d.setColor(ACTIVE_EDGE_COLOR);
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            } else {
                g2d.setColor(EDGE_COLOR);
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
            
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void drawSignalArrows(Graphics2D g2d) {
        for (SignalInfo signal : signalManager.getActiveSignals().values()) {
            Point from = signal.from.getPosition();
            Point to = signal.to.getPosition();
            ArrowDrawer.drawArrow(g2d, from, to, signal.signalColor);
        }
    }

    private void drawNodes(Graphics2D g2d) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        for (Node node : Node.all()) {
            Point pos = node.getPosition();
            Color nodeColor = node.getColor();
            
            // Draw node shadow for depth
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(pos.x - 32, pos.y - 28, 64, 64);
            
            // Draw node circle with gradient effect
            g2d.setColor(nodeColor);
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);
            
            // Add subtle inner highlight
            g2d.setColor(nodeColor.brighter());
            g2d.fillOval(pos.x - 25, pos.y - 25, 20, 20);
            
            // Draw node border
            g2d.setColor(nodeColor.darker());
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);
            
            // Draw node ID with better contrast
            String id = String.valueOf(node.getNodeId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(id);
            int textHeight = fm.getAscent();
            
            // Text shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(id, pos.x - textWidth / 2 + 1, pos.y + textHeight / 4 + 1);
            
            // Main text
            g2d.setColor(Color.WHITE);
            g2d.drawString(id, pos.x - textWidth / 2, pos.y + textHeight / 4);
        }
    }

    private void drawLegend(Graphics2D g2d) {
        int x = 15, y = 25;
        int spacing = 25;
        
        // Background panel for legend
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(x - 10, y - 15, 250, 170, 10, 10);
        g2d.setColor(new Color(189, 195, 199));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x - 10, y - 15, 250, 170, 10, 10);
        
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(new Color(44, 62, 80));
        g2d.drawString("Node States", x, y);
        y += spacing;
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Node states
        for (Node.NodeState state : Node.NodeState.values()) {
            g2d.setColor(state.getColor());
            g2d.fillOval(x, y - 10, 16, 16);
            g2d.setColor(state.getColor().darker());
            g2d.drawOval(x, y - 10, 16, 16);
            
            g2d.setColor(new Color(44, 62, 80));
            g2d.drawString(state.name().replace("_", " "), x + 25, y);
            y += 20;
        }
        
        y += 10;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("Message Types", x, y);
        y += 18;
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Request arrow
        g2d.setColor(new Color(52, 152, 219));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y - 5, x + 20, y - 5);
        ArrowDrawer.drawArrow(g2d, new Point(x, y - 5), new Point(x + 20, y - 5), new Color(52, 152, 219));
        g2d.setColor(new Color(44, 62, 80));
        g2d.drawString("REQUEST", x + 30, y);
        y += 20;
        
        // Reply arrow
        g2d.setColor(new Color(46, 204, 113));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x, y - 5, x + 20, y - 5);
        ArrowDrawer.drawArrow(g2d, new Point(x, y - 5), new Point(x + 20, y - 5), new Color(46, 204, 113));
        g2d.setColor(new Color(44, 62, 80));
        g2d.drawString("REPLY", x + 30, y);
    }

    private void drawStatistics(Graphics2D g2d) {
        List<Node> nodes = Node.all();
        if (nodes.isEmpty()) return;
        
        int x = getWidth() - 180;
        int y = 25;
        
        // Background panel
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(x - 10, y - 15, 170, 100, 10, 10);
        g2d.setColor(new Color(189, 195, 199));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x - 10, y - 15, 170, 100, 10, 10);
        
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(new Color(44, 62, 80));
        g2d.drawString("Network Status", x, y);
        y += 25;
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("Total Nodes: " + nodes.size(), x, y);
        y += 18;
        
        long idleCount = nodes.stream().mapToLong(n -> n.getColor().equals(Node.NodeState.IDLE.getColor()) ? 1 : 0).sum();
        long requestingCount = nodes.stream().mapToLong(n -> n.getColor().equals(Node.NodeState.REQUESTING.getColor()) ? 1 : 0).sum();
        long inCSCount = nodes.stream().mapToLong(n -> n.getColor().equals(Node.NodeState.IN_CS.getColor()) ? 1 : 0).sum();
        
        g2d.drawString("Idle: " + idleCount, x, y);
        y += 15;
        g2d.drawString("Requesting: " + requestingCount, x, y);
        y += 15;
        g2d.drawString("In CS: " + inCSCount, x, y);
    }

    // Communication methods with enhanced visual feedback
    public void showRequest(Node from, Node to) {
        signalManager.showRequest(from, to);
        repaint();
        
        Timer timer = new Timer(2500, e -> {
            signalManager.resetCommunication(from, to);
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void showReply(Node from, Node to) {
        signalManager.showReply(from, to);
        repaint();
        
        Timer timer = new Timer(2500, e -> {
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
        if(Node.all().size() >= 12) {
            JOptionPane.showMessageDialog(
                this,
                "Maximum number of nodes (12) reached for optimal visualization!",
                "Node Limit Reached",
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
                this,
                "No nodes available to remove!",
                "No Nodes",
                JOptionPane.INFORMATION_MESSAGE
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