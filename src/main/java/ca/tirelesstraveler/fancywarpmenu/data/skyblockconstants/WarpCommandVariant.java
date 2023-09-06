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

@SuppressWarnings("unused")
public class WarpCommandVariant {
    private String command;
    private WarpCommandType type;

    private WarpCommandVariant(){
    }

    public String getCommand() {
        return command;
    }

    public WarpCommandType getType() {
        return type;
    }

    public enum WarpCommandType {
        /**
         * An alias that works like the actual /warp command
         */
        ALIAS,
        /**
         * A shortcut to teleport to a single warp
         */
        WARP
    }

    public static void validateWarpCommandVariant(WarpCommandVariant warpCommandVariant) {
        if (warpCommandVariant == null) {
            throw new NullPointerException("Warp command variant cannot be null");
        }

        if (warpCommandVariant.command == null) {
            throw new NullPointerException("Warp command variant's command cannot be null");
        }

        if (warpCommandVariant.type == null) {
            throw new NullPointerException("Warp command variant's command type cannot be null");
        }
    }
}
