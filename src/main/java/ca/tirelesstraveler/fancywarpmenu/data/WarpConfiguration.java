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

package ca.tirelesstraveler.fancywarpmenu.data;

import com.google.gson.Gson;

import java.util.List;

@SuppressWarnings("unused")
public class WarpConfiguration {
    static Gson gson = new Gson();

    private List<Island> islandList;
    private WarpIcon warpIcon;
    private WarpMessages warpMessages;
    private List<String> warpCommandVariants;

    public List<Island> getIslandList() {
        return islandList;
    }

    public WarpIcon getWarpIcon() {
        return warpIcon;
    }

    public WarpMessages getWarpMessages() {
        return warpMessages;
    }

    public List<String> getWarpCommandVariants() {
        return warpCommandVariants;
    }

    public static void validateWarpConfiguration(WarpConfiguration warpConfiguration) throws IllegalArgumentException, NullPointerException {
        if (warpConfiguration == null) {
            throw new NullPointerException("Warp configuration cannot be null");
        }

        if (warpConfiguration.islandList == null || warpConfiguration.islandList.isEmpty()) {
            throw new IllegalArgumentException("Island list cannot be empty");
        }

        for (Island island : warpConfiguration.getIslandList()) {
            Island.validateIsland(island);
        }

        WarpIcon.validateWarpIcon(warpConfiguration.getWarpIcon());
        WarpMessages.validateWarpMessages(warpConfiguration.getWarpMessages());

        if (warpConfiguration.warpCommandVariants == null || warpConfiguration.warpCommandVariants.isEmpty()) {
            throw new NullPointerException("Warp command variant list cannot be empty");
        }

        for (String warpCommandVariant : warpConfiguration.warpCommandVariants) {
            if (warpCommandVariant.equals("")) {
                throw new IllegalArgumentException(String.format("Warp command variant at index %d is an empty string",
                        warpConfiguration.warpCommandVariants.indexOf(warpCommandVariant)));
            } else if (warpCommandVariant.contains("/") || warpCommandVariant.contains(" ")) {
                throw new IllegalArgumentException(String.format("Warp command variant at index %d has slashes or spaces. " +
                        "Include only the command name without slashes or spaces.",
                        warpConfiguration.warpCommandVariants.indexOf(warpCommandVariant)));
            }
        }
    }
}
