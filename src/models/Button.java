package models;

import java.awt.*;
import java.util.ArrayList;

public class Button extends Shape{
    public Function onClick;
    private Color col1;
    private Color col2;
    public int getWidth() {
        return width;
    }
    public Button(ArrayList<Point> points, Color c, Color c2, boolean isFilled, float size, Point origin, Function f) {
        super(points, c, isFilled, size, origin, 1);
        onClick = f;
        col1 = c;
        col2 = c2;
    }

    @Override
    public void SetColor(Color color) {
        super.SetColor(color);
        this.col1 = color;
    }

    public void setCol2(Color col2) {
        this.col2 = col2;
    }

    public Button(ArrayList<Point> points, Color c, boolean isFilled, float size, Point origin, Function f) {
        super(points, c, isFilled, size, origin, 1);
        onClick = f;
        col1 = c;
        col2 = c;
    }
    public Button(ArrayList<Point> points, Color c, boolean isFilled, float size, Point origin, int width, Function f) {
        super(points, c, isFilled, size, origin, width);
        onClick = f;
        col1 = c;
        col2 = c;
    }
    public void Toggle() {
        if (color == col1) {
            color = col2;
        } else {
            color = col1;
        }
    }
}
