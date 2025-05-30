package com.app;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

public class ArrowDrawer {
    public static void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.drawLine(from.x, from.y, to.x, to.y);
        double phi = Math.toRadians(30);
        int barb = 15;

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y;
        for (int j = 0; j < 2; j++) {
            double rho = theta + (j == 0 ? phi : -phi);
            x = to.x - barb * Math.cos(rho);
            y = to.y - barb * Math.sin(rho);
            g2.draw(new Line2D.Double(to.x, to.y, x, y));
        }
    }
}
