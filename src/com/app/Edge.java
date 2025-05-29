package com.app;

import java.awt.*;

public class Edge {
    private final Node from;
    private final Node to;
    private Color color;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        this.color = Color.GRAY;
    }

    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public synchronized Color getColor() { return color; }
    public synchronized void setColor(Color color) { this.color = color; }

    public boolean connects(Node a, Node b) {
        return (from == a && to == b) || (from == b && to == a);
    }
}