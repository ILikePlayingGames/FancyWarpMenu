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

import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.GridRectangle;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class GuiButtonWarp extends GuiButtonScaleTransition {
    /** The button of the island this warp belongs to */
    private final GuiButtonIsland PARENT;
    private final Warp WARP;

    /**
     * x and y are relative to the top left corner of the parent island button.
     */
    public GuiButtonWarp(int buttonId, GuiButtonIsland parent, Warp warp) {
        super(buttonId, "");
        PARENT = parent;
        WARP = warp;
        buttonRectangle = new GridRectangle(parent.scaledGrid, warp.getGridX(), warp.getGridY(), warp.getWidth(), warp.getHeight(), true, false);
        parent.scaledGrid.addRectangle(warp.getDisplayName(), buttonRectangle);
        zLevel = 10;
        displayString = warp.getDisplayName();
        backgroundTextureLocation = WARP.getWarpTextureLocation();
        transition = new ScaleTransition(0, 0, 0);

        if (warp.shouldHideButton()) {
            visible = false;
        }
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            float originalZ = zLevel;

            hovered = PARENT.isMouseOver();
            transition.setCurrentScale(PARENT.scaledGrid.getScaleFactor());

            scaledXPosition = buttonRectangle.getXPosition();
            scaledYPosition = buttonRectangle.getYPosition();
            scaledWidth = buttonRectangle.getWidth();
            scaledHeight = buttonRectangle.getHeight();

            if (hovered) {
                zLevel = 19;
            }

            drawButtonTexture(backgroundTextureLocation);
            drawButtonForegroundLayer(foregroundTextureLocation);

            zLevel = originalZ;

            if (!Settings.shouldHideWarpLabelsUntilIslandHovered() || PARENT.isMouseOver()) {
                drawDisplayString(mc, buttonRectangle.getWidth() / 2F, buttonRectangle.getHeight());
            }
            
            if (Settings.isDebugModeEnabled() && Settings.shouldDrawBorders()) {
                drawBorder(Color.WHITE);
            }
        }
    }

    public String getWarpCommand() {
        return WARP.getWarpCommand();
    }

    Island getIsland() {
        return PARENT.island;
    }
}
