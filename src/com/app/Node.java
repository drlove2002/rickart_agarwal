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

        graph.repaint();
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
    	Node.uidCounter--;
    	Node removed = all_nodes.removeLast();
    	removed.interrupt();
    	for (Node node : Node.all()) {
    		 node.clear(removed);
    	} 
    	graph.repaint();
    	return removed;
    }
    public void clear(Node removed) {
    	int id = removed.getNodeId();
    	pendingReplies.remove(id);
    	deferredReplies.remove(id);
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
        graph.repaint();
    }

    private synchronized Node get(int nodeId) {
		return all_nodes.get(nodeId);
    }

    private void requestCriticalSection() {
        if (currentState != NodeState.IDLE) return;
        
        currentState = NodeState.REQUESTING;
        requestTimestamp = ++clock;
        pendingReplies.clear();
        
        // Send REQUEST to all other nodes
        for (Node other : all_nodes) {
            if (other != this) {
                pendingReplies.add(other.getNodeId());
                other.handleRequest(nodeId, requestTimestamp);
            }
        }
        updateColor();
    }

    public synchronized void handleRequest(int fromNodeId, long timestamp) {
        clock = Math.max(clock + 1, timestamp);
        
        boolean shouldReplyImmediately = true;
        
        if (currentState == NodeState.REQUESTING) {
            // Compare timestamps for priority
            if (timestamp < requestTimestamp || 
               (timestamp == requestTimestamp && fromNodeId < nodeId)) {
                // Other has higher priority, reply immediately
                shouldReplyImmediately = true;
            } else {
                // We have higher priority, defer reply
                shouldReplyImmediately = false;
                deferredReplies.offer(fromNodeId);
            }
        } else if (currentState == NodeState.IN_CS) {
            // We're in CS, defer reply
            shouldReplyImmediately = false;
            deferredReplies.offer(fromNodeId);
        }
        
        if (shouldReplyImmediately) {
            Node requester = get(fromNodeId);
            requester.handleReply(nodeId);
        }
    }

    public synchronized void handleReply(int fromNodeId) {
        pendingReplies.remove(fromNodeId);
        
        // If we have all replies, enter CS
        if (pendingReplies.isEmpty() && currentState == NodeState.REQUESTING) {
            enterCriticalSection();
        }
    }

    private void enterCriticalSection() {
        currentState = NodeState.IN_CS;
        updateColor();
        
        // Stay in CS for random duration
        new Thread(() -> {
            try {
                Thread.sleep(1000 + new Random().nextInt(4000)); // 1-5 seconds
                if(isInterrupted()) {return;}
                exitCriticalSection();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private synchronized void exitCriticalSection() {
        currentState = NodeState.IDLE;
        
        // Send all deferred replies
        while (!deferredReplies.isEmpty()) {
            int deferredNodeId = deferredReplies.poll();
            Node deferredNode = get(deferredNodeId);
            deferredNode.handleReply(nodeId);
        }
        
        updateColor();
    }
    
    @Override
    public void run() {
        Random rand = new Random();
        while (!isInterrupted()) {
            try {
                // Wait random time before potentially requesting CS
                Thread.sleep(3000 + rand.nextInt(8000)); // 3-8 seconds
                
                // 20% chance to request CS when idle
                if (currentState == NodeState.IDLE && rand.nextDouble() < 0.2) {
                    requestCriticalSection();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
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
