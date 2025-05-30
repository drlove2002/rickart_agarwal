package com.app;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ArrowDrawer {
    public static void drawArrow(Graphics2D g2d, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double startX = from.x + (dx / distance) * 30;
        double startY = from.y + (dy / distance) * 30;
        double endX = to.x - (dx / distance) * 30;
        double endY = to.y - (dy / distance) * 30;

        g2d.drawLine((int)startX, (int)startY, (int)endX, (int)endY);

        double arrowLength = 15;
        double arrowAngle = Math.PI / 6;
        double angle = Math.atan2(dy, dx);

        int arrowX1 = (int)(endX - arrowLength * Math.cos(angle - arrowAngle));
        int arrowY1 = (int)(endY - arrowLength * Math.sin(angle - arrowAngle));
        int arrowX2 = (int)(endX - arrowLength * Math.cos(angle + arrowAngle));
        int arrowY2 = (int)(endY - arrowLength * Math.sin(angle + arrowAngle));

        g2d.drawLine((int)endX, (int)endY, arrowX1, arrowY1);
        g2d.drawLine((int)endX, (int)endY, arrowX2, arrowY2);
    }
}
