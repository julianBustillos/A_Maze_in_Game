package com.juju.amazeingame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder = null;
    private DrawingThread mThread = null;
    private Paint mPaint = null;
    private Maze mMaze = null;
    private Player mPlayer = null;

    public GameView(Context context, AttributeSet attributeSet)  {
        super(context, attributeSet);

        mHolder = getHolder();
        mHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(Maze maze, Player player) {
        mMaze = maze;
        mPlayer = player;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null)
            return;

        canvas.drawColor(Color.WHITE);

        synchronized (mMaze) {
            for (Block b : mMaze.getBlockList()) {
                b.draw(canvas, mPaint);
            }
        }

        synchronized (mPlayer) {
            mPlayer.draw(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMaze.setSize(getWidth(), getHeight());
        mMaze.buildRandom(mPlayer);
        mThread = new DrawingThread();
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.setRunning(false);

        boolean joined = false;
        while (!joined) {
            try {
                mThread.join();
                joined = true;
            } catch (InterruptedException e) {}
        }
    }

    private class DrawingThread extends Thread {
        private boolean mRunning = true;

        public void setRunning(boolean running) {
            mRunning = running;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            while (mRunning) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();
                    synchronized (mHolder) {
                        onDraw(canvas);
                    }
                } finally {
                    if (canvas != null)
                        mHolder.unlockCanvasAndPost(canvas);
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }
    }
}
