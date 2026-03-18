package models;

import java.awt.*;
import java.util.ArrayList;

public class Line implements IChangeOrigin{
    private Point pointA;
    private Point pointB;
    private Color color;
    private boolean dotted;
    private int width;
    public models.Point origin;

    public ArrayList<Point> pointsBorder;
    public int getWidth() {
        return width;
    }

    public void setDotted(boolean dotted) {
        this.dotted = dotted;
    }

    public boolean isDotted() {
        return dotted;
    }

    public Line(Point pointA, Point pointB, Color color, boolean dotted, int width) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.color = color;
        this.dotted = dotted;
        this.width = width;
        this.origin = new models.Point(0,0);
        this.pointsBorder = new ArrayList<>();
    }

    private void calculatePoints() {

    }

    public void setPointA(Point pointA) {
        this.pointA = pointA;
    }

    public void setPointB(Point pointB) {
        this.pointB = pointB;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point getPointA() {
        return pointA;
    }

    public Point getPointB() {
        return pointB;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void SetOrigin(Point p) {
        origin = p;
    }

    @Override
    public Point GetOrigin() {
        return origin;
    }

    @Override
    public void SetSize(int i) {

    }

    @Override
    public int GetSize() {
        return 0;
    }

    @Override
    public void calculateInsidePoints() {
    }
}
