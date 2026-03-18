package models;

import java.awt.*;
import java.util.ArrayList;

public class Polygon implements IChangeOrigin {
    protected ArrayList<Point> points;

    protected ArrayList<Line> lines;

    protected Color color;

    public boolean isFilled;

    protected models.Point origin;

    public ArrayList<models.Point> pointsBorder;

    public void SetWidth(int width) {
        this.width = width;
        RebuildLines();
    }

    public int GetWidth() {
        return width;
    }
    public int size;
    @Override
    public void SetSize(int i) {
        size = i;
        System.out.println("Poly update size" + i);
        RebuildLines();
    }

    @Override
    public int GetSize() {

        return size;
    }

    protected int width;

    protected boolean isFinished;
    protected final ArrayList<Point> insidePoints = new ArrayList<>();
    public Polygon(Color c, boolean isFilled, int width) {
        color = c;
        this.isFilled = isFilled;
        points = new ArrayList<>();
        this.width = width;
        this.origin = new models.Point(0,0);
        this.pointsBorder = new ArrayList<>();
        this.size = 1;
        calculateInsidePoints();
    }

    public void AddPoint(Point p) {
        points.add(p);
        this.RebuildLines();
        this.calculateInsidePoints();
    }

    public ArrayList<Point> GetPoints() {

        ArrayList<models.Point> transformedPoints = new ArrayList<>();

        for (models.Point p : this.points) {
            int newX = Math.round(p.getX() * size) + origin.getX();
            int newY = Math.round(p.getY() * size) + origin.getY();
            transformedPoints.add(new models.Point(newX, newY));
        }
        return transformedPoints;
    }

    protected void RebuildLines() {
        lines = new ArrayList<>();
        if (points.size() < 2) {
            return;
        }
        if (points.size() > 2) {
            for (int i = 1; i < points.size(); i++) {
                lines.add(new Line(points.get(i-1), points.get(i), color, !isFinished, width));
            }
        }
        lines.add(new Line(points.get(0), points.get(points.size() - 1), color, !isFinished, width));
    }

    public ArrayList<Line> GetLines() {
        return lines;
    }

    public void Finish() {
        isFinished = true;
        RebuildLines();
    }

    public Color GetColor() {
        return color;
    }
    public void SetColor(Color color) {
        this.color = color;
        RebuildLines();
    }
    public ArrayList<models.Point> GetAllInsidePoints() {
        return insidePoints;
    }

    /**
     * Yeah found this online idk man, but it works!
     */
    @Override
    public void calculateInsidePoints() {
        ArrayList<models.Point> points = this.GetPoints();
        ArrayList<models.Point> ret = new ArrayList<>();
        if (points.isEmpty()) return;

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (models.Point p : points) {
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }

        for (int y = minY; y <= maxY; y++) {
            ArrayList<Integer> xIntersections = new ArrayList<>();

            for (int i = 0; i < points.size(); i++) {
                models.Point p1 = points.get(i);
                models.Point p2 = points.get((i + 1) % points.size());

                if ((p1.getY() <= y && p2.getY() > y) || (p2.getY() <= y && p1.getY() > y)) {
                    double x = p1.getX() + (double)(y - p1.getY()) / (p2.getY() - p1.getY()) * (p2.getX() - p1.getX());
                    xIntersections.add((int) Math.round(x));
                }
            }

            xIntersections.sort(Integer::compareTo);

            for (int i = 0; i < xIntersections.size(); i += 2) {
                if (i + 1 < xIntersections.size()) {
                    int startX = xIntersections.get(i);
                    int endX = xIntersections.get(i + 1);
                    for (int x = startX; x <= endX; x++) {
                        ret.add(new Point(x, y));
                    }
                }
            }
        }
        insidePoints.clear();
        insidePoints.addAll(ret);
    }
    public boolean IsInBounds(Point p) {
        if (this.GetAllInsidePoints().contains(p)) {
            return true;
        }
        return false;
    }

    @Override
    public void SetOrigin(Point p) {
        origin = p;
    }

    @Override
    public Point GetOrigin() {
        return origin;
    }
}
