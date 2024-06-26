/*
 * Copyright (c) 2023-2024. TirelessTraveler
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

package ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu;

/**
 * In-game menus, not serialized
 */
public enum Menu {
    /** Value used when player is not in a menu or in an unknown or irrelevant menu */
    NONE(""),
    SKYBLOCK_MENU("SkyBlock Menu"),
    FAST_TRAVEL("Fast Travel"),
    PORHTAL("Porhtal");

    /**
     * Menu name as displayed at the top of the {@code GuiChest}
     */
    final String MENU_DISPLAY_NAME;

    Menu(String menuDisplayName) {
        this.MENU_DISPLAY_NAME = menuDisplayName;
    }

    public String getMenuDisplayName() {
        return MENU_DISPLAY_NAME;
    }
}
