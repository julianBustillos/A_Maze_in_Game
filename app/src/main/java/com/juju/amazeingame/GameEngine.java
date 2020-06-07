package com.juju.amazeingame;

import android.app.Service;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GameEngine implements SensorEventListener {
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float MAX_SPEED_NORM = 0.07f; //unit : block/s
    private static final float SENSOR_SCALING = 0.01f;
    private static final float BOUNCE_COEFFICIENT = 0.5f;
    private static final float GRAVITY_SQUARE_EPSILON = 0.05f;
    private static final float NO_BOUNCE_SQUARE_EPSILON = 0.0004f;

    private long mTimestamp = 0L;
    private GameActivity mGameActivity;
    private SensorManager mManager = null;
    private Sensor mGravity = null;
    private Vector3D mPrevGravityVector = null;
    private Vector3D mGravityVector = null;
    private Player mPlayer = null;
    private Maze mMaze = null;
    private int mDeathCount = 0;

    GameEngine(GameActivity activity, Player player, Maze maze) {
        mGameActivity = activity;
        mManager = (SensorManager) activity.getBaseContext().getSystemService(Service.SENSOR_SERVICE);
        mGravity = mManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mPrevGravityVector = new Vector3D();
        mGravityVector = new Vector3D();
        mPlayer = player;
        mMaze = maze;
    }

    private void computeSpeed(float dT) {
        Vector3D speed = mPlayer.getSpeed3D(); //unit : block/s
        Vector3D dSpeed = new Vector3D(mGravityVector);
        dSpeed.mult(SENSOR_SCALING * dT);

        //find rotation axis and angle
        float rotAngle = mPrevGravityVector.angle(mGravityVector);
        Vector3D rotAxis = new Vector3D();
        rotAxis.cross(mPrevGravityVector, mGravityVector);
        rotAxis.normalize();

        //rotate speed (equivalent to projection on new basis) and project on plane
        if (rotAngle > 0f && rotAxis.squareNorm() > 0f) {
            speed.rotate(rotAxis, rotAngle);

            Vector3D ZAxis = new Vector3D(0f, 0f, 1f);
            Vector3D ZProj = new Vector3D(ZAxis);
            ZProj.mult(speed.dot(ZAxis));
            speed.sub(ZProj);
            speed.setZ(0f);
        }

        //compute new speed vector and position
        speed.add(dSpeed);
        if (speed.squareNorm() > MAX_SPEED_NORM * MAX_SPEED_NORM) {
            speed.normalize();
            speed.mult(MAX_SPEED_NORM);
        }
        Point2D position = mPlayer.getPosition();
        position.add(new Vector2D(speed.getX(), speed.getY()));

        //update player values
        mPlayer.setSpeed(speed);
        mPlayer.setPosition(position);
    }

    private void applyBounce(Point2D wallPoint, Vector2D wallDir) {
        Vector2D speed = mPlayer.getSpeed2D();
        Point2D position = mPlayer.getPosition();

        wallDir.normalize();
        Vector2D speedProjOnWall = new Vector2D(wallDir);
        speedProjOnWall.mult(speedProjOnWall.dot(speed));
        Vector2D speedProjOnWallNormal = new Vector2D(speed);
        speedProjOnWallNormal.sub(speedProjOnWall);

        if (speedProjOnWallNormal.squareNorm() < NO_BOUNCE_SQUARE_EPSILON) {
            //Contact between wall and player, no bounce is needed

            //Set new speed along wall direction
            speed.set(speedProjOnWall);

            //Compute position projected on wall
            Vector2D wallPosVector = new Vector2D(wallPoint, position);
            wallDir.mult(wallDir.dot(wallPosVector));
            position.set(wallPoint);
            position.add(wallDir);
        }
        else {
            float alpha = 0;

            //find contact point with wall and exceeding speed vector
            if (Math.abs(speed.getX()) < Tolerance.getLengthEpsilon()) {
                if (Math.abs(wallDir.getX()) < Tolerance.getLengthEpsilon())
                    return;

                alpha = (position.getX() - wallPoint.getX()) / wallDir.getX();
            }
            else {
                float div = wallDir.getY() - wallDir.getX() * speed.getY() / speed.getX();
                if (Math.abs(div) < Tolerance.getLengthEpsilon())
                    return;

                alpha = ((position.getY() - wallPoint.getY()) + (wallPoint.getX() - position.getX()) * speed.getY() / speed.getX()) / div;
            }

            wallDir.mult(alpha);
            wallPoint.add(wallDir); //used as contact point
            Vector2D exceed = new Vector2D(wallPoint, position);//exceeding speed

            //Use exceeding speed to apply bounce from wall contact point
            wallDir.normalize();
            wallDir.mult(exceed.dot(wallDir));
            Vector2D wallSymmetry = new Vector2D(wallDir);
            wallSymmetry.sub(exceed);
            wallSymmetry.mult(2f);
            exceed.add(wallSymmetry);
            exceed.mult(BOUNCE_COEFFICIENT);
            position.set(wallPoint);
            position.add(exceed);

            //Compute new speed
            float speedNorm = speed.norm();
            speed.set(exceed);
            speed.normalize();
            speed.mult(speedNorm * BOUNCE_COEFFICIENT);
        }

        //Update player values
        mPlayer.setSpeed(new Vector3D(speed.getX(), speed.getY(), 0f));
        mPlayer.setPosition(position);
    }

    private void computeBounce() { // unit : block
        Point2D position = mPlayer.getPosition();

        if (position.getX() < mMaze.getBlockLeftScreen()) {
            applyBounce(new Point2D(mMaze.getBlockLeftScreen(), mMaze.getBlockTopScreen()), new Vector2D(0f, 1f));
        }
        else if (position.getX() >= mMaze.getBlockRightScreen() - 1f) {
            applyBounce(new Point2D(mMaze.getBlockRightScreen() - 1f, mMaze.getBlockTopScreen()), new Vector2D(0f, 1f));
        }

        if (position.getY() < mMaze.getBlockTopScreen()) {
            applyBounce(new Point2D(mMaze.getBlockLeftScreen(), mMaze.getBlockTopScreen()), new Vector2D(1f, 0f));
        }
        else if (position.getY() >= mMaze.getBlockBottomScreen() - 1f) {
            applyBounce(new Point2D(mMaze.getBlockLeftScreen(), mMaze.getBlockBottomScreen() - 1f), new Vector2D(1f, 0f));
        }
    }

    private void checkCollision() {
        Rect hitBox = mPlayer.getHitBox();

        for (Block b : mMaze.getBlockList()) {
            if (hitBox.intersect(b.getRect())) {
                switch (b.getType()) {
                    case HOLE:
                        mDeathCount++;
                        mPlayer.reset();
                        return;
                    case GOAL:
                        stop();
                        mGameActivity.showDialog();
                        return;
                }
                return;
            }
        }
    }

    public int getDeathCount() {
        return mDeathCount;
    }

    public void start() {
        mManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);
    }

    public void restart(boolean newMaze) {
        if (newMaze)
            mMaze.buildRandom(mPlayer);
        mDeathCount = 0;
        mPlayer.reset();
        start();
    }

    public void stop() {
        mManager.unregisterListener(this, mGravity);
        mTimestamp = 0L;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mTimestamp != 0 && mMaze.isInitialized()) {
            float dT = (event.timestamp - mTimestamp) * NS2S; //average : 0.02s
            mGravityVector.set(event.values[1], event.values[0], event.values[2]); //event values max abs average : 9.81 (without scaling)

            //Set mGravityVector normal to phone plane if very close
            Vector2D gravityPlaneProj = new Vector2D(mGravityVector.getX(), mGravityVector.getY());
            if (gravityPlaneProj.squareNorm() < GRAVITY_SQUARE_EPSILON) {
                mGravityVector.setX(0f);
                mGravityVector.setY(0f);
            }

            //Update player values
            computeSpeed(dT);
            computeBounce();
            checkCollision();
        }
        mPrevGravityVector.set(mGravityVector);
        mTimestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
