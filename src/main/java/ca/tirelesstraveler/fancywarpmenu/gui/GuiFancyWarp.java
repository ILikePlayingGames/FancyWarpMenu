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

import ca.tirelesstraveler.fancywarpmenu.EnvironmentDetails;
import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiFancyWarp extends GuiScreen {
    /** Delay in ms before the player can warp again if the last warp attempt failed */
    private static final long WARP_FAIL_COOL_DOWN = 500L;
    /** The amount of time in ms that the error message remains on-screen after a failed warp attempt */
    private static final long WARP_FAIL_TOOLTIP_DISPLAY_TIME = 2000L;
    private static final Logger logger = LogManager.getLogger();

    private ScaledResolution res;
    private ScaledGrid scaledGrid;
    private boolean screenDrawn;
    private long warpFailCoolDownExpiryTime;
    private long warpFailTooltipExpiryTime;
    private String warpFailMessage;

    @Override
    public void initGui() {
        if (!screenDrawn && EnvironmentDetails.isPatcherInstalled()) {
            return;
        }

        res = new ScaledResolution(mc);
        scaledGrid = new ScaledGrid(0, 0, (float) res.getScaledWidth() / Island.GRID_UNIT_WIDTH_FACTOR,
                (float) res.getScaledHeight() / Island.GRID_UNIT_HEIGHT_FACTOR);
        Warp.initDefaults(res);

        for (Island island : FancyWarpMenu.getInstance().getIslands()) {
            GuiButtonIsland islandButton = new GuiButtonIsland(this, buttonList.size(), res, island);
            buttonList.add(islandButton);

            for (Warp warp : island.getWarps()) {
                buttonList.add(new GuiButtonWarp(buttonList.size(), islandButton, warp));
            }
        }

        buttonList.add(new GuiButtonConfig(buttonList.size()));

        // Sort by z level
        buttonList.sort(null);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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

        drawDefaultBackground();

        List<GuiButtonExt> hoveredButtons = new ArrayList<>();

        // When multiple island buttons overlap, mark only the top one as hovered.
        for (GuiButton button : buttonList) {
            if (button instanceof GuiButtonIsland) {
                ((GuiButtonIsland) button).setHovered(mouseX >= button.xPosition && mouseY >= button.yPosition
                        && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height);

                if (button.isMouseOver()) {
                    hoveredButtons.add((GuiButtonExt) button);
                }
            }
        }

        for (int i = 0; i < hoveredButtons.size() - 1; i++) {
            hoveredButtons.get(i).setHovered(false);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

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
            // Draw screen resolution
            drawCenteredString(mc.fontRendererObj, String.format("%d x %d (%d)", res.getScaledWidth(), res.getScaledHeight(), res.getScaleFactor()), width / 2, height - 20, 14737632);
            // Draw version number
            String modName = FancyWarpMenu.getInstance().getModContainer().getName();
            String modVersion = FancyWarpMenu.getInstance().getModContainer().getVersion();
            drawCenteredString(mc.fontRendererObj, modName + " " + modVersion, width / 2, height - 10, 14737632);

            if (Settings.shouldDrawBorders()) {
                for (GuiButton button : buttonList) {
                    //Draw borders
                    if (button instanceof GuiButtonExt) {
                        ((GuiButtonExt) button).drawBorders(1, 1);
                    }
                }
            }

            // Shift to draw island grid instead of warp grid
            if (!isShiftKeyDown()) {
                for (GuiButton button : buttonList) {
                    // Draw island button coordinate tooltips, draw last to prevent clipping
                    if (button instanceof GuiButtonIsland && button.isMouseOver()) {
                        GuiButtonIsland islandButton = (GuiButtonIsland) button;
                        debugStrings.add(EnumChatFormatting.GREEN + button.displayString);
                        nearestX = islandButton.findNearestGridX(mouseX);
                        nearestY = islandButton.findNearestGridY(mouseY);
                        drawX = islandButton.getActualX(nearestX);
                        drawY = islandButton.getActualY(nearestY);
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
                drawX = scaledGrid.getActualX(nearestX);
                drawY = scaledGrid.getActualY(nearestY);
                drawDebugStrings(debugStrings, drawX, drawY, nearestX, nearestY, -1);
            }
        }
    }

    /**
     * Called when a warp attempt fails
     *
     * @param failMessageKey the translation key of the failure message to display on the Gui
     */
    public void onWarpFail(String failMessageKey) {
        long currentTime = Minecraft.getSystemTime();
        warpFailCoolDownExpiryTime = currentTime + WARP_FAIL_COOL_DOWN;
        warpFailTooltipExpiryTime = currentTime + WARP_FAIL_TOOLTIP_DISPLAY_TIME;
        warpFailMessage = EnumChatFormatting.RED + I18n.format(failMessageKey);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Block repeat clicks if the last warp failed
        if (Minecraft.getSystemTime() > warpFailCoolDownExpiryTime) {
            if (button instanceof GuiButtonWarp) {
                String warpCommand = ((GuiButtonWarp) button).getWarpCommand();
                sendWarpCommand(warpCommand);
            } else if (button instanceof GuiButtonIsland) {
                Island island = ((GuiButtonIsland) button).getIsland();

                if (island.getWarpCount() == 1) {
                    String warpCommand = island.getWarps().get(0).getWarpCommand();
                    sendWarpCommand(warpCommand);
                }
            } else if (button instanceof GuiButtonConfig) {
                mc.displayGuiScreen(new FancyWarpMenuConfigScreen(this));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        FancyWarpMenu mod = FancyWarpMenu.getInstance();

        if (Settings.isDebugModeEnabled()) {
            if (keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
                mc.displayGuiScreen(null);
            } else if (keyCode == Keyboard.KEY_R) {
                mod.reloadResources();
                buttonList.clear();
                initGui();
            } else if (keyCode == Keyboard.KEY_TAB) {
                Settings.setShowDebugOverlay(!Settings.shouldShowDebugOverlay());
            } else if (keyCode == Keyboard.KEY_B) {
                Settings.setDrawBorders(!Settings.shouldDrawBorders());
            }
        }
    }


    int getActualX(int gridX) {
        return scaledGrid.getActualX(gridX);
    }

    int getActualY(int gridY) {
        return scaledGrid.getActualY(gridY);
    }

    private void drawDebugStrings(ArrayList<String> debugStrings, int drawX, int drawY, int nearestGridX, int nearestGridY, int zLevel) {
        debugStrings.add("gridX: " + nearestGridX);
        debugStrings.add("gridY: " + nearestGridY);
        // zLevel of -1 means z is not relevant, like in the case of screen coordinates
        if (zLevel > -1) {
            debugStrings.add("zLevel: " + zLevel);
        }
        drawHoveringText(debugStrings, drawX, drawY);
        drawRect(drawX - 1, drawY - 1, drawX + 1, drawY + 1, Color.RED.getRGB());
    }

    private void sendWarpCommand(String warpCommand) {
        try {
            mc.ingameGUI.getChatGUI().addToSentMessages(warpCommand);
            mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(warpCommand));
        } catch (Exception e) {
            logger.error(String.format("Failed to send command \"%s\": %s", warpCommand, e.getMessage()), e);
        }
    }
}
