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

import ca.tirelesstraveler.fancywarpmenu.data.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LayoutLoader {
    private static final ResourceLocation WARP_CONFIG_LOCATION = new ResourceLocation("fancywarpmenu",
            "data/layout.json");
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger();

    public static Layout loadLayout() {
        try {
            IResource layoutResource = Minecraft.getMinecraft().getResourceManager().getResource(WARP_CONFIG_LOCATION);

            try (InputStream stream = layoutResource.getInputStream();
                 JsonReader reader = new JsonReader(new InputStreamReader(stream))) {
                Layout layout = gson.fromJson(reader, Layout.class);
                WarpIcon warpIcon = layout.getWarpIcon();
                Warp.setWarpIcon(warpIcon);
                Layout.validateLayout(layout);

                // Warp icon
                warpIcon.init();
                Pair<Integer, Integer> warpIconDimensions = getTextureDimensions(warpIcon.getTextureLocation());
                warpIcon.setTextureDimensions(warpIconDimensions.getLeft(), warpIconDimensions.getRight());
                Warp.setWarpIcon(warpIcon);

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
                ResourcePackRepository.Entry resourcePackEntry = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().stream().filter(
                        entry -> entry.getResourcePackName().equals(layoutResource.getResourcePackName())).findFirst().orElse(null);
                String resourcePackName;
                String resourcePackDescription;

                if (resourcePackEntry != null) {
                    resourcePackName = layoutResource.getResourcePackName();
                    resourcePackDescription = resourcePackEntry.getTexturePackDescription();
                } else {
                    resourcePackName = FancyWarpMenu.getInstance().getModContainer().getName() + " " + FancyWarpMenu.getInstance().getModContainer().getVersion();
                    resourcePackDescription = "Built-in resource pack";
                }

                if (FancyWarpMenu.getLayout() != null) {
                    StringBuilder stringBuilder = new StringBuilder("Your Fancy Warp Menu resource pack may be outdated.");
                    stringBuilder.append("\n").append(String.format("Layout loading failed: %s", e.getMessage()));
                    stringBuilder.append("\n").append("Resource Pack Details:");
                    stringBuilder.append("\n").append("Name: ").append(resourcePackName);
                    stringBuilder.append("\n").append("Description: ").append(resourcePackDescription);

                    if (resourcePackEntry != null) {
                        stringBuilder.append("\n").append("File: ").append(resourcePackEntry);
                    }

                    logger.error(stringBuilder, e);

                    if (Minecraft.getMinecraft().ingameGUI != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + stringBuilder.toString()));
                    }

                    return null;
                } else {
                    CrashReport crashReport = new CrashReport("Your Fancy Warp Menu resource pack may be outdated", e);
                    CrashReportCategory resourcePackDetails = crashReport.makeCategory("Resource Pack");

                    resourcePackDetails.addCrashSection("Name", resourcePackName);
                    resourcePackDetails.addCrashSection("Description", resourcePackDescription);

                    if (resourcePackEntry != null) {
                        resourcePackDetails.addCrashSection("File", resourcePackEntry.toString());
                    }

                    throw new ReportedException(crashReport);
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
