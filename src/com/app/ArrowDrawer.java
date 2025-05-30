package com.app;

import java.awt.*;

public class ArrowDrawer {
    public static void drawArrow(Graphics2D g2d, Point from, Point to, Color lineColor) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < 60) return; // Don't draw if nodes are too close
        
        // Calculate start and end points to avoid drawing over nodes
        double startX = from.x + (dx / distance) * 35;
        double startY = from.y + (dy / distance) * 35;
        double endX = to.x - (dx / distance) * 35;
        double endY = to.y - (dy / distance) * 35;

        // Draw the main arrow line in specified color
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine((int)startX, (int)startY, (int)endX, (int)endY);

        // Calculate arrowhead - always black for better visibility
        double arrowLength = 15;
        double arrowAngle = Math.PI / 5;
        double angle = Math.atan2(dy, dx);

        int arrowX1 = (int)(endX - arrowLength * Math.cos(angle - arrowAngle));
        int arrowY1 = (int)(endY - arrowLength * Math.sin(angle - arrowAngle));
        int arrowX2 = (int)(endX - arrowLength * Math.cos(angle + arrowAngle));
        int arrowY2 = (int)(endY - arrowLength * Math.sin(angle + arrowAngle));

        // Draw arrowhead in black for better contrast
        g2d.setColor(Color.BLACK);
        g2d.fillPolygon(
            new int[]{(int)endX, arrowX1, arrowX2},
            new int[]{(int)endY, arrowY1, arrowY2},
            3
        );
        
        // Add a subtle outline to the arrowhead
        g2d.setStroke(new BasicStroke(1));
        g2d.drawPolygon(
            new int[]{(int)endX, arrowX1, arrowX2},
            new int[]{(int)endY, arrowY1, arrowY2},
            3
        );
    }
}
