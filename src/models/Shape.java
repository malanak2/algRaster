package models;

import java.awt.*;
import java.util.ArrayList;

public class Shape extends Polygon implements Cloneable, IChangeOrigin {
    private float size;
    public Shape(ArrayList<models.Point> points, Color c, boolean isFilled, float size, models.Point origin, int width) {
        super(c, isFilled, width);

        this.size = size;
        this.origin = origin;
        for (models.Point p : points) {
            this.AddPoint(p);
        }
        this.Finish();
    }

    @Override
    public void SetSize(int i) {
        size = i;
        System.out.println("Shape update size" + i);
        RebuildLines();
    }

    @Override
    public int GetSize() {
        return (int) size;
    }

    @Override
    public ArrayList<models.Point> GetPoints() {

        ArrayList<models.Point> transformedPoints = new ArrayList<>();

        for (models.Point p : this.points) {
            int newX = Math.round(p.getX() * size) + origin.getX();
            int newY = Math.round(p.getY() * size) + origin.getY();
            transformedPoints.add(new models.Point(newX, newY));
        }
        return transformedPoints;
    };

    /**
     * Calculates the lines for all points
     */
    @Override
    protected void RebuildLines() {
        lines = new ArrayList<>();
        ArrayList<models.Point> points = this.GetPoints();
        if (points.size() < 2) {
            return;
        }
        if (points.size() > 2) {
            for (int i = 1; i < points.size(); i++) {
                lines.add(new Line(points.get(i-1), points.get(i), color, !isFinished, width));
//                System.out.printf("New line: %d, %d, %d, %d\n", lines.getLast().getPointA().getX(), lines.getLast().getPointA().getY(), lines.getLast().getPointB().getX(), lines.getLast().getPointB().getY());
            }
        }
        lines.add(new Line(points.getFirst(), points.getLast(), color, !isFinished, width));
    };
    @Override
    public void SetOrigin(models.Point p) {
        this.origin = p;
        RebuildLines();
    }
    public void SetSize(float f) {
        this.size = f;
        RebuildLines();
        calculateInsidePoints();
    }

    @Override
    public Point GetOrigin() {
        return origin;
    }

    @Override
    public Shape clone() {
        try {
            Shape cloned = (Shape) super.clone();

            if (this.origin != null) {
                cloned.origin = new models.Point(this.origin.getX(), this.origin.getY());
            }

            if (this.points != null) {
                cloned.points = new ArrayList<>();
                for (models.Point p : this.points) {
                    cloned.points.add(new models.Point(p.getX(), p.getY()));
                }
            }

            cloned.RebuildLines();

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
