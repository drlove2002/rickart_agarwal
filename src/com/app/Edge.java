package com.app;
import java.util.ArrayList;
import java.util.List;

public class Edge {
    private static final List<Edge> all = new ArrayList<>();
    private final Node from, to;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public static void add(Node a, Node b) {
        all.add(new Edge(a, b));
    }

    public static void remove(Node node) {
        all.removeIf(e -> e.from == node || e.to == node);
    }

    public static List<Edge> all() {
        return all;
    }

    public Node getFrom() { return from; }
    public Node getTo() { return to; }
}
