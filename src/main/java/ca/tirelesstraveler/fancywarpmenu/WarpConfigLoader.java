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

import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import ca.tirelesstraveler.fancywarpmenu.data.WarpConfiguration;
import ca.tirelesstraveler.fancywarpmenu.data.WarpIcon;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WarpConfigLoader {
    private static final ResourceLocation WARP_CONFIG_LOCATION = new ResourceLocation("fancywarpmenu",
            "data/islands.json");
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger();

    public static WarpConfiguration loadIslands() {
        boolean modLoadingComplete = Loader.instance().isInState(LoaderState.AVAILABLE);

        try {
            IResource islandResource = Minecraft.getMinecraft().getResourceManager().getResource(WARP_CONFIG_LOCATION);

            try (InputStream stream = islandResource.getInputStream();
                 JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)))) {
                WarpConfiguration warpConfig = gson.fromJson(reader, WarpConfiguration.class);
                WarpIcon warpIcon = warpConfig.getWarpIcon();
                warpIcon.init();
                Pair<Integer, Integer> warpIconDimensions = getTextureDimensions(warpIcon.getTextureLocation());
                warpIcon.setTextureDimensions(warpIconDimensions.getLeft(), warpIconDimensions.getRight());
                Warp.setWarpIcon(warpIcon);
                WarpConfiguration.validateWarpConfiguration(warpConfig);

                for (Island island : warpConfig.getIslandList()) {
                    Pair<Integer, Integer> islandTextureDimensions;

                    island.setTextureLocation();
                    islandTextureDimensions = getTextureDimensions(island.getTextureLocation());
                    island.setTextureDimensions(islandTextureDimensions.getLeft(), islandTextureDimensions.getRight());

                    if (island.getHoverEffectTexturePath() != null) {
                        island.setHoverEffectTextureLocation();
                    }
                }

                return warpConfig;
            } catch (RuntimeException e) {
                if (modLoadingComplete) {
                    logger.error(String.format("Warp config loading failed: %s", e.getMessage()), e);

                    if (Minecraft.getMinecraft().ingameGUI != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e.getMessage()));
                    }

                    return null;
                } else {
                    throw new ReportedException(new CrashReport(String.format("Warp config loading failed: %s", e.getMessage()), e));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
