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
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FancyWarpMenuConfigScreen extends GuiConfig {
    public FancyWarpMenuConfigScreen(GuiScreen parent)
    {
        super(parent, Settings.getConfigElements(), "fancywarpmenu", "main", false, false, I18n.format("fancywarpmenu.config.title"), I18n.format("fancywarpmenu.config.subtitle"));
    }

    void openLink(URI link) {
        IChatComponent chatComponent = new ChatComponentText(null).setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link.toString())));
        handleComponentClick(chatComponent);
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
        private URI link;

        public OpenLinkEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);

            // Subclasses may use the array to add additional options in addition to the link.
            if (getConfigElement().getDefaults() != null) {
                setLink(getConfigElement().getDefaults()[0]);
            } else {
                setLink(getConfigElement().getDefault());
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

                if (owningScreen instanceof FancyWarpMenuConfigScreen) {
                    ((FancyWarpMenuConfigScreen) owningScreen).openLink(link);
                }

                return true;
            } else {
                return false;
            }
        }

        protected void setLink(Object linkObject) {
            if (linkObject instanceof String) {
                try {
                    link = new URL((String) linkObject).toURI();
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(linkObject + " is not a valid URL", e);
                }
            } else {
                throw new RuntimeException(linkObject + " is not a string");
            }
        }
    }

    /**
     * A {@link GuiConfigEntries.CategoryEntry} implementation that has a colored label and opens a link when clicked.
     */
    public static class ColoredOpenLinkEntry extends OpenLinkEntry
    {
        /**
         * Creates a new instance of {@code ColoredOpenLinkEntry}.
         * Set the value of prop to an {@code Object} array with the link at index 0 and an instance of {@code EnumChatFormatting} at index 1 for the color.
         *
         * @param owningScreen parent screen
         * @param owningEntryList parent config entry list
         * @param prop property containing the link to open and the color for the label
         */
        public ColoredOpenLinkEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
            setColor(getConfigElement().getDefaults()[1]);
        }

        private void setColor(Object colorCodeObject) {
            if (colorCodeObject != null) {
                if (colorCodeObject instanceof EnumChatFormatting) {
                    String colorCode = colorCodeObject.toString();
                    btnSelectCategory.displayString = colorCode + btnSelectCategory.displayString;
                } else {
                    throw new RuntimeException(colorCodeObject + " is not an instance of EnumChatFormatting");
                }
            }
        }
    }
}
