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
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Warp;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.SkyBlockConstants;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.*;
import ca.tirelesstraveler.fancywarpmenu.gui.grid.ScaledGrid;
import ca.tirelesstraveler.fancywarpmenu.listeners.InventoryChangeListener;
import ca.tirelesstraveler.fancywarpmenu.state.EnvironmentDetails;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import ca.tirelesstraveler.fancywarpmenu.utils.GameChecks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GuiFancyWarp extends GuiChestMenu {
    protected static final FancyWarpMenu modInstance = FancyWarpMenu.getInstance();
    private static final Logger logger = LogManager.getLogger();
    /** Delay in ms before the player can warp again if the last warp attempt failed */
    private static final long WARP_FAIL_COOL_DOWN = 500L;
    /** The amount of time in ms that the error message remains on-screen after a failed warp attempt */
    private static final long WARP_FAIL_TOOLTIP_DISPLAY_TIME = 2000L;

    protected Menu menu;
    protected Layout layout;
    private final InventoryBasic chestInventory;
    private GuiButton configButton;
    private InventoryChangeListener inventoryListener;
    private String warpFailMessage;
    /**
     * Last slot index in the {@link ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.ItemMatchCondition}
     * list for {@link #menu}
     *
     * @see SkyBlockConstants#getMenuMatchingMap()
     */
    protected int lastSlotIndexToCheck;
    protected long warpFailCoolDownExpiryTime;
    private boolean screenDrawn;
    private long warpFailTooltipExpiryTime;

    public GuiFancyWarp(IInventory playerInventory, IInventory chestInventory, Layout layout) {
        super(playerInventory, chestInventory);
        this.layout = layout;
        this.chestInventory = (InventoryBasic) chestInventory;

        if (Settings.isWarpMenuEnabled()) {
            inventoryListener = new InventoryChangeListener(new ChestItemChangeCallback(this));
            this.chestInventory.addInventoryChangeListener(inventoryListener);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (!screenDrawn && EnvironmentDetails.isPatcherInstalled()) {
            return;
        }

        res = new ScaledResolution(mc);
        scaledGrid = new ScaledGrid(0, 0, res.getScaledWidth(), res.getScaledHeight(), Island.GRID_UNIT_HEIGHT_FACTOR, Island.GRID_UNIT_WIDTH_FACTOR, false);
        Warp.initDefaults(res);

        configButton = new GuiButtonConfig(layout, buttonList.size(), res);
        buttonList.add(configButton);
        if (Settings.shouldShowRegularWarpMenuButton()) {
            buttonList.add(new GuiButtonRegularWarpMenu(layout, buttonList.size(), res, scaledGrid));
        }

        addIslandButtons();
        updateButtonStates();
    }

    public ScaledGrid getScaledGrid() {
        return scaledGrid;
    }

    @Override
    public void drawCustomUI(int mouseX, int mouseY, float partialTicks) {
        /*
        Patcher inventory scale doesn't reset inventory scale until the first draw of the screen after
        the inventory is closed. Setting res in initGui would use Patcher's scaled resolution instead of Minecraft's
        resolution.
        */
        if (!screenDrawn && EnvironmentDetails.isPatcherInstalled()) {
            screenDrawn = true;
            initGui();
            this.width = res.getScaledWidth();
            this.height = res.getScaledHeight();
        }

        drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        List<GuiButtonChestMenu> hoveredButtons = new ArrayList<>();

        // When multiple island buttons overlap, mark only the top one as hovered.
        for (GuiButton button : buttonList) {
            if (button instanceof GuiButtonIsland) {
                GuiButtonIsland islandButton = (GuiButtonIsland) button;
                islandButton.setHovered(mouseX >= islandButton.getScaledXPosition() &&
                        mouseY >= islandButton.getScaledYPosition() &&
                        mouseX <= islandButton.getScaledXPosition() + islandButton.getScaledWidth() &&
                        mouseY <= ((GuiButtonIsland) button).getScaledYPosition() + islandButton.getScaledHeight());

                if (button.isMouseOver()) {
                    hoveredButtons.add((GuiButtonChestMenu) button);
                }
            }
        }

        for (int i = 0; i < hoveredButtons.size() - 1; i++) {
            hoveredButtons.get(i).setHovered(false);
        }

        drawButtons(mouseX, mouseY);

        // Draw warp fail tooltip
        if (Minecraft.getSystemTime() <= warpFailTooltipExpiryTime && warpFailMessage != null) {
            drawHoveringText(Collections.singletonList(warpFailMessage), mouseX, mouseY);
        }

        if (Settings.isDebugModeEnabled() && Settings.shouldShowDebugOverlay()) {
            ArrayList<String> debugStrings = new ArrayList<>();
            int drawX;
            int drawY;
            int nearestX;
            int nearestY;
            boolean tooltipDrawn = false;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 20);
            // Draw screen resolution
            drawCenteredString(mc.fontRendererObj, String.format("%d x %d (%d)", res.getScaledWidth(), res.getScaledHeight(), res.getScaleFactor()), width / 2, height - 20, 14737632);
            // Draw version number
            String modName = FancyWarpMenu.getInstance().getModContainer().getName();
            String modVersion = FancyWarpMenu.getInstance().getModContainer().getVersion();
            drawCenteredString(mc.fontRendererObj, modName + " " + modVersion, width / 2, height - 10, 14737632);

            // Shift to draw island grid instead of warp grid
            if (!isShiftKeyDown()) {
                for (GuiButton button : buttonList) {
                    // Draw island button coordinate tooltips, draw last to prevent clipping
                    if (button instanceof GuiButtonIsland && button.isMouseOver()) {
                        GuiButtonIsland islandButton = (GuiButtonIsland) button;
                        debugStrings.add(EnumChatFormatting.GREEN + button.displayString);
                        nearestX = islandButton.getScaledGrid().findNearestGridX(mouseX);
                        nearestY = islandButton.getScaledGrid().findNearestGridY(mouseY);
                        drawX = (int) islandButton.getScaledGrid().getActualX(nearestX);
                        drawY = (int) islandButton.getScaledGrid().getActualY(nearestY);
                        drawDebugStrings(debugStrings, drawX, drawY, nearestX, nearestY, islandButton.getZLevel());
                        tooltipDrawn = true;
                        break;
                    }
                }
            }

            // Draw screen coordinate tooltips
            if (!tooltipDrawn) {
                nearestX = scaledGrid.findNearestGridX(mouseX);
                nearestY = scaledGrid.findNearestGridY(mouseY);
                drawX = (int) scaledGrid.getActualX(nearestX);
                drawY = (int) scaledGrid.getActualY(nearestY);
                drawDebugStrings(debugStrings, drawX, drawY, nearestX, nearestY, -1);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Called when a warp attempt fails
     *
     * @param failMessageKey the translation key of the failure message to display on the Gui
     * @param replacements replacement objects to substitute in place of placeholders in the translated message
     */
    public void onWarpFail(String failMessageKey, Object... replacements) {
        long currentTime = Minecraft.getSystemTime();
        warpFailCoolDownExpiryTime = currentTime + WARP_FAIL_COOL_DOWN;
        warpFailTooltipExpiryTime = currentTime + WARP_FAIL_TOOLTIP_DISPLAY_TIME;
        warpFailMessage = EnumChatFormatting.RED + I18n.format(failMessageKey, replacements);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (Settings.isWarpMenuEnabled()) {
            if (button instanceof GuiButtonConfig) {
                FancyWarpMenuState.setOpenConfigMenuRequested(true);
                mc.thePlayer.closeScreen();
            } else if (button instanceof GuiButtonRegularWarpMenu) {
                Settings.setWarpMenuEnabled(false);
                setCustomUIState(false, false);
            }
        } else if (button instanceof GuiButtonConfig) {
            Settings.setWarpMenuEnabled(true);
            mc.thePlayer.addChatMessage(new ChatComponentTranslation(
                    "fancywarpmenu.messages.fancyWarpMenuEnabled").setChatStyle(
                    new ChatStyle().setColor(EnumChatFormatting.GREEN)));

            if (GameChecks.menuItemsMatch(menu, chestInventory)) {
                setCustomUIState(true, true);
            } else {
                FancyWarpMenuState.setOpenConfigMenuRequested(true);
                mc.thePlayer.closeScreen();
            }
        }
    }

    @Override
    protected void handleCustomUIMouseInput(int mouseX, int mouseY, int mouseButton) {
        // Left click
        if (mouseButton == 0) {
            for (GuiButton button : buttonList) {
                if (handlePotentialButtonPress(button, mouseX, mouseY)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void handleCustomUIKeyboardInput(char typedChar, int keyCode) {
        if (Settings.isDebugModeEnabled()) {
            if (keyCode == Keyboard.KEY_R) {
                // Layout isn't loaded into the currently opened screen. That must be implemented in subclasses.
                if (isShiftKeyDown()) {
                    modInstance.reloadResources();
                } else {
                    modInstance.reloadLayouts();
                }
            } else if (keyCode == Keyboard.KEY_TAB) {
                Settings.setShowDebugOverlay(!Settings.shouldShowDebugOverlay());
            } else if (keyCode == Keyboard.KEY_B) {
                Settings.setDrawBorders(!Settings.shouldDrawBorders());
            }
        }
    }

    /**
     * Called when a mouse button is clicked.
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
            // Don't send a C0EPacketClickWindow when clicking the config button while the custom UI is disabled
            if (mouseButton == 0 && configButton.isMouseOver()) {
                actionPerformed(configButton);
            } else {
                super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    protected void addIslandButtons() {
        for (Island island : layout.getIslandList()) {
            addIslandButton(island);
        }

        // Sort by z level
        buttonList.sort(null);
    }

    protected void addIslandButton(Island island) {
        GuiButtonIsland islandButton = new GuiButtonIsland(this, buttonList.size(), res, island);
        buttonList.add(islandButton);

        for (Warp warp : island.getWarps()) {
            if (Settings.shouldHideWarpLabelForIslandsWithOneWarp() && island.getWarpCount() == 1) {
                warp.setHideDisplayName(true);
            }

            if (!Settings.shouldHideUnobtainableWarps() || !warp.requiresSpecialGameMode()) {
                buttonList.add(new GuiButtonWarp(buttonList.size(), islandButton, warp));
            }
        }
    }

    /**
     * Left-clicks an inventory slot at the given index in the current screen if the current screen
     * is an instance of {@link GuiChest} and the mouse is not already holding an item
     *
     * @param slotIndex the index of the inventory slot to click
     */
    protected void clickSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < inventorySlots.inventorySlots.size()) {
            Slot slotToClick = inventorySlots.getSlot(slotIndex);

            if (slotToClick.getHasStack()) {
                if (mc.thePlayer.inventory.getItemStack() == null) {
                    // Left click no shift
                    handleMouseClick(inventorySlots.getSlot(slotIndex), slotIndex, 0, 0);
                } else {
                    onWarpFail(FancyWarpMenu.getFullLanguageKey("errors.mouseIsHoldingItem"));
                }
            } else {
                onWarpFail(FancyWarpMenu.getFullLanguageKey("errors.slotHasNoItem"), slotIndex);
            }
        } else {
            onWarpFail(FancyWarpMenu.getFullLanguageKey("errors.slotNumberOutOfBounds"), slotIndex);
        }
    }

    protected void drawButtons(int mouseX, int mouseY) {
        for (GuiButton button : buttonList) {
            if (button instanceof GuiButtonConfig || Settings.isWarpMenuEnabled()) {
                button.drawButton(mc, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void updateButtonStates() {
        for (GuiButton button : buttonList) {
            // The config button is active on both the custom and default UI.
            if (button instanceof GuiButtonChestMenu && !(button instanceof GuiButtonConfig)) {
                GuiButtonChestMenu buttonChestMenu = (GuiButtonChestMenu) button;

                buttonChestMenu.setEnabled(customUIInteractionEnabled);
                buttonChestMenu.setVisible(renderCustomUI);
            }
        }
    }

    /**
     * Called whenever an item in the inventory of the {@link GuiChestMenu} changes.
     * This is used to enable the fancy warp menu when the SkyBlock menu the player has open is a warp menu.
     *
     * @param triggerCount number of times {@link #inventoryListener} was triggered
     */
    private void onChestItemChange(int triggerCount) {
        /*
        Don't start checking until the item in the last slot to check has been loaded.

        The item change event is triggered twice for each item, and the item stack is set on the 2nd time it's
        triggered. For example, slot 53 is actually set on the 106th time the item change event triggers.
         */
        if (triggerCount > lastSlotIndexToCheck * 2 && chestInventory.getStackInSlot(lastSlotIndexToCheck) != null) {
            if (GameChecks.menuItemsMatch(menu, chestInventory)) {
                setCustomUIState(true, true);
            }

            chestInventory.removeInventoryChangeListener(inventoryListener);
        }
    }

    /**
     * Tests for and handles a {@code GuiButton} press if the provided button was pressed. This re-implements most of
     * the logic from {@link net.minecraft.client.gui.GuiScreen#mouseClicked(int, int, int)} that is skipped when
     * the custom UI processes input instead of the default UI.
     *
     * @param button {@code GuiButton} to check
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     * @return {@code true} if the press was cancelled via an event listener, {@code false} otherwise
     */
    private boolean handlePotentialButtonPress(GuiButton button, int mouseX, int mouseY) {
        if (button.mousePressed(mc, mouseX, mouseY)) {
            GuiScreenEvent.ActionPerformedEvent.Pre preEvent = new GuiScreenEvent.ActionPerformedEvent.Pre(this, button, buttonList);

            if (MinecraftForge.EVENT_BUS.post(preEvent)) {
                return true;
            }

            GuiButton resultButton = preEvent.button;
            selectedButton = resultButton;
            resultButton.playPressSound(mc.getSoundHandler());
            this.actionPerformed(resultButton);
            if (mc.currentScreen == this) {
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, resultButton, buttonList));
            }
        }

        return false;
    }

    private void drawDebugStrings(ArrayList<String> debugStrings, int drawX, int drawY, int nearestGridX, int nearestGridY, int zLevel) {
        debugStrings.add("gridX: " + nearestGridX);
        debugStrings.add("gridY: " + nearestGridY);
        // zLevel of -1 means z is not relevant, like in the case of screen coordinates
        if (zLevel > -1) {
            debugStrings.add("zLevel: " + zLevel);
        }
        drawHoveringText(debugStrings, drawX, drawY);
        drawRect(drawX - 2, drawY - 2, drawX + 2, drawY + 2, Color.RED.getRGB());
    }

    /**
     * A callback called when any item in the chest it is attached to changes
     */
    private static class ChestItemChangeCallback implements Consumer<InventoryBasic> {
        private final GuiFancyWarp GUI_FANCY_WARP;
        private int triggerCount;

        ChestItemChangeCallback(GuiFancyWarp guiFancyWarp) {
            GUI_FANCY_WARP = guiFancyWarp;
            triggerCount = 0;
        }

        @Override
        public void accept(InventoryBasic chestInventory) {
            triggerCount++;
            GUI_FANCY_WARP.onChestItemChange(triggerCount);
        }
    }
}
