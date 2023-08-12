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

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.GridRectangle;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@SuppressWarnings("FieldCanBeLocal")
public class GuiButtonConfig extends GuiButtonScaleTransition {
    private static final float HOVERED_SCALE = 1.2F;
    private static final long SCALE_TRANSITION_DURATION = 500;

    /** This button uses its own grid instead of the grid of the GuiScreen it belongs to since it's also attached to vanilla screens, which don't have grids */
    private final ScaledGrid scaledGrid;
    // Far right edge
    private final int GRID_X = 60;
    // Bottom edge
    private final int GRID_Y = 32;

    public GuiButtonConfig(int buttonId, ScaledResolution res) {
        super(buttonId, I18n.format("fancywarpmenu.ui.buttons.config"));
        scaledGrid = new ScaledGrid(0, 0, res.getScaledWidth(), res.getScaledHeight(), Island.GRID_UNIT_HEIGHT_FACTOR, Island.GRID_UNIT_WIDTH_FACTOR, false);
        width = height = (int) (res.getScaledWidth() * 0.05);
        // Above islands and warps
        zLevel = 20;
        buttonRectangle = new GridRectangle(scaledGrid, GRID_X, GRID_Y, width, height, false, true);
        scaledGrid.addRectangle("configButton", buttonRectangle);
        backgroundTextureLocation = new ResourceLocation("fancywarpmenu:textures/gui/Logo.png");
        transition = new ScaleTransition(0, 1, 1);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        if (this.visible) {
            super.drawButton(mc, mouseX, mouseY);
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);

            scaledGrid.setScaleFactor(transition.getCurrentScale());
            scaledXPosition = buttonRectangle.getXPosition();
            scaledYPosition = buttonRectangle.getYPosition();
            scaledWidth = buttonRectangle.getWidth();
            scaledHeight = buttonRectangle.getHeight();

            drawButtonTexture(backgroundTextureLocation);

            if (Settings.isDebugModeEnabled() && Settings.shouldDrawBorders()) {
                drawBorder(Color.WHITE);
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean clicked = super.mousePressed(mc, mouseX, mouseY);

        if (clicked && !(mc.currentScreen instanceof GuiFancyWarp)) {
            Settings.setWarpMenuEnabled(true);
            FancyWarpMenu.getInstance().getWarpMenuListener().displayFancyWarpMenu();
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("fancywarpmenu.messages.fancyWarpMenuEnabled").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
        }

        return clicked;
    }
}
