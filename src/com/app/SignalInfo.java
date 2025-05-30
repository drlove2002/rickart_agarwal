package com.app;

import java.awt.Color;

public class SignalInfo {
    public Node from;
    public Node to;
    public Color signalColor;

    public SignalInfo(Node from, Node to, Color signalColor) {
        this.from = from;
        this.to = to;
        this.signalColor = signalColor;
    }

    public String getKey() {
        return from.getNodeId() + "-" + to.getNodeId();
    }
}
