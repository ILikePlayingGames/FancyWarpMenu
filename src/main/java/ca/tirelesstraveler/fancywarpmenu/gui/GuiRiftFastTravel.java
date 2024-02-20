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
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonIsland;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonWarp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;

public class GuiRiftFastTravel extends GuiFancyWarp {

    public GuiRiftFastTravel(IInventory playerInventory, IInventory chestInventory, Layout layout) {
        super(playerInventory, chestInventory, layout);
        menu = Menu.PORHTAL;
        lastSlotIndexToCheck = FancyWarpMenu.getSkyBlockConstants().getLastMatchConditionInventorySlotIndex(menu);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        // Block repeat clicks if the last warp failed
        if (Minecraft.getSystemTime() > warpFailCoolDownExpiryTime) {
            if (button instanceof GuiButtonWarp) {
                GuiButtonWarp warpButton = (GuiButtonWarp) button;

                // Don't click twice for islands with only one warp
                if (warpButton.getIsland().getWarpCount() > 1) {
                    clickSlot(warpButton.getWarpSlotIndex());
                }
            } else if (button instanceof GuiButtonIsland) {
                Island island = ((GuiButtonIsland) button).getIsland();

                if (island.getWarpCount() == 1) {
                    clickSlot(island.getWarps().get(0).getSlotIndex());
                }
            }
        }
    }
}
