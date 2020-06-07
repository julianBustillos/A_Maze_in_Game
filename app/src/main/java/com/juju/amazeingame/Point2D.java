package com.juju.amazeingame;

public class Point2D {
    private float[] mValues = null;

    Point2D() {
        mValues = new float[2];
    }

    Point2D(float x, float y) {
        this();
        this.set(x, y);
    }

    public void set(float x, float y) {
        mValues[0] = x;
        mValues[1] = y;
    }

    public void set(Point2D p) {
        mValues[0] = p.mValues[0];
        mValues[1] = p.mValues[1];
    }

    public float getX() {
        return mValues[0];
    }

    public float getY() {
        return mValues[1];
    }

    public void add(Vector2D v) {
        mValues[0] += v.getX();
        mValues[1] += v.getY();
    }
}
