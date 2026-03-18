package rasterizers;

import models.Line;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    void rasterize(Line line);
    void rasterize(models.Polygon poly);
    void rasterize(models.Circle circle);

}
