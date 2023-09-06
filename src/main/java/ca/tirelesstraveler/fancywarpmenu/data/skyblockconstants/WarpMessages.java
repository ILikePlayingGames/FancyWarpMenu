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

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class WarpMessages {
    private List<String> warpSuccessMessages;
    /** key: chat message, value: translation key of message to show in warp menu */
    private Map<String, String> warpFailMessages;

    private WarpMessages() {
    }

    public List<String> getWarpSuccessMessages() {
        return warpSuccessMessages;
    }

    public Map<String, String> getWarpFailMessages() {
        return warpFailMessages;
    }

    public static void validateWarpMessages(WarpMessages warpMessages) throws IllegalArgumentException, NullPointerException {
        if (warpMessages == null) {
            throw new NullPointerException("Warp messages cannot be null");
        }

        List<String> successMessages = warpMessages.getWarpSuccessMessages();
        if (successMessages == null || successMessages.isEmpty()) {
            throw new IllegalArgumentException("Warp success message list cannot be empty");
        }

        Map<String, String> failMessages = warpMessages.getWarpFailMessages();
        if (failMessages == null || failMessages.isEmpty()) {
            throw new IllegalArgumentException("Warp fail message list cannot be empty");
        }
    }
}
