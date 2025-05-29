package com.app;

import java.awt.*;
import java.util.Random;
import java.util.List;

public class Node extends Thread {
    static int uidCounter = 0;
    private final int nodeId;
    private final Graph panel;
    private Point position;
    private Color color;
    private final Random rand = new Random();

    public Node(Graph panel) {
        super("Node-" + uidCounter);
        this.nodeId = uidCounter++;
        this.panel = panel;
        this.position = new Point(rand.nextInt(700)+50, rand.nextInt(500)+50);
        this.color = Color.GREEN;

        panel.refresh();
    }

    public int getNodeId() { return nodeId; }
    public Point getPosition() { return position; }
    public void setPosition(Point position) { this.position = position; }
    public synchronized Color getColor() { return color; }
    public synchronized void setColor(Color color) { this.color = color; panel.refresh(); }

    /**
     * Send a request to another node: both nodes & their connecting edge turn BLUE for 2 seconds
     */
    public void request(Node target) {
        this.setColor(Color.BLUE);
        target.setColor(Color.BLUE);
        panel.colorEdge(this, target, Color.BLUE);

        new Thread(() -> {
            try {
                Thread.sleep(500);
                panel.colorEdge(this, target, Color.GRAY);
                this.setColor(Color.GREEN);
                target.setColor(Color.GREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Reply from target to this node: edge turns GREEN for 2 seconds, nodes stay BLUE
     */
    public void reply(Node source) {
        panel.colorEdge(source, this, Color.GREEN);

        new Thread(() -> {
            try {
                Thread.sleep(500);
                panel.colorEdge(source, this, Color.GRAY);
                this.setColor(Color.GREEN);
                source.setColor(Color.GREEN);
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
                Thread.sleep(1000 + rand.nextInt(2000)); // wait before requesting
                all = panel.getNodes();
                if (all.size() < 2) continue;

                // pick random target != this
                Node target;
                do {
                    target = all.get(rand.nextInt(all.size()));
                } while (target == this);

                // simulate request and reply
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