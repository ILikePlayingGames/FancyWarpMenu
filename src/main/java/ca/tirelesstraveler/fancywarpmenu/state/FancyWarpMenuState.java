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

package ca.tirelesstraveler.fancywarpmenu.state;

import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import net.minecraft.client.Minecraft;

/**
 * This class stores the current state of the Fancy Warp Menu.
 */
public class FancyWarpMenuState {
    private static Layout overworldLayout;
    private static Layout riftLayout;
    private static boolean openConfigMenuRequested;

    /**
     * Gets the layout corresponding to the given SkyBlock menu.
     *
     * @param menu the SkyBlock menu to get a layout for
     * @return {@code riftLayout} if {@code Menu.PORHTAL} is provided, {@code overworldLayout} otherwise
     */
    public static Layout getLayoutForMenu(Menu menu) {
        if (menu == Menu.PORHTAL) {
            return getRiftLayout();
        } else {
            return getOverworldLayout();
        }
    }

    public static Layout getOverworldLayout() {
        return overworldLayout;
    }

    public static Layout getRiftLayout() {
        return riftLayout;
    }

    public static boolean isFancyWarpMenuOpen() {
        return Minecraft.getMinecraft().currentScreen instanceof GuiFancyWarp;
    }

    public static boolean isOpenConfigMenuRequested() {
        return openConfigMenuRequested;
    }

    public static void setOverworldLayout(Layout overworldLayout) {
        FancyWarpMenuState.overworldLayout = overworldLayout;
    }

    public static void setRiftLayout(Layout riftLayout) {
        FancyWarpMenuState.riftLayout = riftLayout;
    }

    public static void setOpenConfigMenuRequested(boolean openConfigMenuRequested) {
        FancyWarpMenuState.openConfigMenuRequested = openConfigMenuRequested;
    }
}
