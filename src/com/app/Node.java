package com.app;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Node extends Thread {
    private enum NodeState {
        IDLE,           // Green - Not interested in CS
        REQUESTING,     // Orange - Sent REQUEST messages, waiting for replies
        IN_CS,          // Red - Currently in critical section
    }

    private static int uidCounter = 0;
    private static final List<Node> all_nodes = new ArrayList<>();
    private static Graph graph = null;

    private final int nodeId;
    private Point position;
    private Color color;
    private volatile NodeState currentState = NodeState.IDLE;
    private long clock = 0;
    private long requestTimestamp = -1;

    // Ricart-Agrawala specific fields
    private final Set<Integer> pendingReplies = Collections.synchronizedSet(new HashSet<>());
    private final Queue<Integer> deferredReplies = new ArrayDeque<>();

    private Node() {
        super("Node-" + uidCounter);
        this.nodeId = uidCounter++;
        this.position = generateNonOverlappingPosition();
        this.color = Color.GREEN;
        log("Node created with ID: " + nodeId);
    }

    private void log(String message) {
        System.out.println("[Node-" + nodeId + "] " + message + " (State: " + currentState + ", Clock: " + clock + ")");
    }

    private static Point generateNonOverlappingPosition() {
        Random r = new Random();
        while (true) {
            Point p = new Point(100 + r.nextInt(600), 100 + r.nextInt(400));
            boolean overlaps = all().stream().anyMatch(n -> p.distance(n.position) < 70);
            if (!overlaps) return p;
        }
    }

    public static List<Node> all() {
        return Collections.unmodifiableList(all_nodes);
    }

    public static Node push(Graph graph) {
        if (Node.graph == null) {Node.graph = graph;}
        Node node = new Node();
        all_nodes.add(node);
        return node;
    }

    public static Node pull() {
        if (all_nodes.isEmpty()) return null;
        
        Node.uidCounter--;
        Node removed = all_nodes.removeLast();
        removed.interrupt();
        
        // Clean up references to removed node
        for (Node node : Node.all()) {
            node.clear(removed);
        }
        
        if (graph != null) {
            graph.repaint();
        }
        return removed;
    }

    public void clear(Node removed) {
        int id = removed.getNodeId();
        pendingReplies.remove(id);
        synchronized(deferredReplies) {
            deferredReplies.remove(id);
        }
    }

    public int getNodeId() {
        return nodeId;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public synchronized Color getColor() {
        return color;
    }

    public synchronized void setColor(Color color) {
        this.color = color;
        if (graph != null) {
            graph.repaint();
        }
    }

    // FIXED: Find node by ID, not by index
    private synchronized Node findNodeById(int nodeId) {
        return all_nodes.stream()
                .filter(node -> node.getNodeId() == nodeId)
                .findFirst()
                .orElse(null);
    }

    private synchronized void requestCriticalSection() {
        if (currentState != NodeState.IDLE) return;
        
        currentState = NodeState.REQUESTING;
        requestTimestamp = ++clock;
        pendingReplies.clear();
        
        log("Requesting Critical Section (timestamp: " + requestTimestamp + ")");
        
        // Add all other nodes to pending replies BEFORE sending requests
        synchronized(all_nodes) {
            for (Node other : all_nodes) {
                if (other != this) {
                    pendingReplies.add(other.getNodeId());
                }
            }
        }
        
        // Now send REQUEST to all other nodes
        synchronized(all_nodes) {
            for (Node other : all_nodes) {
                if (other != this) {
                    log("Sending REQUEST to Node-" + other.getNodeId());
                    
                    // Show visual request
                    if (graph != null) {
                        graph.showRequest(this, other);
                    }
                    
                    other.handleRequest(nodeId, requestTimestamp);
                }
            }
        }
        updateColor();
    }

    public synchronized void handleRequest(int fromNodeId, long timestamp) {
        clock = Math.max(clock + 1, timestamp);
        log("Received REQUEST from Node-" + fromNodeId + " (timestamp: " + timestamp + ")");
        
        boolean shouldReplyImmediately = true;
        
        if (currentState == NodeState.REQUESTING) {
            // Compare timestamps for priority (FIXED: proper comparison)
            if (timestamp < requestTimestamp || 
               (timestamp == requestTimestamp && fromNodeId < nodeId)) {
                // Other has higher priority, reply immediately
                log("Other node has higher priority (ts:" + timestamp + " vs " + requestTimestamp + "), replying immediately");
                shouldReplyImmediately = true;
                // Since we're giving up our request, reset to IDLE
                currentState = NodeState.IDLE;
                pendingReplies.clear();
                updateColor();
            } else {
                // We have higher priority, defer reply
                log("We have higher priority (ts:" + requestTimestamp + " vs " + timestamp + "), deferring reply");
                shouldReplyImmediately = false;
                synchronized(deferredReplies) {
                    deferredReplies.offer(fromNodeId);
                }
            }
        } else if (currentState == NodeState.IN_CS) {
            // We're in CS, defer reply
            log("Currently in CS, deferring reply");
            shouldReplyImmediately = false;
            synchronized(deferredReplies) {
                deferredReplies.offer(fromNodeId);
            }
        }
        // If IDLE, always reply immediately (default case)
        
        if (shouldReplyImmediately) {
            Node requester = findNodeById(fromNodeId);
            if (requester != null) {
                log("Sending REPLY to Node-" + fromNodeId);
                
                // Show visual reply
                if (graph != null) {
                    graph.showReply(this, requester);
                }
                
                requester.handleReply(nodeId);
            }
        }
    }

    public synchronized void handleReply(int fromNodeId) {
        log("Received REPLY from Node-" + fromNodeId);
        pendingReplies.remove(fromNodeId);
        
        log("Pending replies remaining: " + pendingReplies.size());
        
        // If we have all replies, enter CS
        if (pendingReplies.isEmpty() && currentState == NodeState.REQUESTING) {
            log("All replies received, entering Critical Section");
            enterCriticalSection();
        }
    }

    private void enterCriticalSection() {
        currentState = NodeState.IN_CS;
        log("ENTERED Critical Section");
        updateColor();
        
        // Stay in CS for balanced duration (shorter for better visualization)
        new Thread(() -> {
            try {
                int duration = 3000; // Fixed 3 seconds for predictable visualization
                log("Will stay in CS for " + duration + "ms");
                Thread.sleep(duration);
                
                if (!isInterrupted()) {
                    exitCriticalSection();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private synchronized void exitCriticalSection() {
        log("EXITING Critical Section");
        currentState = NodeState.IDLE;
        
        // Send all deferred replies
        synchronized(deferredReplies) {
            while (!deferredReplies.isEmpty()) {
                int deferredNodeId = deferredReplies.poll();
                Node deferredNode = findNodeById(deferredNodeId);
                if (deferredNode != null) {
                    log("Sending deferred REPLY to Node-" + deferredNodeId);
                    
                    // Show visual reply
                    if (graph != null) {
                        graph.showReply(this, deferredNode);
                    }
                    
                    deferredNode.handleReply(nodeId);
                }
            }
        }
        
        updateColor();
        log("Critical Section EXIT complete");
    }
    
    @Override
    public void run() {
        Random rand = new Random();
        log("Node thread started");
        
        while (!isInterrupted()) {
            try {
                // Balanced wait time for better visualization
                int waitTime = 8000 + rand.nextInt(4000); // 8-12 seconds
                Thread.sleep(waitTime);
                
                // 40% chance to request CS when idle
                if (currentState == NodeState.IDLE && rand.nextDouble() < 0.4) {
                    log("Deciding to request Critical Section");
                    requestCriticalSection();
                }
                
            } catch (InterruptedException e) {
                log("Node thread interrupted");
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log("Node thread terminated");
    }
    
    private void updateColor() {
        Color newColor;
        switch (currentState) {
            case IDLE: newColor = Color.GREEN; break;
            case REQUESTING: newColor = Color.ORANGE; break;
            case IN_CS: newColor = Color.RED; break;
            default: newColor = Color.GREEN;
        }
        setColor(newColor);
    }
}