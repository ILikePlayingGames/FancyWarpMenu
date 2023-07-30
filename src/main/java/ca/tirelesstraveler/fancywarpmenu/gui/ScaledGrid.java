/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.tirelesstraveler.fancywarpmenu.gui;

/**
 * This is a GUI placement grid whose size can be scaled by a given factor.
 * It also keeps the relative positions of GUI elements when scaled.
 */
public class ScaledGrid {
    public final float ORIGINAL_X_POSITION;
    public final float ORIGINAL_Y_POSITION;
    public final float ORIGINAL_GRID_WIDTH;
    public final float ORIGINAL_GRID_HEIGHT;
    public final float ORIGINAL_GRID_UNIT_WIDTH;
    public final float ORIGINAL_GRID_UNIT_HEIGHT;

    private float gridStartX;
    private float gridStartY;
    private float gridWidth;
    private float gridHeight;
    private float gridUnitWidth;
    private float gridUnitHeight;
    private float scaleFactor;
    private boolean scaled;

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, {@code numberOfRowsAndColumns} rows,
     * and {@code numberOfRowsAndColumns} columns.
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param numberOfRowsAndColumns number of rows and number of columns the grid will have
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, int numberOfRowsAndColumns) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, numberOfRowsAndColumns, numberOfRowsAndColumns);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, {@code numberOfRows} rows,
     * and {@code numberOfColumns} columns.
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param numberOfRows number of rows the grid will have
     * @param numberOfColumns number of columns the grid will have
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, int numberOfRows, int numberOfColumns) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, gridWidth / numberOfColumns, gridHeight / numberOfRows);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, and grid squares {@code gridUnitSize} by {@code gridUnitSize} in size
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param gridUnitSize side length of each grid square
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, float gridUnitSize) {
        this(gridStartX, gridStartY, gridWidth, gridHeight, gridUnitSize, gridUnitSize);
    }

    /**
     * Create a {@code ScaledGrid} with the top-left corner at ({@code gridStartX}. {@code gridStartY}) with a total width
     * of {@code gridWidth}, a total height of {@code gridHeight}, and grid rectangles {@code gridUnitWidth} by {@code gridUnitHeight} in size
     *
     * @param gridStartX x-coordinate of the left edge of the grid
     * @param gridStartY y-coordinate of the top edge of the grid
     * @param gridWidth total width of the grid
     * @param gridHeight total height of the grid
     * @param gridUnitWidth width of each grid rectangle
     * @param gridUnitHeight height of each grid rectangle
     */
    public ScaledGrid(float gridStartX, float gridStartY, float gridWidth, float gridHeight, float gridUnitWidth, float gridUnitHeight) {
        ORIGINAL_X_POSITION = gridStartX;
        ORIGINAL_Y_POSITION = gridStartY;
        ORIGINAL_GRID_WIDTH = gridWidth;
        ORIGINAL_GRID_HEIGHT = gridHeight;
        ORIGINAL_GRID_UNIT_WIDTH = gridUnitWidth;
        ORIGINAL_GRID_UNIT_HEIGHT = gridUnitHeight;
        this.gridStartX = gridStartX;
        this.gridStartY = gridStartY;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridUnitWidth = gridUnitWidth;
        this.gridUnitHeight = gridUnitHeight;
        scaleFactor = 1;
        scaled = false;
    }

    /**
     * Find the nearest grid x-coordinate to the given mouse x-position in pixels
     */
    int findNearestGridX(int mouseX) {
        float offset = mouseX - gridStartX;
        float quotient = offset / gridUnitWidth;
        float remainder = offset / gridUnitHeight;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > getScaledPosition(gridUnitWidth) / 2 ? quotient + 1 : quotient);
    }

    /**
     * Find the nearest grid y-coordinate to the given mouse y-position in pixels
     */
    int findNearestGridY(int mouseY) {
        float offset = mouseY - gridStartY;
        float quotient =  offset / gridUnitHeight;
        float remainder = offset % gridUnitHeight;

        // Truncate instead of rounding to keep the point above the cursor
        return (int) (remainder > getScaledPosition(gridUnitHeight) / 2 ? quotient + 1 : quotient);
    }

    /**
     * Find the x-position in pixels of a given grid x-coordinate
     */
    float getActualX(int gridX) {
        return gridStartX + getScaledDimension(getOffsetX(gridX));
    }

    /**
     * Find the y-position in pixels of a given grid y-coordinate
     */
    float getActualY(int gridY) {
        return gridStartY + getScaledDimension(getOffsetY(gridY));
    }

    /**
     * Get the offset of {@code gridX} from {@code gridStartX} in pixels
     */
    float getOffsetX(int gridX) {
        return getScaledPosition(gridUnitWidth * gridX);
    }

    /**
     * Get the offset of {@code gridY} from {@code gridStartY} in pixels
     */
    float getOffsetY(int gridY) {
        return getScaledPosition(gridUnitHeight * gridY);
    }

    float getScaleFactor() {
        return scaleFactor;
    }

    void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        scaled = scaleFactor != 1;

        gridWidth = getScaledDimension(ORIGINAL_GRID_WIDTH);
        gridHeight = getScaledDimension(ORIGINAL_GRID_HEIGHT);
        gridUnitWidth = getScaledDimension(ORIGINAL_GRID_UNIT_WIDTH);
        gridUnitHeight = getScaledDimension(ORIGINAL_GRID_UNIT_HEIGHT);
        gridStartX = ORIGINAL_X_POSITION - Math.abs((gridWidth - ORIGINAL_GRID_WIDTH)) / 2;
        gridStartY = ORIGINAL_Y_POSITION - Math.abs((gridHeight - ORIGINAL_GRID_HEIGHT)) / 2;
    }

    public float getGridStartX() {
        return gridStartX;
    }

    public float getGridStartY() {
        return gridStartY;
    }

    public float getGridUnitWidth() {
        return gridUnitWidth;
    }

    public float getGridUnitHeight() {
        return gridUnitHeight;
    }

    float getScaledDimension(float dimension) {
        if (scaled) {
            return dimension * scaleFactor;
        } else {
            return dimension;
        }
    }

    float getScaledPosition(float position) {
        if (scaled) {
            return (position / scaleFactor);
        } else {
            return position;
        }
    }

    boolean isScaled() {
        return scaled;
    }
}
