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

import ca.tirelesstraveler.fancywarpmenu.gui.grid.GridRectangle;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public abstract class GuiButtonScaleTransition extends GuiButtonExt {
    private static final float HOVERED_BRIGHTNESS = 1F;
    private static final float UN_HOVERED_BRIGHTNESS = 0.9F;

    /** This rectangle determines the button's placement on its {@code GuiScreen}'s {@code ScaledGrid} */
    protected GridRectangle buttonRectangle;
    protected ScaleTransition transition;
    protected float scaledXPosition;
    protected float scaledYPosition;
    protected float scaledWidth;
    protected float scaledHeight;

    public GuiButtonScaleTransition(int buttonId, String buttonText) {
        super(buttonId, buttonText);
    }

    public GuiButtonScaleTransition(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    /**
     * Draw a border around this button with the given color. This stutters due to using int instead of float.
     *
     * @param color color of the border
     */
    public void drawBorder(Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, zLevel);
        drawHorizontalLine((int) scaledXPosition, (int) (scaledXPosition + scaledWidth), (int) scaledYPosition, color.getRGB());
        drawVerticalLine((int) scaledXPosition, (int) scaledYPosition, (int) (scaledYPosition + scaledHeight), color.getRGB());
        drawHorizontalLine((int) scaledXPosition, (int) (scaledXPosition + scaledWidth), (int) (scaledYPosition + scaledHeight), color.getRGB());
        drawVerticalLine((int) (scaledXPosition + scaledWidth), (int) scaledYPosition, (int) (scaledYPosition + scaledHeight), color.getRGB());
        GlStateManager.popMatrix();
    }

    /**
     * Button hover calculations adapted for float values instead of int
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= scaledXPosition && mouseY >= scaledYPosition && mouseX <= scaledXPosition + scaledWidth && mouseY <= this.scaledYPosition + scaledHeight;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return enabled && visible && mouseX >= scaledXPosition && mouseY >= scaledYPosition && mouseX <= scaledXPosition + scaledWidth && mouseY <= this.scaledYPosition + scaledHeight;
    }

    public void drawButtonForegroundLayer(ResourceLocation foregroundTextureLocation) {
        if (foregroundTextureLocation != null && hovered) {
            drawButtonTexture(foregroundTextureLocation);
        }
    }

    /**
     * Draws the provided texture at ({@code this.scaledXPosition}, {@code this.scaledYPosition}, {@code this.zLevel}) at a size of ({@code this.scaledWidth})x({@code this.scaledHeight})
     *
     * @param textureLocation location of texture to draw
     */
    protected void drawButtonTexture(ResourceLocation textureLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureLocation);
        GlStateManager.enableBlend();
        // Blend allows the texture to be drawn with transparency intact
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        if (hovered) {
            GlStateManager.color( HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS);
        } else {
            GlStateManager.color(UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS);
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(scaledXPosition, scaledYPosition + scaledHeight, zLevel).tex(0, 1).endVertex();
        worldRenderer.pos(scaledXPosition + scaledWidth, scaledYPosition + scaledHeight, zLevel).tex(1, 1).endVertex();
        worldRenderer.pos(scaledXPosition + scaledWidth, scaledYPosition, zLevel).tex(1, 0).endVertex();
        worldRenderer.pos(scaledXPosition, scaledYPosition, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1);
    }

    /**
     * Draws the display string for this button relative to its top-left corner.
     * Offsets should not be pre-scaled. This method scales before rendering.
     *
     * @param mc Minecraft
     * @param xOffset x-offset from button left
     * @param yOffset y-offset from button top
     */
    public void drawDisplayString(Minecraft mc, float xOffset, float yOffset) {
        String[] lines = displayString.split("\n");

        if (hovered) {
            GlStateManager.color( HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS);
        } else {
            GlStateManager.color(UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS);
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(transition.getCurrentScale(), transition.getCurrentScale(), 1);
        GlStateManager.translate(scaledXPosition / transition.getCurrentScale(), scaledYPosition / transition.getCurrentScale(), zLevel);
        for (int i = 0; i < lines.length; i++) {
            mc.fontRendererObj.drawStringWithShadow(lines[i], xOffset - mc.fontRendererObj.getStringWidth(lines[i]) / 2F, yOffset + (mc.fontRendererObj.FONT_HEIGHT + 1) * i, Color.WHITE.getRGB());
        }
        GlStateManager.popMatrix();
        GlStateManager.color(1,1,1);
    }

    /**
     * Recalculates the progress of {@code transition} towards its end time and reverses the direction of transition if
     * this button's hover state changes
     *
     * @param scaleTransitionDuration duration from transition start to finish
     * @param hoveredScale final scale when the transition when the button is hovered is finished
     */
    public void transitionStep(long scaleTransitionDuration, float hoveredScale) {
        transition.step();

        if (hovered) {
            if (transition.getEndScale() == 1) {
                transition = new ScaleTransition((long) (transition.getProgress() * scaleTransitionDuration), transition.getCurrentScale(), hoveredScale);
            }
        } else {
            if (transition.getEndScale() == hoveredScale) {
                transition = new ScaleTransition((long) (transition.getProgress() * scaleTransitionDuration), transition.getCurrentScale(), 1);
            }
        }
    }
}
