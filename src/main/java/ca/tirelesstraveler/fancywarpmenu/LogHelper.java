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

package ca.tirelesstraveler.fancywarpmenu;

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

public class LogHelper {
    public static void logDebug(String message, Throwable throwable) {
        logDebug(message, throwable, new Object[0]);
    }

    public static void logDebug(String message, Object... params) {
        logDebug(message, null, params);
    }

    /**
     * Workaround to allow easy toggling of debug logging in a production environment
     * <br>
     * Add {@code -Dfancywarpmenu.debug=true} to JVM arguments to enable debug logging
     *
     * @see Logger#debug(Message)
     */
    public static void logDebug(String message, Throwable throwable, Object... params) {
        String callingClassName = new Throwable().getStackTrace()[3].getClassName();
        Logger logger = LogManager.getLogger(callingClassName);

        if (FancyWarpMenu.logger.isDebugEnabled() || Settings.isDebugModeEnabled()) {
            if (throwable != null && params != null && params.length > 0) {
                throw new IllegalArgumentException("Throwable and params cannot be used together in the same call");
            }

            if (throwable != null) {
                logger.info(message, throwable);
            } else {
                logger.info(message, params);
            }
        } else {
            logger.debug(message);
        }
    }
}
