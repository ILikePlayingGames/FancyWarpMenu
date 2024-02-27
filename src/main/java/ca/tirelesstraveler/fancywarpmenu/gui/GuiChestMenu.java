/*
 * Copyright (c) 2024. TirelessTraveler
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

import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonChestMenu;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * A GuiChest implementation that replaces the standard chest GUI with a custom menu.
 * This class also includes the ability to switch between the custom UI and the default UI using
 * {@link #setCustomUIState(boolean, boolean)}. Visibility and input processing can also be switched separately
 * between the user interfaces using {@link #setRenderCustomUI(boolean)} and {@link #setCustomUIInteractionEnabled(boolean)}.
 */
public abstract class GuiChestMenu extends GuiChest {
    protected ScaledResolution res;
    protected ScaledGrid scaledGrid;
    protected ResourceLocation backgroundTextureLocation;
    /**
     * {@code true} renders the custom UI, {@code false} renders the default chest UI
     */
    protected boolean renderCustomUI;
    /**
     * {@code true} passes interactions to the custom UI, {@code false} passes them to the default chest UI
     */
    protected boolean customUIInteractionEnabled;
    /**
     * The button the user is currently pressing and holding left click on
     */
    protected GuiButton selectedButton;

    public GuiChestMenu(IInventory playerInventory, IInventory chestInventory, ResourceLocation backgroundTextureLocation) {
        this(playerInventory, chestInventory, backgroundTextureLocation, true, true);
    }

    public GuiChestMenu(IInventory playerInventory, IInventory chestInventory, ResourceLocation backgroundTextureLocation,
                        boolean renderCustomUI, boolean customUIInteractionEnabled) {
        super(playerInventory, chestInventory);
        this.backgroundTextureLocation = backgroundTextureLocation;
        this.renderCustomUI = renderCustomUI;
        this.customUIInteractionEnabled = customUIInteractionEnabled;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (renderCustomUI) {
            if (backgroundTextureLocation != null) {
                GlStateManager.color(1F, 1F, 1F, 1F);
                mc.getTextureManager().bindTexture(backgroundTextureLocation);
                drawTexturedModalRect(0, 0, 0, 0, res.getScaledWidth(), res.getScaledHeight());
            } else {
                drawDefaultBackground();
            }
        } else {
            super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (customUIInteractionEnabled) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
                // Pass through close window key presses
                super.keyTyped(typedChar, keyCode);
            } else {
                handleCustomUIKeyboardInput(typedChar, keyCode);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Called when a mouse button is clicked. Re-implements most of the logic from
     * {@link net.minecraft.client.gui.GuiScreen#mouseClicked(int, int, int)} that is skipped when the Fancy Warp Menu
     * is enabled.
     *
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     * @param mouseButton clicked button (0 == left click is the only one that matters here)
     * @throws IOException not thrown here, may be thrown in super
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (customUIInteractionEnabled) {
            handleCustomUIMouseInput(mouseX, mouseY, mouseButton);
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!customUIInteractionEnabled) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (customUIInteractionEnabled) {
            // state == 0 is left click release
            if (selectedButton != null && state == 0) {
                selectedButton.mouseReleased(mouseX, mouseY);
                selectedButton = null;
            }
        } else {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void setRenderCustomUI(boolean renderCustomUI) {
        setCustomUIState(renderCustomUI, customUIInteractionEnabled);
    }

    protected void setCustomUIInteractionEnabled(boolean customUIInteractionEnabled) {
        setCustomUIState(renderCustomUI, customUIInteractionEnabled);
    }

    protected void setCustomUIState(boolean renderCustomUI, boolean customUIInteractionEnabled) {
        this.renderCustomUI = renderCustomUI;
        this.customUIInteractionEnabled = customUIInteractionEnabled;
        updateButtonStates();
    }

    /**
     * Updates the enable and visibility states of the buttons in {@link this#buttonList} when
     * {@code customUIInteractionEnabled} or {@code renderCustomUI} changes
     */
    protected void updateButtonStates() {
        for (GuiButton button : buttonList) {
            if (button instanceof GuiButtonChestMenu) {
                GuiButtonChestMenu buttonChestMenu = (GuiButtonChestMenu) button;

                buttonChestMenu.setEnabled(customUIInteractionEnabled);
                buttonChestMenu.setVisible(renderCustomUI);
            }
        }
    }

    /**
     * Renders the custom UI
     */
    public abstract void drawCustomUI(int mouseX, int mouseY, float partialTicks);

    /**
     * Draws the buttons for the custom UI from {@link this#buttonList}
     */
    protected abstract void drawButtons(int mouseX, int mouseY);

    /**
     * Processes key presses for the custom UI
     *
     * @param typedChar character typed
     * @param keyCode typed LWJGL key code
     */
    protected abstract void handleCustomUIKeyboardInput(char typedChar, int keyCode);

    /**
     * Processes mouse button presses for the custom UI
     *
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     * @param mouseButton mouse button pressed
     */
    protected abstract void handleCustomUIMouseInput(int mouseX, int mouseY, int mouseButton);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (renderCustomUI) {
            drawCustomUI(mouseX, mouseY, partialTicks);
        } else {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

}
