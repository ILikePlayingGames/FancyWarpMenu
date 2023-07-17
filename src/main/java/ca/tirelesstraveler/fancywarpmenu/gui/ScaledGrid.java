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

public class ScaledGrid {
    public final int GRID_START_X;
    public final int GRID_START_Y;
    public final float GRID_UNIT_WIDTH;
    public final float GRID_UNIT_HEIGHT;
    private float scale;
    private boolean scaled;

    public ScaledGrid(int gridStartX, int gridStartY, float gridUnitSize) {
        this(gridStartX, gridStartY, gridUnitSize, gridUnitSize);
    }

    public ScaledGrid(int gridStartX, int gridStartY, float gridUnitWidth, float gridUnitHeight) {
        this.GRID_START_X = gridStartX;
        this.GRID_START_Y = gridStartY;
        this.GRID_UNIT_WIDTH = gridUnitWidth;
        this.GRID_UNIT_HEIGHT = gridUnitHeight;
        scale = 1;
        scaled = false;
    }

    /**
     * Find the nearest grid x-coordinate to the given mouse x-position in pixels
     */
    int findNearestGridX(int mouseX) {
        float quotient = getScaledPositionF((mouseX - GRID_START_X) / GRID_UNIT_WIDTH);
        float remainder = getScaledPositionF((mouseX - GRID_START_Y) % GRID_UNIT_WIDTH);

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > getScaledPositionF(GRID_UNIT_WIDTH) / 2 ? quotient + 1 : quotient);
    }

    /**
     * Find the nearest grid y-coordinate to the given mouse y-position in pixels
     */
    int findNearestGridY(int mouseY) {
        float quotient = getScaledPositionF((mouseY - GRID_START_Y) / GRID_UNIT_HEIGHT);
        float remainder = getScaledPositionF((mouseY - GRID_START_X) % GRID_UNIT_HEIGHT);

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > getScaledPositionF(GRID_UNIT_HEIGHT) / 2 ? quotient + 1 : quotient);
    }

    /**
     * Find the x-position in pixels of a given grid x-coordinate
     */
    int getActualX(int gridX) {
        return getScaledPosition(Math.round(GRID_START_X + GRID_UNIT_WIDTH * gridX));
    }

    /**
     * Find the y-position in pixels of a given grid y-coordinate
     */
    int getActualY(int gridY) {
        return getScaledPosition(Math.round(GRID_START_Y + GRID_UNIT_WIDTH * gridY));
    }


    float getScale() {
        return scale;
    }

    int getScaledDimension(int dimension) {
        if (scaled) {
            return (int) (dimension * scale);
        } else {
            return dimension;
        }
    }

    int getScaledPosition(int position) {
        return (int) getScaledPositionF(position);
    }

    float getScaledPositionF(float position) {
        if (scaled) {
            return (position / scale);
        } else {
            return position;
        }
    }

    boolean isScaled() {
        return scaled;
    }
}
