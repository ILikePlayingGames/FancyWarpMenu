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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("SuspiciousNameCombination")
public class GuiButtonConfig extends GuiButtonExt {
    private static final float HOVERED_SCALE = 1.2F;
    private static ResourceLocation buttonTextureLocation;

    public GuiButtonConfig(int buttonId) {
        super(buttonId, I18n.format("fancywarpmenu.ui.buttons.config"));
        if (buttonTextureLocation == null) {
            String buttonTexturePath = "fancywarpmenu:textures/gui/Logo.png";
            buttonTextureLocation = new ResourceLocation(buttonTexturePath);
        }

        width = height = (int) (Minecraft.getMinecraft().currentScreen.width * 0.05);
        xPosition = (int) (Minecraft.getMinecraft().currentScreen.width - width * 1.3);
        yPosition = (int) (Minecraft.getMinecraft().currentScreen.height - height * 1.3);

        // Above islands and warps
        zLevel = 20;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        if (this.visible) {
            int drawWidth = hovered ? (int) (width * HOVERED_SCALE) : width;
            int drawX = hovered ? xPosition - (drawWidth - width) : xPosition;
            int drawY = hovered ? yPosition - (drawWidth - height) : yPosition;

            mc.getTextureManager().bindTexture(buttonTextureLocation);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, zLevel);
            drawScaledCustomSizeModalRect(drawX, drawY, 0, 0, 1, 1, drawWidth, drawWidth, 1, 1);
            GlStateManager.popMatrix();
        }
    }
}
