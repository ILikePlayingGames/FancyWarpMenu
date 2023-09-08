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

package ca.tirelesstraveler.fancywarpmenu.gui.buttons;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.layout.ConfigButton;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.GridRectangle;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeVersion;

@SuppressWarnings("FieldCanBeLocal")
public class GuiButtonConfig extends GuiButtonScaleTransition {
    private static final float HOVERED_SCALE = 1.2F;
    private static final long SCALE_TRANSITION_DURATION = 500;

    /** This button uses its own grid instead of the grid of the GuiScreen it belongs to since it's also attached to vanilla screens, which don't have grids */
    private final ScaledGrid scaledGrid;
    // Far right edge
    private final int GRID_X;
    // Bottom edge
    private final int GRID_Y;

    public GuiButtonConfig(int buttonId, ScaledResolution res) {
        super(buttonId, EnumChatFormatting.GREEN + I18n.format(FancyWarpMenu.getFullLanguageKey("gui.buttons.config")));
        scaledGrid = new ScaledGrid(0, 0, res.getScaledWidth(), res.getScaledHeight(), Island.GRID_UNIT_HEIGHT_FACTOR, Island.GRID_UNIT_WIDTH_FACTOR, false);
        ConfigButton configButtonSettings = FancyWarpMenu.getLayout().getConfigButton();
        configButtonSettings.init(res);
        GRID_X = configButtonSettings.getGridX();
        GRID_Y = configButtonSettings.getGridY();
        width = configButtonSettings.getWidth();
        height = configButtonSettings.getHeight();
        // Above islands and warps
        zLevel = 20;
        buttonRectangle = new GridRectangle(scaledGrid, GRID_X, GRID_Y, width, height, false, true);
        scaledGrid.addRectangle("configButton", buttonRectangle);
        backgroundTextureLocation = configButtonSettings.getTextureLocation();
        transition = new ScaleTransition(0, 1, 1);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            calculateHoverState(mouseX, mouseY);
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);

            super.drawButton(mc, mouseX, mouseY);

            if (Settings.isUpdateNotificationEnabled() && FancyWarpMenu.getUpdateCheckResult() != null && FancyWarpMenu.getUpdateCheckResult().status == ForgeVersion.Status.OUTDATED) {
                drawButtonForegroundLayer(ConfigButton.NOTIFICATION_TEXTURE_LOCATION);
            }
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean clicked = super.mousePressed(mc, mouseX, mouseY);

        if (clicked && !(mc.currentScreen instanceof GuiFancyWarp)) {
            if (!Settings.isWarpMenuEnabled()) {
                Settings.setWarpMenuEnabled(true);
                mc.thePlayer.addChatMessage(new ChatComponentTranslation("fancywarpmenu.messages.fancyWarpMenuEnabled").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            }

            FancyWarpMenu.getInstance().getWarpMenuListener().createFastTravelMenu(true);
        }

        return clicked;
    }
}
