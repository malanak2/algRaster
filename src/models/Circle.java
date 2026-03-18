package models;

import java.awt.*;
import java.util.ArrayList;

public class Circle implements IChangeOrigin{
    private Point origin;
    private float radius;
    private Color color;
    private ArrayList<Point> insidePoints;
    public boolean isFilled;

    private int width;
    public void SetWidth(int width) {
        this.width = width;
    }
    public int GetWidth() {
        return width;
    }

    @Override
    public void SetSize(int i) {
        setRadius(i);
    }

    @Override
    public int GetSize() {
        return Math.round(getRadius());
    }

    public ArrayList<models.Point> pointsBorder;

    public float getRadius() {
        return radius;
    }

    public Point getOrigin() {
        return origin;
    }

    public Color getColor() {
        return color;
    }
    public Function onClick;
    public Circle(Point origin, float radius, Color color, boolean isFilled, int width, Function f) {
        this.origin = origin;
        this.radius = radius;
        this.color = color;
        this.insidePoints = new ArrayList<>();
        this.isFilled = isFilled;
        this.width = width;
        this.onClick = f;
        this.pointsBorder = new ArrayList<>();
        calculateInsidePoints();
    }

    @Override
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

    @Override
    public void SetOrigin(Point p) {
        this.origin = p;
    }

    @Override
    public Point GetOrigin() {
        return origin;
    }
}
