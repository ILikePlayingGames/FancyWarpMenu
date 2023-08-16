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

package ca.tirelesstraveler.fancywarpmenu.gui;

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FancyWarpMenuConfigScreen extends GuiConfig {
    public FancyWarpMenuConfigScreen(GuiScreen parent)
    {
        super(parent, Settings.getConfigElements(), "fancywarpmenu", "main", false, false, I18n.format("fancywarpmenu.config.title"), I18n.format("fancywarpmenu.config.subtitle"));
    }

    /**
     * A {@link GuiConfigEntries.CategoryEntry} implementation that changes the color of the label to a supplied chat color
     * <p>Example</p>
     * <p>DummyConfigElement updateAvailableCategory = new DummyConfigElement.DummyCategoryElement(CATEGORY_UPDATE_AVAILABLE, "fancywarpmenu.config.categories.updateAvailable", updateAvailableElements);</p>
     * <p>updateAvailableCategory.set(EnumChatFormatting.GREEN);</p>
     * <p>updateAvailableCategory.setConfigEntryClass(FancyWarpMenuConfigScreen.ColoredCategoryEntry.class);</p>
     */
    public static class ColoredCategoryEntry extends GuiConfigEntries.CategoryEntry
    {
        public ColoredCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);

            Object colorCodeObject = configElement.getDefault();

            if (colorCodeObject != null) {
                if (colorCodeObject instanceof EnumChatFormatting) {
                    String colorCode = colorCodeObject.toString();
                    btnSelectCategory.displayString = colorCode + btnSelectCategory.displayString;
                }
            }
        }
    }

    /**
     * A {@link GuiConfigEntries.CategoryEntry} implementation that opens a link when clicked.
     * <p>Example</p>
     * <p>DummyConfigElement downloadUpdateElement = new DummyConfigElement("downloadUpdate", EnvironmentDetails.SUPPORT_LINK, ConfigGuiType.STRING, "fancywarpmenu.config.downloadUpdate");</p>
     * <p>downloadUpdateElement.setConfigEntryClass(FancyWarpMenuConfigScreen.OpenLinkEntry.class);</p>
     */
    public static class OpenLinkEntry extends GuiConfigEntries.CategoryEntry
    {
        private final URI link;

        public OpenLinkEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);

            Object linkObject = configElement.getDefault();

            if (linkObject instanceof String) {
                try {
                    link = new URL((String) linkObject).toURI();
                    toolTip.add(link.toString());
                } catch (URISyntaxException | IOException e) {
                    throw new ReportedException(new CrashReport(linkObject + " is not a valid URL", e));
                }
            } else {
                throw new RuntimeException(linkObject + " is not a string");
            }
        }

        @Override
        protected GuiScreen buildChildScreen() throws RuntimeException {
            return null;
        }

        @Override
        public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            if (btnSelectCategory.mousePressed(this.mc, x, y)) {
                btnSelectCategory.playPressSound(mc.getSoundHandler());

                try {
                    Desktop.getDesktop().browse(link);
                } catch (IOException e) {
                    throw new ReportedException(new CrashReport("Could not open link " + link, e));
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public static class UnmodifiableStringEntry extends GuiConfigEntries.StringEntry {
        public UnmodifiableStringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        public boolean enabled() {
            return false;
        }
    }
}
