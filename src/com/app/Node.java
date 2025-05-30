package com.app;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Node extends Thread {
    public static int uidCounter = 0;

    private final int nodeId;
    private final Graph graph;
    private Point position;
    private Color color;
    private final Random rand = new Random();

    public Node(Graph graph) {
        super("Node-" + uidCounter);
        this.nodeId = uidCounter++;
        this.graph = graph;
        this.position = new Point(rand.nextInt(700) + 50, rand.nextInt(500) + 50);
        this.color = Color.GREEN;

        graph.repaint();
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

    public void request(Node target) {
        graph.showRequest(this, target);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                graph.resetCommunication(this, target);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void reply(Node source) {
        graph.showReply(this, source);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                graph.resetCommunication(source, this);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        List<Node> all;
        while (!isInterrupted()) {
            try {
                Thread.sleep(1000 + rand.nextInt(2000));
                all = graph.getNodes();
                if (all.size() < 2) continue;

                Node target;
                do {
                    target = all.get(rand.nextInt(all.size()));
                } while (target == this);

                request(target);
                Thread.sleep(1000 + rand.nextInt(2000));
                target.reply(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
