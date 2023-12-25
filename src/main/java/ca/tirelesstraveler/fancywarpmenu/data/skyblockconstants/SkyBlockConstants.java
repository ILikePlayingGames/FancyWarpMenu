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

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static ca.tirelesstraveler.fancywarpmenu.data.DataCommon.gson;

@SuppressWarnings("unused")
public class SkyBlockConstants {
    private SkyBlockConstants() {
    }

    /** Chat messages sent by the server when a warp attempt succeeds or fails */
    private WarpMessages warpMessages;
    /** Names of the warp command and its aliases */
    private List<WarpCommandVariant> warpCommandVariants;
    /** Chat messages are checked to see if they start with this string in order to see if the player joined SkyBlock */
    private String skyBlockJoinMessage;

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
}
