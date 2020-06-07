package com.juju.amazeingame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Block {
    enum Type {HOLE, GOAL}

    private static int SIZE = 0;
    private static int OFFSET_WIDTH = 0;
    private static int OFFSET_HEIGHT = 0;
    private static Drawable IMAGE = null;

    public static int getSize() {
        return SIZE;
    }

    public static int getOffsetWidth() {
        return OFFSET_WIDTH;
    }

    public static int getOffsetHeight() {
        return OFFSET_HEIGHT;
    }

    public static void setContext(Context context) {
        IMAGE = context.getDrawable(R.drawable.monkey);
    }

    public static void computeSize(int size, int width, int height, int nbBlocksX, int nbBlocksY) {
        SIZE = size;
        int realWidth = nbBlocksX * SIZE;
        int realHeight = nbBlocksY * SIZE;
        OFFSET_WIDTH = (realWidth - width) / 2;
        OFFSET_HEIGHT = (realHeight - height) / 2;
    }


    private final Type mType;
    private final Rect mRect;

    Block(Type type, int posX, int posY) {
        mType = type;
        mRect = new Rect(posX * SIZE - OFFSET_WIDTH, posY * SIZE - OFFSET_HEIGHT, (posX + 1) * SIZE - OFFSET_WIDTH, (posY + 1) * SIZE - OFFSET_HEIGHT);
    }

    public Type getType() {
        return mType;
    }

    public Rect getRect() {
        return mRect;
    }

    public void draw(Canvas canvas, Paint paint) {
        switch (mType) {
            case HOLE:
                paint.setColor(Color.BLACK);
                canvas.drawRect(mRect, paint);
                break;
            case GOAL:
                IMAGE.setBounds(mRect);
                IMAGE.draw(canvas);
            default:
                return;
        }
    }
}
