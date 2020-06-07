package com.juju.amazeingame;

public class Vector2D {
    private float[] mValues = null;

    Vector2D() {
        mValues = new float[2];
    }

    Vector2D(Vector2D v) {
        this();
        this.set(v);
    }

    Vector2D(float x, float y) {
        this();
        this.set(x, y);
    }

    Vector2D(Point2D p1, Point2D p2) {
        this();
        this.set(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    }

    public float getX() {
        return mValues[0];
    }

    public float getY() {
        return mValues[1];
    }

    public void set(float x, float y) {
        mValues[0] = x;
        mValues[1] = y;
    }

    public void set(Vector2D v) {
        mValues[0] = v.mValues[0];
        mValues[1] = v.mValues[1];
    }

    public float squareNorm() {
        float sqNorm = mValues[0] * mValues[0] + mValues[1] * mValues[1];
        if (sqNorm > Tolerance.getSquareLengthEpsilon())
            return sqNorm;
        return 0f;
    }

    public float norm() {
        return (float)Math.sqrt(this.squareNorm());
    }

    public void normalize() {
        float norm = this.norm();
        if (norm > Tolerance.getLengthEpsilon()) {
            mValues[0] /= norm;
            mValues[1] /= norm;
        }
        else {
            mValues[0] = mValues[1] = 0f;
        }
    }

    public void add(Vector2D v) {
        mValues[0] += v.mValues[0];
        mValues[1] += v.mValues[1];
    }

    public void sub(Vector2D v) {
        mValues[0] -= v.mValues[0];
        mValues[1] -= v.mValues[1];
    }

    public void mult(float value) {
        mValues[0] *= value;
        mValues[1] *= value;
    }

    public float dot(Vector2D v) {
        return mValues[0] * v.mValues[0] + mValues[1] * v.mValues[1];
    }

    public float angle(Vector2D v) { //non oriented angle between 0 and PI
        float angle = (float)Math.acos(this.dot(v) / this.norm() / v.norm());
        if (angle > Tolerance.getAngleEpsilon())
            return angle;
        return 0f;
    }
}
