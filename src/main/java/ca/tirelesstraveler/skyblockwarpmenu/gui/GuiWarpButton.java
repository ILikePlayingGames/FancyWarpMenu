package ca.tirelesstraveler.skyblockwarpmenu.gui;

import ca.tirelesstraveler.skyblockwarpmenu.data.Warp;
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
        width = warp.getWidth();
        height = warp.getHeight();
        displayString = warp.getDisplayName();
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY <yPosition + height;
            mc.getTextureManager().bindTexture(WARP.getWarpTextureLocation());
            GlStateManager.enableBlend();
            if (PARENT.isMouseOver()) {
                GlStateManager.color(1, 1, 1, 1);
            } else {
                GlStateManager.color(0.8F, 0.8F, 0.8F, 0.5F);
            }
            // Blend allows the texture to be drawn with transparency intact
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, 1, 1, WARP.getWidth(), WARP.getHeight(), 1, 1);
            GlStateManager.disableBlend();
            GlStateManager.resetColor();

            drawDisplayString(1, xPosition + width / 2 + 1, yPosition + height);
        }
    }

    public String getWarpCommand() {
        return WARP.getWarpCommand();
    }
}
