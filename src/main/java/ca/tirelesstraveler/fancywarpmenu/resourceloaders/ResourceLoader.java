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

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class ResourceLoader {
    protected static final Gson gson = new Gson();
    protected static final Logger logger = LogManager.getLogger();

    protected static void handleResourceLoadException(IResource resource, boolean fatal, RuntimeException e) {
        ResourcePackRepository.Entry resourcePackEntry = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries().stream().filter(
                entry -> entry.getResourcePackName().equals(resource.getResourcePackName())).findFirst().orElse(null);
        String resourcePackName;
        String resourcePackDescription;

        if (resourcePackEntry != null) {
            resourcePackName = resource.getResourcePackName();
            resourcePackDescription = resourcePackEntry.getTexturePackDescription();
        } else {
            resourcePackName = FancyWarpMenu.getInstance().getModContainer().getName() + " " + FancyWarpMenu.getInstance().getModContainer().getVersion();
            resourcePackDescription = "Built-in resource pack";
        }

        if (!fatal) {
            StringBuilder stringBuilder = new StringBuilder("Your Fancy Warp Menu resource pack may be outdated.");
            stringBuilder.append("\n").append(String.format("Resource loading failed: %s", e.getMessage()));
            stringBuilder.append("\n").append(String.format("Resource Path: %s", resource.getResourceLocation().toString()));
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
        } else {
            CrashReport crashReport = new CrashReport("Your Fancy Warp Menu resource pack may be outdated", e);
            CrashReportCategory resourceDetails = crashReport.makeCategory("Resource");
            CrashReportCategory resourcePackDetails = crashReport.makeCategory("Resource Pack");

            resourceDetails.addCrashSection("Path", resource.getResourceLocation().toString());

            resourcePackDetails.addCrashSection("Name", resourcePackName);
            resourcePackDetails.addCrashSection("Description", resourcePackDescription);

            if (resourcePackEntry != null) {
                resourcePackDetails.addCrashSection("File", resourcePackEntry.toString());
            }

            throw new ReportedException(crashReport);
        }
    }

    protected static void handleGetResourceException(String resourcePath, boolean fatal, IOException e) {
        if (!fatal) {
            StringBuilder stringBuilder = new StringBuilder("Your Fancy Warp Menu resource pack may be outdated.");
            stringBuilder.append("\n").append(String.format("Resource loading failed: %s", e.getMessage()));
            stringBuilder.append("\n").append(String.format("Resource Path: %s", resourcePath));

            logger.error(stringBuilder, e);

            if (Minecraft.getMinecraft().ingameGUI != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + stringBuilder.toString()));
            }
        } else {
            CrashReport crashReport = new CrashReport("Your Fancy Warp Menu resource pack may be outdated", e);
            CrashReportCategory resourceDetails = crashReport.makeCategory("Resource");
            resourceDetails.addCrashSection("Path", resourcePath);
            throw new ReportedException(crashReport);
        }
    }
}
