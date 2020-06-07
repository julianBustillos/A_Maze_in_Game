package com.juju.amazeingame;

public class Tolerance {
    private static final float EPSILON_LENGTH = 1E-6f;
    private static final float EPSILON_ANGLE = 1E-3f;

    static final float getLengthEpsilon (){
        return EPSILON_LENGTH;
    }

    static final float getSquareLengthEpsilon (){
        return EPSILON_LENGTH * EPSILON_LENGTH;
    }

    static final float getAngleEpsilon (){
        return EPSILON_ANGLE;
    }
}
