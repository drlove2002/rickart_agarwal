package com.app;
import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class SignalManager {
    private final Map<String, SignalInfo> signals = new ConcurrentHashMap<>();

    public void showRequest(Node from, Node to) {
        String key = from.getNodeId() + "->" + to.getNodeId();
        signals.put(key, new SignalInfo(from, to, Color.ORANGE));
        new Timer().schedule(new TimerTask() {
            public void run() {
                signals.remove(key);
            }
        }, 800);
    }
    
    public void resetCommunication(Node from, Node to) {
        String key = from.getNodeId() + "->" + to.getNodeId();
        signals.remove(key);
    }

    public void showReply(Node from, Node to) {
        String key = from.getNodeId() + "->" + to.getNodeId();
        signals.put(key, new SignalInfo(from, to, Color.CYAN));
        new Timer().schedule(new TimerTask() {
            public void run() {
                signals.remove(key);
            }
        }, 800);
    }
    
    public Collection<SignalInfo> getActiveSignals() {
        return signals.values();
    }
}
