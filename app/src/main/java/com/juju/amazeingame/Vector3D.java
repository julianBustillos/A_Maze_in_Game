package com.juju.amazeingame;

public class Vector3D {
    private float[] mValues = null;

    Vector3D() {
        mValues = new float[3];
    }

    Vector3D(Vector3D v) {
        this();
        this.set(v);
    }

    Vector3D(float x, float y, float z) {
        this();
        this.set(x, y, z);
    }

    public float getX() {
        return mValues[0];
    }

    public float getY() {
        return mValues[1];
    }

    public float getZ() {
        return mValues[2];
    }

    public void set(float x, float y, float z) {
        mValues[0] = x;
        mValues[1] = y;
        mValues[2] = z;
    }

    public void set(Vector3D v) {
        mValues[0] = v.mValues[0];
        mValues[1] = v.mValues[1];
        mValues[2] = v.mValues[2];
    }

    public void setX(float value) {
        mValues[0] = value;
    }

    public void setY(float value) {
        mValues[1] = value;
    }

    void setZ(float value) {
        mValues[2] = value;
    }

    public float squareNorm() {
        float sqNorm = mValues[0] * mValues[0] + mValues[1] * mValues[1] + mValues[2] * mValues[2];
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
            mValues[2] /= norm;
        }
        else {
            mValues[0] = mValues[1] = mValues[2] = 0f;
        }
    }

    public void add(Vector3D v) {
        mValues[0] += v.mValues[0];
        mValues[1] += v.mValues[1];
        mValues[2] += v.mValues[2];
    }

    public void sub(Vector3D v) {
        mValues[0] -= v.mValues[0];
        mValues[1] -= v.mValues[1];
        mValues[2] -= v.mValues[2];
    }

    public void mult(float value) {
        mValues[0] *= value;
        mValues[1] *= value;
        mValues[2] *= value;
    }

    public float dot(Vector3D v) {
        return mValues[0] * v.mValues[0] + mValues[1] * v.mValues[1] + mValues[2] * v.mValues[2];
    }

    public void cross(Vector3D v1, Vector3D v2) {
        mValues[0] = v1.mValues[1] * v2.mValues[2] - v1.mValues[2] * v2.mValues[1];
        mValues[1] = v1.mValues[2] * v2.mValues[0] - v1.mValues[0] * v2.mValues[2];
        mValues[2] = v1.mValues[0] * v2.mValues[1] - v1.mValues[1] * v2.mValues[0];
    }

    public float angle(Vector3D v) { //non oriented angle between 0 and PI
        float angle = (float)Math.acos(this.dot(v) / this.norm() / v.norm());
        if (angle > Tolerance.getAngleEpsilon())
            return angle;
        return 0f;
    }

    public void rotate(Vector3D axis, float angle) {
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        float t = 1f - c;

        float u00 = axis.mValues[0] * axis.mValues[0];
        float u01 = axis.mValues[0] * axis.mValues[1];;
        float u02 = axis.mValues[0] * axis.mValues[2];;
        float u11 = axis.mValues[1] * axis.mValues[1];;
        float u12 = axis.mValues[1] * axis.mValues[2];;
        float u22 = axis.mValues[2] * axis.mValues[2];;

        float m00 = c + u00 * t;
        float m01 = u01 * t - axis.mValues[2] * s;
        float m02 = u02 * t + axis.mValues[1] * s;
        float m10 = u01 * t + axis.mValues[2] * s;
        float m11 = c + u11 * t;;
        float m12 = u12 * t - axis.mValues[0] * s;
        float m20 = u02 * t - axis.mValues[1] * s;
        float m21 = u12 * t + axis.mValues[0] * s;
        float m22 = c + u22 * t;

        float temp0 = m00 * mValues[0] +  m01 * mValues[1] +  m02 * mValues[2];
        float temp1 = m10 * mValues[0] +  m11 * mValues[1] +  m12 * mValues[2];
        float temp2 = m20 * mValues[0] +  m21 * mValues[1] +  m22 * mValues[2];

        mValues[0] = temp0;
        mValues[1] = temp1;
        mValues[2] = temp2;
    }
}
