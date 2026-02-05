package models;

import org.w3c.dom.css.ElementCSSInlineStyle;

import java.awt.*;
import java.util.ArrayList;

public class Polygon {
    private ArrayList<Point> points;

    private ArrayList<Line> lines;

    private Color color;

    private boolean isFinished;

    public Polygon(Color c) {
        color = c;
        points = new ArrayList<>();
    }

    public void AddPoint(Point p) {
        points.add(p);
        this.RebuildLines();
    }

    public ArrayList<Point> GetPoints() {
        return points;
    }

    private void RebuildLines() {
        lines = new ArrayList<>();
        if (points.size() < 2) {
            return;
        }
        if (points.size() > 2) {
            for (int i = 1; i < points.size(); i++) {
                lines.add(new Line(points.get(i-1), points.get(i), color, !isFinished));
            }
        }
        lines.add(new Line(points.get(0), points.get(points.size() - 1), color, !isFinished));
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
}
