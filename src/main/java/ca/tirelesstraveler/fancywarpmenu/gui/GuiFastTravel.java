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
import ca.tirelesstraveler.fancywarpmenu.GameState;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonConfig;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonIsland;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonRegularWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonWarp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiFastTravel extends GuiFancyWarp {

    public GuiFastTravel(Layout layout) {
        super(layout);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButtonConfig(buttonList.size(), res));
        if (Settings.shouldShowRegularWarpMenuButton()) {
            buttonList.add(new GuiButtonRegularWarpMenu(buttonList.size(), res, scaledGrid));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Block repeat clicks if the last warp failed
        if (Minecraft.getSystemTime() > warpFailCoolDownExpiryTime) {
            if (button instanceof GuiButtonWarp) {
                GuiButtonWarp warpButton = (GuiButtonWarp) button;

                // Don't send command twice for single warp islands
                if (warpButton.getIsland().getWarpCount() > 1) {
                    String warpCommand = warpButton.getWarpCommand();
                    sendCommand(warpCommand);
                }
            } else if (button instanceof GuiButtonIsland) {
                Island island = ((GuiButtonIsland) button).getIsland();

                if (island.getWarpCount() == 1) {
                    String warpCommand = island.getWarps().get(0).getWarpCommand();
                    sendCommand(warpCommand);
                }
            } else if (button instanceof GuiButtonConfig) {
                mc.displayGuiScreen(new FancyWarpMenuConfigScreen(this));
            } else if (button instanceof GuiButtonRegularWarpMenu) {
                sendCommand("/warp");
            }
        }
    }

    @Override
    protected void addIslandButtons() {
        for (Island island : FancyWarpMenu.getLayout().getIslandList()) {
            // Conditions for hiding Jerry's Workshop from the warp menu
            if (!Settings.isDebugModeEnabled() || !Settings.shouldAlwaysShowJerryIsland()) {
                if ((!Settings.shouldShowJerryIsland()
                        || !GameState.isLateWinter())
                        && island.getWarps().get(0).getWarpCommand().equals("/savethejerrys")) {
                    continue;
                }
            }

            addIslandButton(island);
        }

        // Sort by z level
        buttonList.sort(null);
    }
}
