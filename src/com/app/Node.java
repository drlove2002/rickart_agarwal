package com.app;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Node extends Thread {
    private static final List<Node> allNodes = new ArrayList<>();
    private static final int CS_DURATION = 1000;

    private final int nodeId;
    private int clock = 0;
    private int requestTime = -1;
    private boolean wantsCS = false;
    private boolean inCS = false;
    private final Set<Integer> repliesReceived = new HashSet<>();
    private final Set<Node> deferredReplies = new HashSet<>();
    private final SignalManager manager;
    private final Graph graph;
    private Point position;
    private Color color = Color.BLUE;

    public Node(int id, Point pos, Graph g, SignalManager m) {
        this.nodeId = id;
        this.position = generateNonOverlappingPosition();
        this.graph = g;
        this.manager = m;
        allNodes.add(this);
    }

    public static List<Node> all() {
        return allNodes;
    }

    public static Node push(Graph g, SignalManager m) {
        int id = allNodes.size();
        Point pos = generateNonOverlappingPosition();
        Node node = new Node(id, pos, g, m);
        allNodes.add(node);
        return node;
    }

    public static Node pull() {
        return allNodes.isEmpty() ? null : allNodes.remove(allNodes.size() - 1);
    }

    private static Point generateNonOverlappingPosition() {
        Random r = new Random();
        while (true) {
            Point p = new Point(100 + r.nextInt(600), 100 + r.nextInt(400));
            boolean overlaps = allNodes.stream().anyMatch(n -> p.distance(n.position) < 70);
            if (!overlaps) return p;
        }
    }

    public int getNodeId() { return nodeId; }
    public Point getPosition() { return position; }
    public Color getColor() { return color; }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(2000 + new Random().nextInt(3000));
                requestCS();
            }
        } catch (InterruptedException ignored) {}
    }

    public void requestCS() {
        clock++;
        requestTime = clock;
        wantsCS = true;
        repliesReceived.clear();
        color = Color.YELLOW;

        for (Node other : allNodes) {
            if (other != this) {
                manager.showRequest(this, other);
                other.receiveRequest(this, requestTime);
            }
        }

        waitForReplies();
        enterCS();
        try {
            Thread.sleep(CS_DURATION);
        } catch (InterruptedException ignored) {}
        exitCS();
    }

    private void receiveRequest(Node from, int timestamp) {
        clock = Math.max(clock, timestamp) + 1;

        boolean allow = (!wantsCS) ||
                        (requestTime > timestamp) ||
                        (requestTime == timestamp && nodeId > from.nodeId);

        if (allow) {
            manager.showReply(this, from);
            from.receiveReply(this);
        } else {
            deferredReplies.add(from);
        }
    }

    private void receiveReply(Node from) {
        repliesReceived.add(from.nodeId);
    }

    private void waitForReplies() {
        while (repliesReceived.size() < allNodes.size() - 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

    private void enterCS() {
        color = Color.GREEN;
        inCS = true;
    }

    private void exitCS() {
        color = Color.BLUE;
        inCS = false;
        wantsCS = false;
        requestTime = -1;

        for (Node deferred : deferredReplies) {
            manager.showReply(this, deferred);
            deferred.receiveReply(this);
        }
        deferredReplies.clear();
    }
}
