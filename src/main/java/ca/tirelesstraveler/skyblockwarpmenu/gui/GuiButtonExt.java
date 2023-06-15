package ca.tirelesstraveler.skyblockwarpmenu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * Button class with additional utility methods
 */
public abstract class GuiButtonExt extends GuiButton {
    protected final ScaledResolution RES;

    /**
     * Constructor without coordinates for when placement is set at runtime
     */
    public GuiButtonExt(int buttonId, String buttonText, ScaledResolution res) {
        this(buttonId, 0, 0, buttonText, res);
    }
    public GuiButtonExt(int buttonId, int x, int y, String buttonText, ScaledResolution res) {
        super(buttonId, x, y, buttonText);
        RES = res;
    }

    protected void drawBorders(float scale, int borderWidth) {
        int scaledMinX = (int) (xPosition / scale);
        int scaledMinY = (int) (yPosition / scale);
        int scaledMaxX = (int) ((xPosition + width) / scale);
        int scaledMaxY = (int) ((yPosition + height) / scale);
        
        drawRect(scaledMinX, scaledMinY, scaledMaxX, scaledMinY + borderWidth, Color.black.getRGB());
        drawRect(scaledMinX, scaledMinY, scaledMinX + borderWidth, scaledMaxY, Color.black.getRGB());
        drawRect(scaledMinX, scaledMaxY - borderWidth, scaledMaxX, scaledMaxY, Color.black.getRGB());
        drawRect(scaledMaxX - borderWidth, scaledMinY, scaledMaxX, scaledMaxY, Color.black.getRGB());
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {}

    /**
     * Draw the button's display string (label).
     */
    public void drawDisplayString(float scale, int x, int y) {
        String[] lines = displayString.split("\n", 3);
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        for (int i = 0; i < lines.length; i++) {
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, lines[i],
                    scaledX, scaledY + (int) ((10 * i) / scale), 14737632);
        }
    }
}
