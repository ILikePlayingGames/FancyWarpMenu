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

package ca.tirelesstraveler.fancywarpmenu.data.layout;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Class that holds the settings for drawing the config button that opens the mod's settings
 */
@SuppressWarnings("unused")
public class ConfigButton extends Button {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), "textures/gui/Logo.png");
    /** Overlay texture rendered when mod is outdated */
    public static final ResourceLocation NOTIFICATION_TEXTURE_LOCATION = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), "textures/gui/Notification.png");

    private ConfigButton(){}

    public ResourceLocation getTextureLocation() {
        return TEXTURE_LOCATION;
    }

    public static void validateConfigButtonIcon(ConfigButton configButton) throws IllegalArgumentException, NullPointerException {
        if (configButton == null) {
            throw new NullPointerException("Config button settings cannot be null");
        }

        Button.validateButtonIcon(configButton);

        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(ConfigButton.TEXTURE_LOCATION);
            IOUtils.closeQuietly(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Config button texture not found at %s", ConfigButton.TEXTURE_LOCATION));
        }

        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(ConfigButton.NOTIFICATION_TEXTURE_LOCATION);
            IOUtils.closeQuietly(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Config button notification texture not found at %s", ConfigButton.NOTIFICATION_TEXTURE_LOCATION));
        }
    }
}
