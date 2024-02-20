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

package ca.tirelesstraveler.fancywarpmenu.resourceloaders;

import ca.tirelesstraveler.fancywarpmenu.data.layout.*;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LayoutLoader extends ResourceLoader {
    public static final ResourceLocation OVERWORLD_LAYOUT_LOCATION = new ResourceLocation("fancywarpmenu",
            "data/layout.json");
    public static final ResourceLocation RIFT_LAYOUT_LOCATION = new ResourceLocation("fancywarpmenu", "data/riftLayout.json");

    public static Layout loadLayout(ResourceLocation resourceLocation) {
        try {
            IResource layoutResource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);

            try (InputStream stream = layoutResource.getInputStream();
                 JsonReader reader = new JsonReader(new InputStreamReader(stream))) {
                Layout layout = gson.fromJson(reader, Layout.class);
                Layout.validateLayout(layout);

                // Layout background texture
                layout.setBackgroundTextureLocation();

                // Warp icon
                WarpIcon warpIcon = layout.getWarpIcon();
                Warp.setWarpIcon(warpIcon);
                warpIcon.init();
                Pair<Integer, Integer> warpIconDimensions = getTextureDimensions(warpIcon.getTextureLocation());
                warpIcon.setTextureDimensions(warpIconDimensions.getLeft(), warpIconDimensions.getRight());

                // Config and regular warp menu button icon dimensions
                ConfigButton configButton = layout.getConfigButton();
                RegularWarpMenuButton regularWarpMenuButton = layout.getRegularWarpMenuButton();

                Pair<Integer, Integer> configButtonIconDimensions = getTextureDimensions(configButton.getTextureLocation());
                configButton.setTextureDimensions(configButtonIconDimensions.getLeft(), configButtonIconDimensions.getRight());
                Pair<Integer, Integer> regularWarpMenuButtonIconDimensions = getTextureDimensions(regularWarpMenuButton.getTextureLocation());
                regularWarpMenuButton.setTextureDimensions(regularWarpMenuButtonIconDimensions.getLeft(), regularWarpMenuButtonIconDimensions.getRight());

                // Island texture dimensions and hover effect texture
                for (Island island : layout.getIslandList()) {
                    Pair<Integer, Integer> islandTextureDimensions;

                    island.setTextureLocation();
                    islandTextureDimensions = getTextureDimensions(island.getTextureLocation());
                    island.setTextureDimensions(islandTextureDimensions.getLeft(), islandTextureDimensions.getRight());

                    if (island.getHoverEffectTexturePath() != null) {
                        island.setHoverEffectTextureLocation();
                    }
                }

                return layout;
            } catch (RuntimeException e) {
                boolean fatal = FancyWarpMenuState.getOverworldLayout() == null;

                handleResourceLoadException(layoutResource, fatal, e);
                return null;
            }
        } catch (IOException e) {
            boolean fatal = FancyWarpMenuState.getOverworldLayout() == null;

            handleGetResourceException(resourceLocation.toString(), fatal, e);
            return null;
        }
    }

    private static Pair<Integer, Integer> getTextureDimensions(ResourceLocation resourceLocation) {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

        try (InputStream textureStream = resourceManager.getResource(resourceLocation).getInputStream()) {
            BufferedImage bufferedImage = TextureUtil.readBufferedImage(textureStream);
            return Pair.of(bufferedImage.getWidth(), bufferedImage.getHeight());
        } catch (IOException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e,
                    String.format("Failed to read texture \"%s\"", resourceLocation)));
        }
    }
}
