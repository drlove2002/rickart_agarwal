package com.app;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Edge {
	private static final List<Edge> all_edges = new ArrayList<>();
    private final Node from;
    private final Node to;
    private Color color;

    private Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        this.color = Color.GRAY;
    }
    
    public static List<Edge> all() {
        return Collections.unmodifiableList(all_edges);
    }
    
    public static Edge add(Node from, Node to) {
    	Edge edge = new Edge(from, to);
    	all_edges.add(edge);
    	return edge;
    }
    public static void remove(Node removed) {
    	all_edges.removeIf(edge -> edge.getFrom() == removed || edge.getTo() == removed);
    }
    
    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public synchronized Color getColor() { return color; }
    public synchronized void setColor(Color color) { this.color = color; }

    public boolean connects(Node a, Node b) {
        return (from == a && to == b) || (from == b && to == a);
    }
}