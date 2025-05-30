package com.app;

import java.awt.*;

public class ArrowDrawer {
    public static void drawArrow(Graphics2D g2d, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 60) return; // Don't draw if nodes are too close
        
        // Calculate start and end points to avoid drawing over nodes
        double startX = from.x + (dx / distance) * 35;
        double startY = from.y + (dy / distance) * 35;
        double endX = to.x - (dx / distance) * 35;
        double endY = to.y - (dy / distance) * 35;

        // Draw the main arrow line
        g2d.drawLine((int)startX, (int)startY, (int)endX, (int)endY);

        // Calculate arrowhead
        double arrowLength = 12;
        double arrowAngle = Math.PI / 6;
        double angle = Math.atan2(dy, dx);

        int arrowX1 = (int)(endX - arrowLength * Math.cos(angle - arrowAngle));
        int arrowY1 = (int)(endY - arrowLength * Math.sin(angle - arrowAngle));
        int arrowX2 = (int)(endX - arrowLength * Math.cos(angle + arrowAngle));
        int arrowY2 = (int)(endY - arrowLength * Math.sin(angle + arrowAngle));

        // Draw arrowhead
        g2d.fillPolygon(
            new int[]{(int)endX, arrowX1, arrowX2},
            new int[]{(int)endY, arrowY1, arrowY2},
            3
        );
    }
}