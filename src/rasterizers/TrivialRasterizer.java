package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor;
    private Raster raster;
    public TrivialRasterizer(Raster raster, Color defaultColor, int width) {
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
                    drawPixel(x, y, line.getColor().getRGB(), line.getWidth());
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
                    drawPixel(x, y, line.getColor().getRGB(), line.getWidth());
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
                    drawPixel(x, y, line.getColor().getRGB(), line.getWidth());
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
                    drawPixel(x, y, line.getColor().getRGB(), line.getWidth());
                }
            }
        }

    }
    @Override
    public void rasterize(models.Polygon poly) {
        if (poly.isFilled) {
            for (models.Point p : poly.GetAllInsidePoints()) {
                drawPixel(p.getX(), p.getY(), poly.GetColor().getRGB(), 1);
            }
        }
    }

    @Override
    public void rasterize(models.Circle circle) {
        int x0 = circle.getOrigin().getX();
        int y0 = circle.getOrigin().getY();
        int radius = Math.round(circle.getRadius());
        int rgb = circle.getColor().getRGB();

        int x = radius;
        int y = 0;
        int decisionOver2 = 1 - x;

        while (x >= y) {
            drawSymmetricPoints(raster, x0, y0, x, y, rgb, circle.getWidth());

            y++;
            if (decisionOver2 <= 0) {
                decisionOver2 += 2 * y + 1;
            } else {
                x--;
                decisionOver2 += 2 * (y - x) + 1;
            }
        }
        if (circle.isFilled) {
            for (models.Point p : circle.getInsidePoints()) {
                drawPixel(p.getX(), p.getY(), circle.getColor().getRGB(), 1);
            }
        }

    }
    private void drawSymmetricPoints(Raster raster, int x0, int y0, int x, int y, int rgb, int width) {
        drawPixel(x0 + x, y0 + y, rgb, width);
        drawPixel(x0 + y, y0 + x, rgb, width);
        drawPixel(x0 - y, y0 + x, rgb, width);
        drawPixel(x0 - x, y0 + y, rgb, width);
        drawPixel(x0 - x, y0 - y, rgb, width);
        drawPixel(x0 - y, y0 - x, rgb, width);
        drawPixel(x0 + y, y0 - x, rgb, width);
        drawPixel(x0 + x, y0 - y, rgb, width);
    }
    private void drawPixel(int x, int y, int rgb, int width) {
        int offset = width / 2;
        for (int ix = x - offset; ix < x - offset + width; ix++) {
            for (int iy = y - offset; iy < y - offset + width; iy++) {
                if (ix >= 0 && ix < raster.getWidth() && iy >= 0 && iy < raster.getHeight()) {raster.setPixel(ix, iy, rgb);
                }
            }
        }
    }
}

