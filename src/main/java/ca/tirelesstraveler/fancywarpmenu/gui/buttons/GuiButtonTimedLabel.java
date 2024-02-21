/*
 * Copyright (c) 2024. TirelessTraveler
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

package ca.tirelesstraveler.fancywarpmenu.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * This class allows the setting of a temporary button label that reverts to the original button label
 * after a given amount of time.
 */
public class GuiButtonTimedLabel extends GuiButton {
    private String originalButtonLabel;
    private long timedLabelExpiryTime;

    public GuiButtonTimedLabel(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public GuiButtonTimedLabel(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    /**
     * Reset the temporary label to the original label after {@link #timedLabelExpiryTime} has passed.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);

        if (timedLabelExpiryTime > 0 && Minecraft.getSystemTime() > timedLabelExpiryTime) {
            timedLabelExpiryTime = -1;
            displayString = originalButtonLabel;
        }
    }

    /**
     * Set a temporary label text for this button that will revert to the original label text after the given
     * time has elapsed.
     *
     * @param labelText the temporary label text
     * @param time the time in milliseconds this label text should be shown for
     */
    public void setTimedLabel(String labelText, int time) {
        timedLabelExpiryTime = Minecraft.getSystemTime() + time;
        originalButtonLabel = displayString;
        displayString = labelText;
    }
}
