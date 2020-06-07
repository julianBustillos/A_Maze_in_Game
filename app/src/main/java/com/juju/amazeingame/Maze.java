package com.juju.amazeingame;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Maze {
    private static final int MIN_EMPTY_SPACE = 2; // unit : block
    private static final int NB_BLOCKS_Y = 19;

    private int mNbBlocksX = 0;
    private int mWidth = 0; //unit : pixel
    private int mHeight = 0; //unit : pixel
    private float mBlockLeftScreen = 0; //unit : pixel
    private float mBlockRightScreen = 0; //unit : pixel
    private float mBlockTopScreen = 0; //unit : pixel
    private float mBlockBottomScreen = 0; //unit : pixel
    private List<Block> mBlockList = null;
    private boolean initialized;

    Maze() {
        mBlockList = new ArrayList<Block>();
        initialized = false;
    }

    private int ComputeBlockNumbersAndSize(int width, int height) {
        int blockSize = (int) Math.ceil((double) height / NB_BLOCKS_Y);
        mNbBlocksX = (int) Math.ceil((double) width / blockSize);
        return blockSize;
    }

    private void computeScreenBlockLimits() {
        mBlockLeftScreen = (float)Block.getOffsetWidth() / (float)Block.getSize();
        mBlockRightScreen = ((float)Block.getOffsetWidth() + mWidth) / (float)Block.getSize();
        mBlockTopScreen = (float)Block.getOffsetHeight() / (float)Block.getSize();
        mBlockBottomScreen = ((float)Block.getOffsetHeight() + mHeight) / (float)Block.getSize();
    }

    private void RecursiveGeneration(Block.Type grid[][], Rect zone)
    {
        int xMin = zone.left + MIN_EMPTY_SPACE;
        int xMax = zone.right - MIN_EMPTY_SPACE;
        if (xMin > xMax)
            return;

        int yMin = zone.top + MIN_EMPTY_SPACE;
        int yMax = zone.bottom - MIN_EMPTY_SPACE;
        if (yMin > yMax)
            return;

        Random random = new Random();

        int xBound = 0;
        for (int x = xMin; x <= xMax; x++) {
            if (grid[x][zone.top - 1] ==  Block.Type.HOLE && grid[x][zone.bottom + 1] == Block.Type.HOLE)
                xBound++;
        }
        if (xBound == 0)
            return;
        int xRandom = random.nextInt(xBound);
        for (int x = xMin; x <= xMax; x++) {
            if (grid[x][zone.top - 1] ==  Block.Type.HOLE && grid[x][zone.bottom + 1] == Block.Type.HOLE) {
                if (xRandom == 0) {
                    xRandom = x;
                    break;
                }
                xRandom--;
            }
        }
        for (int y = zone.top; y <= zone.bottom; y++)
            grid[xRandom][y] = Block.Type.HOLE;

        int yBound = 0;
        for (int y = yMin; y <= yMax; y++) {
            if (grid[zone.left - 1][y] ==  Block.Type.HOLE && grid[zone.right + 1][y] == Block.Type.HOLE)
                yBound++;
        }
        if (yBound == 0)
            return;
        int yRandom = random.nextInt(yBound);
        for (int y = yMin; y <= yMax; y++) {
            if (grid[zone.left - 1][y] ==  Block.Type.HOLE && grid[zone.right + 1][y] == Block.Type.HOLE) {
                if (yRandom == 0) {
                    yRandom = y;
                    break;
                }
                yRandom--;
            }
        }
        for (int x = zone.left; x <= zone.right; x++)
            grid[x][yRandom] = Block.Type.HOLE;

        int notOpenRandom = random.nextInt(4);
        if (notOpenRandom != 0) {
            int openY = zone.top + random.nextInt(yRandom - zone.top - MIN_EMPTY_SPACE + 1);
            for (int k = 0; k < MIN_EMPTY_SPACE; k++)
                grid[xRandom][openY + k] = null;
        }
        if (notOpenRandom != 1) {
            int openY = yRandom + 1 + random.nextInt(zone.bottom - yRandom - MIN_EMPTY_SPACE + 1);
            for (int k = 0; k < MIN_EMPTY_SPACE; k++)
                grid[xRandom][openY + k] = null;
        }
        if (notOpenRandom != 2) {
            int openX = zone.left + random.nextInt(xRandom - zone.left - MIN_EMPTY_SPACE + 1);
            for (int k = 0; k < MIN_EMPTY_SPACE; k++)
                grid[openX + k][yRandom] = null;
        }
        if (notOpenRandom != 3) {
            int openX = xRandom + 1 + random.nextInt(zone.right - xRandom - MIN_EMPTY_SPACE + 1);
            for (int k = 0; k < MIN_EMPTY_SPACE; k++)
                grid[openX + k][yRandom] = null;
        }

        RecursiveGeneration(grid, new Rect(zone.left, zone.top, xRandom - 1, yRandom - 1));
        RecursiveGeneration(grid, new Rect(xRandom + 1, zone.top, zone.right, yRandom - 1));
        RecursiveGeneration(grid, new Rect(zone.left, yRandom + 1, xRandom - 1, zone.bottom));
        RecursiveGeneration(grid, new Rect(xRandom + 1, yRandom + 1, zone.right, zone.bottom));
    }

    private void InitPlayerAndGoal(Block.Type grid[][], Player player) {
        Random random = new Random();
        ArrayList<Integer> validPos = new ArrayList<Integer>();

        //Find and initialize valid player position
        for (int j = 1; j < NB_BLOCKS_Y - 1; j++) {
            for (int i = 1; i < mNbBlocksX - 1; i++) {
                if (grid[i][j] == null && grid[i + 1][j] == null && grid[i][j + 1] == null && grid[i + 1][j + 1] == null)
                    validPos.add(j * (mNbBlocksX + 2) + i);
            }
        }
        int playerPos = validPos.get(random.nextInt(validPos.size()));
        int initY = playerPos / (mNbBlocksX + 2);
        int initX = playerPos - initY * (mNbBlocksX + 2);
        synchronized (player) {
            player.setInit((float)initX - 0.5f, (float)initY - 0.5f);
        }

        //Find valid goal position using distance criterion
        final int holeDistance = mNbBlocksX * NB_BLOCKS_Y;
        int distanceGrid[][] = new int[mNbBlocksX][NB_BLOCKS_Y];
        Queue<Integer> indexQueue = new LinkedList<Integer>();
        for (int j = 0; j < NB_BLOCKS_Y; j++) {
            for (int i = 0; i < mNbBlocksX; i++) {
                if (grid[i + 1][j + 1] != null)
                    distanceGrid[i][j] = holeDistance;
                else
                    distanceGrid[i][j] = -1;
            }
        }
        distanceGrid[initX][initY] = 0;
        distanceGrid[initX + 1][initY] = 0;
        distanceGrid[initX][initY + 1] = 0;
        distanceGrid[initX + 1][initY + 1] = 0;

        for (int j = Math.max(0, initY - 1); j < Math.min(NB_BLOCKS_Y, initY + 3); j++) {
            for (int i = Math.max(0, initX - 1); i < Math.min(mNbBlocksX, initX + 3); i++) {
                indexQueue.add(j * mNbBlocksX + i);
            }
        }


        while(!indexQueue.isEmpty()) {
            int jCurrent = indexQueue.element() / mNbBlocksX;
            int iCurrent = indexQueue.element() - jCurrent * mNbBlocksX;
            indexQueue.remove();

            if (distanceGrid[iCurrent][jCurrent] >= 0)
                continue;

            int minDistance = holeDistance;
            for (int j = Math.max(0, jCurrent - 1); j < Math.min(NB_BLOCKS_Y, jCurrent + 2); j++) {
                for (int i = Math.max(0, iCurrent - 1); i < Math.min(mNbBlocksX, iCurrent + 2); i++) {
                    if (distanceGrid[i][j] < 0)
                        indexQueue.add(j * mNbBlocksX + i);
                    else if (distanceGrid[i][j] < minDistance)
                        minDistance = distanceGrid[i][j];
                }
            }
            distanceGrid[iCurrent][jCurrent] = minDistance + 1;
        }

        int currentMaxDist = 0;
        int iMax = 0;
        int jMax = 0;
        for (int j = 1; j < NB_BLOCKS_Y - 1; j++) {
            for (int i = 1; i < mNbBlocksX - 1; i++) {
                if (distanceGrid[i][j] != holeDistance && distanceGrid[i][j] >= 0 && distanceGrid[i][j] > currentMaxDist) {
                    currentMaxDist = distanceGrid[i][j];
                    iMax = i;
                    jMax = j;
                }
            }
        }
        grid[iMax + 1][jMax + 1] = Block.Type.GOAL;
    }

    private void GenerateMaze(Player player)
    {
        //Generate grid randomly
        Block.Type grid[][] = new Block.Type[mNbBlocksX + 2][NB_BLOCKS_Y + 2];
        for (int x = 0; x < mNbBlocksX + 2; x++)
            grid[x][0] = grid[x][NB_BLOCKS_Y + 1] = Block.Type.HOLE;
        for (int y = 1; y < NB_BLOCKS_Y + 1; y++)
            grid[0][y] = grid[mNbBlocksX + 1][y] = Block.Type.HOLE;

        RecursiveGeneration(grid, new Rect(1, 1, mNbBlocksX, NB_BLOCKS_Y));
        InitPlayerAndGoal(grid, player);

        //Create block list
        synchronized (this) {
            mBlockList.clear();
            for (int x = 1; x <= mNbBlocksX; x++) {
                for (int y = 1; y <= NB_BLOCKS_Y; y++) {
                    if (grid[x][y] != null)
                        mBlockList.add(new Block(grid[x][y], x - 1, y - 1));
                }
            }
        }

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public float getBlockLeftScreen() {
        return mBlockLeftScreen;
    }

    public float getBlockRightScreen() {
        return mBlockRightScreen;
    }

    public float getBlockTopScreen() {
        return mBlockTopScreen;
    }

    public float getBlockBottomScreen() {
        return mBlockBottomScreen;
    }

    public void buildRandom(Player player)
    {
        int blockSize = ComputeBlockNumbersAndSize(mWidth, mHeight);
        Block.computeSize(blockSize, mWidth, mHeight, mNbBlocksX, NB_BLOCKS_Y);
        computeScreenBlockLimits();
        GenerateMaze(player);
    }

    public List<Block> getBlockList() {
        return mBlockList;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
