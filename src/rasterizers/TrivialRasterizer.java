package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor;
    private Raster raster;

    public TrivialRasterizer(Raster raster, Color defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void rasterize(Line line) {
        double k = (line.getPointB().getY() - line.getPointA().getY()) / (double) (line.getPointB().getX() - line.getPointA().getX());
        double q = line.getPointA().getY() - k * line.getPointA().getX();
        boolean staticX = line.getPointA().getX() == line.getPointB().getX();
        boolean staticY = line.getPointA().getY() == line.getPointB().getY();
        int stepamount = 1;
        if (line.isDotted()) stepamount = 5;
        if (Math.abs(k) < 1) {
            if (line.getPointA().getX() < line.getPointB().getX()) {
                for (int x = line.getPointA().getX(); x <= line.getPointB().getX(); x += stepamount) {
                    int y = (int) Math.round(k * x + q);
                    if (staticY) {
                        y = line.getPointA().getY();
                    }
                    if (x >= raster.getWidth() || y >= raster.getHeight() || x < 0 || y < 0) {
                        continue;
                    }
                    raster.setPixel(x, y, defaultColor.getRGB());
                }

            } else {
                for (int x = line.getPointA().getX(); x >= line.getPointB().getX(); x -= stepamount) {
                    int y = (int) Math.round(k * x + q);
                    if (staticY) {
                        y = line.getPointA().getY();
                    }
                    if (x >= raster.getWidth() || y >= raster.getHeight() || x < 0 || y < 0) {
                        continue;
                    }
                    raster.setPixel(x, y, defaultColor.getRGB());
                }
            }
        } else {
            if (line.getPointA().getY() < line.getPointB().getY()) {
                for (int y = line.getPointA().getY(); y <= line.getPointB().getY(); y += stepamount) {
                    int x = (int) Math.round((y - q) / k);
                    if (staticX) {
                        x = line.getPointA().getX();
                    }
                    if (x >= raster.getWidth() || y >= raster.getHeight() || x < 0 || y < 0) {
                        continue;
                    }
                    raster.setPixel(x, y, defaultColor.getRGB());
                }
            } else {
                for (int y = line.getPointA().getY(); y >= line.getPointB().getY(); y -= stepamount) {
                    int x = (int) Math.round((y - q) / k);
                    if (staticX) {
                        x = line.getPointA().getX();
                    }
                    if (x >= raster.getWidth() || y >= raster.getHeight() || x < 0 || y < 0) {
                        continue;
                    }
                    raster.setPixel(x, y, defaultColor.getRGB());
                }
            }
        }
    }
}
