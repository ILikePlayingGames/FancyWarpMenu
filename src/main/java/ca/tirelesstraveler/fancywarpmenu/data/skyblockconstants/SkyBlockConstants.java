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

package ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants;

import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.matchconditions.MenuMatchCondition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static ca.tirelesstraveler.fancywarpmenu.resourceloaders.ResourceLoader.gson;

@SuppressWarnings("unused")
public class SkyBlockConstants {
    private SkyBlockConstants() {
    }

    /** A map with SkyBlock menus as keys and lists of conditions used to identify them as values */
    private Map<Menu, List<MenuMatchCondition>> menuMatchingMap;

    /** Chat messages sent by the server when a warp attempt succeeds or fails */
    private WarpMessages warpMessages;
    /** Names of the warp command and its aliases */
    private List<WarpCommandVariant> warpCommandVariants;
    /** Chat messages are checked to see if they start with this string in order to see if the player joined SkyBlock */
    private String skyBlockJoinMessage;

    public Map<Menu, List<MenuMatchCondition>> getMenuMatchingMap() {
        return menuMatchingMap;
    }

    public WarpMessages getWarpMessages() {
        return warpMessages;
    }

    public List<WarpCommandVariant> getWarpCommandVariants() {
        return warpCommandVariants;
    }

    public String getSkyBlockJoinMessage() {
        return skyBlockJoinMessage;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public static void validateSkyBlockConstants(SkyBlockConstants skyBlockConstants) {
        if (skyBlockConstants == null) {
            throw new NullPointerException("SkyBlock constants cannot be null");
        }

        for(Map.Entry<Menu, List<MenuMatchCondition>> menuMatchingMapEntry : skyBlockConstants.menuMatchingMap.entrySet()) {
            List<MenuMatchCondition> matchConditions = getMenuMatchConditions(menuMatchingMapEntry);

            for (MenuMatchCondition menuMatchCondition : matchConditions) {
                menuMatchCondition.validateCondition();
            }
        }

        WarpMessages.validateWarpMessages(skyBlockConstants.getWarpMessages());

        if (skyBlockConstants.warpCommandVariants == null || skyBlockConstants.warpCommandVariants.isEmpty()) {
            throw new NullPointerException("Warp command variant list cannot be empty");
        }

        for (WarpCommandVariant warpCommandVariant : skyBlockConstants.warpCommandVariants) {
            WarpCommandVariant.validateWarpCommandVariant(warpCommandVariant);
        }

        if (StringUtils.isEmpty(skyBlockConstants.skyBlockJoinMessage)) {
            throw new IllegalArgumentException("SkyBlock join message cannot be null or empty.");
        }
    }

    @NotNull
    private static List<MenuMatchCondition> getMenuMatchConditions(Map.Entry<Menu, List<MenuMatchCondition>> menuMatchingMapEntry) {
        List<MenuMatchCondition> matchConditions = menuMatchingMapEntry.getValue();

        if (matchConditions == null) {
            throw new NullPointerException(String.format("Menu %s's menu match conditions list cannot be null",
                    menuMatchingMapEntry.getKey().name()));
        } else if (matchConditions.isEmpty()) {
            throw new IllegalArgumentException(String.format("Menu %s's menu match conditions list cannot be empty",
                    menuMatchingMapEntry.getKey().name()));
        }
        return matchConditions;
    }
}
