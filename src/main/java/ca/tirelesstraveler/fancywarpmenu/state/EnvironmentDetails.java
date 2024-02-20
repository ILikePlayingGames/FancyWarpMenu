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

/**
 * Information about the local environment at runtime, for constants and compatibility with other mods
 */
public class EnvironmentDetails {
    public static final String SUPPORT_LINK = "https://discord.gg/tXFf9umfA9";
    private static boolean deobfuscatedEnvironment;
    /**
     *  Whether the Sk1er Patcher mod is installed. Patcher's GUI scale feature causes the Fancy Warp Menu to not be
     *  scaled properly so the menu must be initialized differently when Patcher is present.
     * */
    private static boolean patcherInstalled;

    private EnvironmentDetails() {
    }

    public static boolean isDeobfuscatedEnvironment() {
        return deobfuscatedEnvironment;
    }

    public static boolean isPatcherInstalled() {
        return patcherInstalled;
    }

    public static void setDeobfuscatedEnvironment(boolean deobfuscatedEnvironment) {
        EnvironmentDetails.deobfuscatedEnvironment = deobfuscatedEnvironment;
    }

    public static void setPatcherInstalled(boolean patcherInstalled) {
        EnvironmentDetails.patcherInstalled = patcherInstalled;
    }
}
