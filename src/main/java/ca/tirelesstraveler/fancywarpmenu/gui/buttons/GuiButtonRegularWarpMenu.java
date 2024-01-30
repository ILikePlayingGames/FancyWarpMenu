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
import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.layout.RegularWarpMenuButton;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.GridRectangle;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import ca.tirelesstraveler.fancywarpmenu.gui.transitions.ScaleTransition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("FieldCanBeLocal")
public class GuiButtonRegularWarpMenu extends GuiButtonScaleTransition {
    private static final float HOVERED_SCALE = 1.2F;
    private static final long SCALE_TRANSITION_DURATION = 500;
    // Far right edge
    private final int GRID_X;
    // Bottom edge
    private final int GRID_Y;

    public GuiButtonRegularWarpMenu(Layout layout, int buttonId, ScaledResolution res, ScaledGrid scaledGrid) {
        super(buttonId, EnumChatFormatting.GREEN + I18n.format(FancyWarpMenu.getFullLanguageKey("gui.buttons.regularWarpMenu")));
        RegularWarpMenuButton regularWarpMenuButtonSettings = layout.getRegularWarpMenuButton();
        regularWarpMenuButtonSettings.init(res);
        GRID_X = regularWarpMenuButtonSettings.getGridX();
        GRID_Y = regularWarpMenuButtonSettings.getGridY();
        width = regularWarpMenuButtonSettings.getWidth();
        height = regularWarpMenuButtonSettings.getHeight();
        // Above islands and warps
        zLevel = 20;
        buttonRectangle = new GridRectangle(scaledGrid, GRID_X, GRID_Y, width, height, false, true);
        scaledGrid.addRectangle("regularWarpMenuButton", buttonRectangle);
        backgroundTextureLocation = regularWarpMenuButtonSettings.getTextureLocation();
        transition = new ScaleTransition(0, 1, 1);
        displayString = String.join("\n", Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(displayString, width * 3));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            calculateHoverState(mouseX, mouseY);
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);

            super.drawButton(mc, mouseX, mouseY);
        }
    }
}
