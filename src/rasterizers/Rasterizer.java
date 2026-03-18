package rasterizers;

import models.Line;
import models.Point;

import java.awt.*;
import java.util.ArrayList;

public interface Rasterizer {

    void setColor(Color color);

    ArrayList<Point> rasterize(Line line);
    void rasterize(models.Polygon poly);
    ArrayList<Point> rasterize(models.Circle circle);

}
