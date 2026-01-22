package models;

import java.awt.*;
import java.lang.reflect.Array;

public class Point {
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int x;
    public int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


}
