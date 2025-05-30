package com.app;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

public class Node extends Thread {
    public enum NodeState {
        IDLE(new Color(46, 204, 113)),           // Modern green
        REQUESTING(new Color(241, 196, 15)),     // Modern yellow/orange  
        IN_CS(new Color(231, 76, 60));           // Modern red
        
        private final Color color;
        NodeState(Color color) { this.color = color; }
        public Color getColor() { return color; }
    }

    private static final AtomicInteger uidCounter = new AtomicInteger(0);
    private static final List<Node> all_nodes = Collections.synchronizedList(new ArrayList<>());
    private static volatile Graph graph = null;

    private final int nodeId;
    private final Point position;
    private final AtomicReference<NodeState> currentState = new AtomicReference<>(NodeState.IDLE);
    private final AtomicLong clock = new AtomicLong(0);
    private final AtomicLong requestTimestamp = new AtomicLong(-1);

    // Thread-safe collections for Ricart-Agrawala
    private final Set<Integer> pendingReplies = ConcurrentHashMap.newKeySet();
    private final Queue<Integer> deferredReplies = new ConcurrentLinkedQueue<>();

    private Node() {
        super("Node-" + uidCounter.get());
        this.nodeId = uidCounter.getAndIncrement();
        this.position = generateNonOverlappingPosition();
        log("Node created with ID: " + nodeId);
    }

    private void log(String message) {
        System.out.println(String.format("[Node-%d] %s (State: %s, Clock: %d)", 
            nodeId, message, currentState.get(), clock.get()));
    }

    private static Point generateNonOverlappingPosition() {
        Random r = new Random();
        int attempts = 0;
        while (attempts < 100) { // Prevent infinite loop
            Point p = new Point(120 + r.nextInt(560), 120 + r.nextInt(360));
            boolean overlaps = all_nodes.stream()
                .anyMatch(n -> p.distance(n.position) < 80);
            if (!overlaps) return p;
            attempts++;
        }
        // Fallback to grid position if random fails
        int gridSize = (int) Math.ceil(Math.sqrt(uidCounter.get()));
        int row = uidCounter.get() / gridSize;
        int col = uidCounter.get() % gridSize;
        return new Point(150 + col * 100, 150 + row * 100);
    }

    public static List<Node> all() {
        return new ArrayList<>(all_nodes); // Return copy to avoid concurrent modification
    }

    public static Node push(Graph graph) {
        if (Node.graph == null) { Node.graph = graph; }
        Node node = new Node();
        all_nodes.add(node);
        return node;
    }

    public static Node pull() {
        if (all_nodes.isEmpty()) return null;
        
        Node removed = all_nodes.remove(all_nodes.size() - 1);
        removed.interrupt();
        
        // Clean up references to removed node
        all_nodes.forEach(node -> node.clear(removed));
        
        if (graph != null) {
            graph.repaint();
        }
        return removed;
    }

    public void clear(Node removed) {
        int id = removed.getNodeId();
        pendingReplies.remove(id);
        deferredReplies.remove(id);
    }

    // Getters
    public int getNodeId() { return nodeId; }
    public Point getPosition() { return new Point(position); } // Return copy
    public Color getColor() { return currentState.get().getColor(); }

    private Node findNodeById(int nodeId) {
        return all_nodes.stream()
                .filter(node -> node.getNodeId() == nodeId)
                .findFirst()
                .orElse(null);
    }

    private void requestCriticalSection() {
        if (!currentState.compareAndSet(NodeState.IDLE, NodeState.REQUESTING)) {
            return; // Already requesting or in CS
        }
        
        long timestamp = clock.incrementAndGet();
        requestTimestamp.set(timestamp);
        pendingReplies.clear();
        
        log("Requesting Critical Section (timestamp: " + timestamp + ")");
        
        // Get current snapshot of nodes
        List<Node> currentNodes = all();
        
        // Add all other nodes to pending replies
        currentNodes.stream()
            .filter(node -> node != this)
            .forEach(node -> pendingReplies.add(node.getNodeId()));
        
        // Send requests to all other nodes
        currentNodes.stream()
            .filter(node -> node != this)
            .forEach(this::sendRequest);
        
        repaintGraph();
    }

    private void sendRequest(Node other) {
        log("Sending REQUEST to Node-" + other.getNodeId());
        
        if (graph != null) {
            graph.showRequest(this, other);
        }
        
        other.handleRequest(nodeId, requestTimestamp.get());
    }

    public void handleRequest(int fromNodeId, long timestamp) {
        long newClock = Math.max(clock.get() + 1, timestamp);
        clock.set(newClock);
        
        log("Received REQUEST from Node-" + fromNodeId + " (timestamp: " + timestamp + ")");
        
        NodeState state = currentState.get();
        boolean shouldReplyImmediately = true;
        
        if (state == NodeState.REQUESTING) {
            long myTimestamp = requestTimestamp.get();
            // Higher priority = lower timestamp, or same timestamp with lower ID
            if (timestamp < myTimestamp || (timestamp == myTimestamp && fromNodeId < nodeId)) {
                log("Other node has higher priority, giving up my request");
                // Give up our request
                currentState.set(NodeState.IDLE);
                pendingReplies.clear();
                repaintGraph();
            } else {
                log("We have higher priority, deferring reply");
                shouldReplyImmediately = false;
                deferredReplies.offer(fromNodeId);
            }
        } else if (state == NodeState.IN_CS) {
            log("Currently in CS, deferring reply");
            shouldReplyImmediately = false;
            deferredReplies.offer(fromNodeId);
        }
        
        if (shouldReplyImmediately) {
            sendReply(fromNodeId);
        }
    }

    private void sendReply(int toNodeId) {
        Node requester = findNodeById(toNodeId);
        if (requester != null) {
            log("Sending REPLY to Node-" + toNodeId);
            
            if (graph != null) {
                graph.showReply(this, requester);
            }
            
            requester.handleReply(nodeId);
        }
    }

    public void handleReply(int fromNodeId) {
        log("Received REPLY from Node-" + fromNodeId);
        pendingReplies.remove(fromNodeId);
        
        log("Pending replies remaining: " + pendingReplies.size());
        
        if (pendingReplies.isEmpty() && currentState.get() == NodeState.REQUESTING) {
            log("All replies received, entering Critical Section");
            enterCriticalSection();
        }
    }

    private void enterCriticalSection() {
        currentState.set(NodeState.IN_CS);
        log("ENTERED Critical Section");
        repaintGraph();
        
        // Use a separate thread for CS timing to avoid blocking
        new Thread(() -> {
            try {
                int duration = 2500 + new Random().nextInt(1500); // 2.5-4 seconds
                log("Will stay in CS for " + duration + "ms");
                Thread.sleep(duration);
                
                if (!isInterrupted()) {
                    exitCriticalSection();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "CS-Timer-" + nodeId).start();
    }
    
    private void exitCriticalSection() {
        log("EXITING Critical Section");
        currentState.set(NodeState.IDLE);
        
        // Send all deferred replies
        Integer deferredNodeId;
        while ((deferredNodeId = deferredReplies.poll()) != null) {
            sendReply(deferredNodeId);
        }
        
        repaintGraph();
        log("Critical Section EXIT complete");
    }
    
    @Override
    public void run() {
        Random rand = new Random();
        log("Node thread started");
        
        while (!isInterrupted()) {
            try {
                // Variable wait time for more realistic behavior
                int waitTime = 6000 + rand.nextInt(6000); // 6-12 seconds
                Thread.sleep(waitTime);
                
                // 35% chance to request CS when idle
                if (currentState.get() == NodeState.IDLE && rand.nextDouble() < 0.35) {
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
    
    private void repaintGraph() {
        if (graph != null) {
            SwingUtilities.invokeLater(() -> graph.repaint());
        }
    }
}