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

package ca.tirelesstraveler.fancywarpmenu.gui.grid;

/**
 * A rectangle placed on a {@code ScaledGrid}. These rectangles serve as bounding boxes for the GUI elements placed on
 * the grid, providing coordinates and dimensions to be used during rendering. This class does not cover using a
 * {@code ScaledGrid} placed within a {@code ScaledGrid}. For an example of that usage, see {@link ca.tirelesstraveler.fancywarpmenu.gui.GuiButtonIsland}
 *
 * @see ScaledGrid
 */
public class GridRectangle {
    protected transient final ScaledGrid scaledGrid;
    protected int gridX;
    protected int gridY;
    /** x position in pixels of the left edge */
    protected float xPosition;
    /** y position in pixels of the top edge */
    protected float yPosition;
    /** scaled x position of the left edge */
    protected float scaledXPosition;
    /** scaled y position of the top edge */
    protected float scaledYPosition;
    protected float width;
    protected float height;
    protected float scaledWidth;
    protected float scaledHeight;
    /** If {@code true}, multiply the position by the scale factor. If {@code false}, leave position as is. */
    protected boolean scalePosition;
    /** If {@code true}, shift the position so the rectangle looks like it's being expanded from the centre instead of the top left when scaled. If {@code false}, leave position as is. This works only when {@code scalePosition} is {@code false}. */
    protected boolean centerPositionWhenScaled;

    public GridRectangle(ScaledGrid scaledGrid, int gridX, int gridY, float width, float height, boolean scalePosition, boolean centerPositionWhenScaled) {
        this.scaledGrid = scaledGrid;
        this.gridX = gridX;
        this.gridY = gridY;
        this.xPosition = scaledGrid.getActualX(gridX);
        this.yPosition = scaledGrid.getActualY(gridY);
        this.width = width;
        this.height = height;
        this.scalePosition = scalePosition;
        this.centerPositionWhenScaled = centerPositionWhenScaled;
    }

    public void scale(float scaleFactor) {
        scaledWidth = width * scaleFactor;
        scaledHeight = height * scaleFactor;

        if (scalePosition) {
            scaledXPosition = scaledGrid.getActualX(gridX);
            scaledYPosition = scaledGrid.getActualY(gridY);
        } else if (centerPositionWhenScaled) {
            scaledXPosition = xPosition - (scaledWidth - width) / 2;
            scaledYPosition = yPosition - (scaledHeight - height) / 2;
        }
    }

    public float getXPosition() {
        if (scalePosition || centerPositionWhenScaled) {
            return scaledXPosition;
        } else {
            return xPosition;
        }
    }

    public float getYPosition() {
        if (scalePosition || centerPositionWhenScaled) {
            return scaledYPosition;
        } else {
            return yPosition;
        }
    }

    public float getWidth() {
        return scaledWidth;
    }

    public float getHeight() {
        return scaledHeight;
    }

    @Override
    public String toString() {
        return "GridRectangle{" +
                "gridX=" + gridX +
                ", gridY=" + gridY +
                ", xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", scaledXPosition=" + scaledXPosition +
                ", scaledYPosition=" + scaledYPosition +
                ", width=" + width +
                ", height=" + height +
                ", scaledWidth=" + scaledWidth +
                ", scaledHeight=" + scaledHeight +
                ", scalePosition=" + scalePosition +
                ", centerPositionWhenScaled=" + centerPositionWhenScaled +
                '}';
    }
}
