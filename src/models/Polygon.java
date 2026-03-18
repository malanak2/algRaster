package models;

import org.w3c.dom.css.ElementCSSInlineStyle;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    protected ArrayList<Point> points;

    protected ArrayList<Line> lines;

    protected Color color;

    public boolean isFilled;

    public void setWidth(int width) {
        this.width = width;
        RebuildLines();
    }

    protected int width;

    protected boolean isFinished;
    protected final ArrayList<Point> insidePoints = new ArrayList<>();
    public Polygon(Color c, boolean isFilled, int width) {
        color = c;
        this.isFilled = isFilled;
        points = new ArrayList<>();
        this.width = width;
        rebuildInsidePoints();
    }

    public void AddPoint(Point p) {
        points.add(p);
        this.RebuildLines();
        this.rebuildInsidePoints();
    }

    public ArrayList<Point> GetPoints() {
        return points;
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
    protected void rebuildInsidePoints() {
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
}
