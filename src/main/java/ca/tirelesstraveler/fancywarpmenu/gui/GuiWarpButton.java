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

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class GuiWarpButton extends GuiButtonExt {
    /** The button of the island this warp belongs to */
    private final GuiIslandButton PARENT;
    private final Warp WARP;

    /**
     * x and y are relative to the top left corner of the parent island button.
     */
    public GuiWarpButton(int buttonId, GuiIslandButton parent, Warp warp) {
        super(buttonId, 0, 0, "", parent.RES);
        PARENT = parent;
        WARP = warp;
        xPosition = parent.getActualX(warp.getGridX());
        yPosition = parent.getActualY(warp.getGridY());
        zLevel = 10;
        width = warp.getWidth();
        height = warp.getHeight();
        displayString = warp.getDisplayName();
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
        if (this.visible) {
            mc.getTextureManager().bindTexture(WARP.getWarpTextureLocation());
            GlStateManager.enableBlend();
            if (PARENT.isMouseOver()) {
                GlStateManager.color(1, 1, 1, 1);
            } else {
                GlStateManager.color(0.8F, 0.8F, 0.8F, 0.5F);
            }
            // Blend allows the texture to be drawn with transparency intact
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, zLevel);
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, 1, 1, WARP.getWidth(), WARP.getHeight(), 1, 1);
            GlStateManager.disableBlend();
            GlStateManager.resetColor();

            if (!Settings.shouldHideWarpLabelsUntilIslandHovered() || PARENT.isMouseOver()) {
                drawDisplayString(xPosition + width / 2 + 1, yPosition + height);
            }
            GlStateManager.popMatrix();
        }
    }

    public String getWarpCommand() {
        return WARP.getWarpCommand();
    }
}
