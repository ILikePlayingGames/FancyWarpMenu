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
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;

public class GuiButtonIsland extends GuiButtonScaleTransition {
    static final float HOVERED_SCALE = 1.1F;
    static final long SCALE_TRANSITION_DURATION = 400;
    final Island island;
    final ScaledGrid scaledGrid;

    public GuiButtonIsland(GuiFancyWarp parent, int buttonId, ScaledResolution res, Island island) {
        super(buttonId, "");
        this.island = island;
        island.init(res);
        scaledXPosition = parent.getScaledGrid().getActualX(island.getGridX());
        scaledYPosition = parent.getScaledGrid().getActualY(island.getGridY());
        zLevel = island.getzLevel();
        width = island.getWidth();
        height = island.getHeight();
        scaledGrid = new ScaledGrid(scaledXPosition, scaledYPosition, width, height, Warp.GRID_UNIT_WIDTH_FACTOR, true);
        displayString = EnumChatFormatting.GREEN + island.getName();
        backgroundTextureLocation = island.getTextureLocation();
        foregroundTextureLocation = island.getHoverEffectTextureLocation();
        transition = new ScaleTransition(0, 1, 1);

        // Each line is drawn separately. Copy the colour code to all lines.
        if (displayString.contains("\n")) {
            displayString = displayString.replaceAll("\n", "\n" + EnumChatFormatting.GREEN);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            float originalZ = zLevel;

            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);

            scaledGrid.setScaleFactor(transition.getCurrentScale());
            scaledXPosition = scaledGrid.getGridStartX();
            scaledYPosition = scaledGrid.getGridStartY();
            scaledWidth = scaledGrid.getScaledDimension(width);
            scaledHeight = scaledGrid.getScaledDimension(height);

            if (hovered) {
                zLevel = 9;
            }

            drawButtonTexture(backgroundTextureLocation);
            if (hovered) {
                drawButtonForegroundLayer(foregroundTextureLocation);
            }

            if (Settings.shouldShowIslandLabels()) {
                drawDisplayString(mc, scaledWidth / 2F, scaledHeight);
            }

            if (Settings.isDebugModeEnabled() && Settings.shouldDrawBorders()) {
                drawBorder(Color.WHITE);
            }

            zLevel = originalZ;
        }
    }

    public float getScaledXPosition() {
        return scaledXPosition;
    }

    public float getScaledYPosition() {
        return scaledYPosition;
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }
}
