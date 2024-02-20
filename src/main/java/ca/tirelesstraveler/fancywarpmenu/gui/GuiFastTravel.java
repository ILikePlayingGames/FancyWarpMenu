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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonIsland;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonWarp;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import ca.tirelesstraveler.fancywarpmenu.utils.GameChecks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiFastTravel extends GuiFancyWarp {

    public GuiFastTravel(IInventory playerInventory, IInventory chestInventory, Layout layout) {
        super(playerInventory, chestInventory, layout);
        menu = Menu.FAST_TRAVEL;
        lastSlotIndexToCheck = FancyWarpMenu.getSkyBlockConstants().getLastMatchConditionInventorySlotIndex(menu);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        // Block repeat clicks if the last warp failed
        if (Minecraft.getSystemTime() > warpFailCoolDownExpiryTime) {
            if (button instanceof GuiButtonWarp) {
                GuiButtonWarp warpButton = (GuiButtonWarp) button;

                // Don't send command twice for single warp islands
                if (warpButton.getIsland().getWarpCount() > 1) {
                    String warpCommand = warpButton.getWarpCommand();
                    mc.thePlayer.sendChatMessage(warpCommand);
                }
            } else if (button instanceof GuiButtonIsland) {
                Island island = ((GuiButtonIsland) button).getIsland();

                if (island.getWarpCount() == 1) {
                    String warpCommand = island.getWarps().get(0).getWarpCommand();
                    mc.thePlayer.sendChatMessage(warpCommand);
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (Settings.isDebugModeEnabled() && keyCode == Keyboard.KEY_R) {
            this.layout = FancyWarpMenuState.getOverworldLayout();
            this.onResize(mc, res.getScaledWidth(), res.getScaledHeight());
        }
    }

    @Override
    protected void addIslandButtons() {
        for (Island island : FancyWarpMenuState.getOverworldLayout().getIslandList()) {
            addIslandButton(island);
        }

        // Sort by z level
        buttonList.sort(null);
    }

    @Override
    protected void updateButtonStates() {
        // Season is needed to show/hide the Jerry island button.
        GameChecks.checkSeason();

        super.updateButtonStates();
    }
}
