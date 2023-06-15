package ca.tirelesstraveler.skyblockwarpmenu.gui;

import ca.tirelesstraveler.skyblockwarpmenu.data.Island;
import ca.tirelesstraveler.skyblockwarpmenu.data.Settings;
import ca.tirelesstraveler.skyblockwarpmenu.data.Warp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiIslandButton extends GuiButtonExt {
    private final Island island;
    // The width of the grid squares on which to place the warps
    private final float GRID_UNIT_WIDTH;

    public GuiIslandButton(GuiFancyWarp parent, int buttonId, ScaledResolution res, Island island) {
        super(buttonId, "", res);
        this.island = island;
        island.init(res);
        xPosition = parent.getActualX(island.getGridX());
        yPosition = parent.getActualY(island.getGridY());
        width = island.getWidth();
        height = island.getHeight();
        GRID_UNIT_WIDTH = width / Warp.GRID_UNIT_WIDTH_FACTOR;
        displayString = island.getName();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY <yPosition + height;
            mc.getTextureManager().bindTexture(island.getTextureLocation());
            GlStateManager.pushMatrix();
            if (hovered) {
                GlStateManager.color(1, 1, 1, 1);
            } else {
                GlStateManager.color(0.85F, 0.85F, 0.85F, 0.5F);
            }
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, 1, 1, island.getWidth(), island.getHeight(), 1, 1);
            GlStateManager.popMatrix();
            GlStateManager.resetColor();

            if (Settings.shouldShowIslandLabels()) {
                drawDisplayString(1, xPosition + width / 2 + 1, yPosition + 1);
            }
        }
    }
    int findNearestGridX(int mouseX) {
        float quotient = (mouseX - xPosition) / GRID_UNIT_WIDTH;
        float remainder = (mouseX - xPosition) % GRID_UNIT_WIDTH;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > GRID_UNIT_WIDTH / 2 ? quotient + 1 : quotient);
    }

    int findNearestGridY(int mouseY) {
        float quotient = (mouseY - yPosition) / GRID_UNIT_WIDTH;
        float remainder = (mouseY - yPosition) % GRID_UNIT_WIDTH;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > GRID_UNIT_WIDTH / 2 ? quotient + 1 : quotient);
    }

    int getActualX(int gridX) {
        return Math.round(xPosition + GRID_UNIT_WIDTH * gridX);
    }

    int getActualY(int gridY) {
        return Math.round(yPosition + GRID_UNIT_WIDTH * gridY);
    }
}
