package com.app;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class SignalManager {
    private final Map<String, SignalInfo> activeSignals = new HashMap<>();

    public void showRequest(Node from, Node to) {
        from.setColor(Color.BLUE);
        to.setColor(Color.BLUE);
        activeSignals.put(getKey(from, to), new SignalInfo(from, to, Color.BLUE));
    }

    public void showReply(Node from, Node to) {
        activeSignals.put(getKey(from, to), new SignalInfo(from, to, Color.GREEN));
    }

    public void resetCommunication(Node from, Node to) {
        activeSignals.remove(getKey(from, to));
        activeSignals.remove(getKey(to, from));
        from.setColor(Color.GREEN);
        to.setColor(Color.GREEN);
    }

    public Map<String, SignalInfo> getActiveSignals() {
        return activeSignals;
    }

    private String getKey(Node from, Node to) {
        return from.getNodeId() + "-" + to.getNodeId();
    }
}
