package com.juju.amazeingame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Player {
    private static final float HITBOX_REDUCTION = 0.7f;

    private Drawable mImage = null;
    private Point2D mInit = null; //unit : block
    private Point2D mPosition = null; //unit : block
    private Vector3D mSpeed = null; //unit : block/s

    Player(Context context) {
        mImage = context.getDrawable(R.drawable.smile);
        mInit = new Point2D();
        mPosition = new Point2D(); //position on 2D phone plane
        mSpeed = new Vector3D(); //speed in 3D phone frame
        setInit(0, 0);
    }

    private Rect computeRect(float reduction) {
        float left = (mPosition.getX() + (1f - reduction) / 2f) * Block.getSize() - Block.getOffsetWidth();
        float top = (mPosition.getY() + (1f - reduction) / 2f) * Block.getSize() - Block.getOffsetHeight();
        float right = left + reduction * Block.getSize();
        float bottom = top + reduction * Block.getSize();
        return new Rect(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    public Point2D getPosition() {
        return mPosition;
    }

    public Vector3D getSpeed3D() {
        return new Vector3D(mSpeed);
    }

    public Vector2D getSpeed2D() {
        return new Vector2D(mSpeed.getX(), mSpeed.getY());
    }

    public void setInit(float x, float y) {
        mInit.set(x, y);
        reset();
    }

    public void reset() {
        mPosition.set(mInit);
        mSpeed.set(0f, 0f, 0f);
    }

    public void setSpeed(Vector3D speed) {
        mSpeed.set(speed);
    }

    public void setPosition(Point2D position) {
        mPosition.set(position);
    }

    public Rect getHitBox () {
        return computeRect(HITBOX_REDUCTION);
    }

    public void draw(Canvas canvas) {
        mImage.setBounds(computeRect(1.0f));
        mImage.draw(canvas);
    }
}
