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

import static ca.tirelesstraveler.fancywarpmenu.data.DataCommon.gson;

/**
 * Class that holds the settings for drawing the warp icon (portal)
 */
@SuppressWarnings("unused")
public class WarpIcon {
    /** Path to the warp button texture relative to {@code resources/assets/fancywarpmenu} */
    private String texturePath;
    /** Path to the texture to be drawn on top of the button texture when the button is hovered, relative to {@code resources/assets/fancywarpmenu} */
    private String hoverEffectTexturePath;

    /** Width to render the warp icon texture at as a percentage of the screen width. Texture height is set automatically. */
    private float widthPercentage;
    private transient ResourceLocation textureLocation;
    private transient ResourceLocation hoverEffectTextureLocation;
    /** Width of the warp icon texture, used to set the hit box of the warp button */
    private transient int textureWidth;
    /** Height of the warp icon texture, used to set the hit box of the warp button */
    private transient int textureHeight;

    private WarpIcon(){}

    public void init() {
        textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), texturePath);

        if (hoverEffectTexturePath != null) {
            hoverEffectTextureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), hoverEffectTexturePath);
        }
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public ResourceLocation getHoverEffectTextureLocation() {
        return hoverEffectTextureLocation;
    }

    public float getWidthPercentage() {
        return widthPercentage;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public void setTextureDimensions(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }
    public String toString() {
        return gson.toJson(this);
    }

    public static void validateWarpIcon(WarpIcon warpIcon) throws IllegalArgumentException, NullPointerException {
        if (warpIcon == null) {
            throw new NullPointerException("Warp icon cannot be null");
        }

        if (warpIcon.texturePath == null) {
            throw new NullPointerException("Warp icon texture path cannot be null");
        }

        ResourceLocation textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), warpIcon.texturePath);
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(textureLocation);
            IOUtils.closeQuietly(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Warp icon texture not found at %s", textureLocation));
        }

        if (warpIcon.widthPercentage < 0 || warpIcon.widthPercentage > 1) {
            throw new IllegalArgumentException("Warp icon widthPercentage must be between 0 and 1");
        }
    }
}
