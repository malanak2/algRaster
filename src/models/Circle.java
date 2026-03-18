package models;

import java.awt.*;
import java.util.ArrayList;

public class Circle {
    private Point origin;
    private float radius;
    private Color color;
    private ArrayList<Point> insidePoints;
    public boolean isFilled;

    private int width;
    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }


    public float getRadius() {
        return radius;
    }

    public Point getOrigin() {
        return origin;
    }

    public Color getColor() {
        return color;
    }

    public Circle(Point origin, float radius, Color color, boolean isFilled, int width) {
        this.origin = origin;
        this.radius = radius;
        this.color = color;
        this.insidePoints = new ArrayList<>();
        this.isFilled = isFilled;
        this.width = width;
        calculateInsidePoints();
    }

    public void calculateInsidePoints() {
        this.insidePoints.clear();

        int h = origin.getX();
        int k = origin.getY();
        int r = Math.round(radius);

        for (int x = h - r; x <= h + r; x++) {
            for (int y = k - r; y <= k + r; y++) {
                double distanceSquared = Math.pow(x - h, 2) + Math.pow(y - k, 2);

                if (distanceSquared <= Math.pow(radius, 2)) {
                    insidePoints.add(new Point(x, y));
                }
            }
        }
    }

    public ArrayList<models.Point> getInsidePoints() {
        return insidePoints;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        calculateInsidePoints();
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
        calculateInsidePoints();
    }
}
