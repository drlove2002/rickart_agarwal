package com.app;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SignalManager {
    private final Map<String, SignalInfo> activeSignals = new ConcurrentHashMap<>();
    
    // Modern colors for better visibility
    private static final Color REQUEST_COLOR = new Color(52, 152, 219);  // Modern blue
    private static final Color REPLY_COLOR = new Color(46, 204, 113);    // Modern green

    public void showRequest(Node from, Node to) {
        String key = getKey(from, to);
        activeSignals.put(key, new SignalInfo(from, to, REQUEST_COLOR));
        System.out.println("Signal Manager: Showing REQUEST from Node-" + from.getNodeId() + " to Node-" + to.getNodeId());
    }

    public void showReply(Node from, Node to) {
        String key = getKey(from, to);
        activeSignals.put(key, new SignalInfo(from, to, REPLY_COLOR));
        System.out.println("Signal Manager: Showing REPLY from Node-" + from.getNodeId() + " to Node-" + to.getNodeId());
    }

    public void resetCommunication(Node from, Node to) {
        String key1 = getKey(from, to);
        String key2 = getKey(to, from);
        activeSignals.remove(key1);
        activeSignals.remove(key2);
        System.out.println("Signal Manager: Reset communication between Node-" + from.getNodeId() + " and Node-" + to.getNodeId());
    }

    public void clearNode(Node node) {
        activeSignals.entrySet().removeIf(entry -> {
            SignalInfo signal = entry.getValue();
            return signal.from == node || signal.to == node;
        });
        System.out.println("Signal Manager: Cleared all signals for Node-" + node.getNodeId());
    }

    public Map<String, SignalInfo> getActiveSignals() {
        return new HashMap<>(activeSignals);
    }

    private String getKey(Node from, Node to) {
        return from.getNodeId() + "-" + to.getNodeId();
    }
}