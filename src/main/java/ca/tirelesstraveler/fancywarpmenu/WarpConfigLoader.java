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
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import ca.tirelesstraveler.fancywarpmenu.data.WarpConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WarpConfigLoader {
    private static final ResourceLocation WARP_CONFIG_LOCATION = new ResourceLocation("fancywarpmenu",
            "data/islands.json");
    private static final Gson gson = new Gson();

    public static WarpConfiguration loadIslands() {
        try {
            IResource islandResource = Minecraft.getMinecraft().getResourceManager().getResource(WARP_CONFIG_LOCATION);

            try (InputStream stream = islandResource.getInputStream();
                 JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)))){
                 WarpConfiguration warpConfig = gson.fromJson(reader, WarpConfiguration.class);

                for (Island island:
                     warpConfig.getIslandList()) {
                    island.setTextureLocation();
                }

                 warpConfig.getWarpIcon().init();
                 Warp.setWarpIcon(warpConfig.getWarpIcon());
                 return warpConfig;
            } catch (JsonParseException e) {
                if (Settings.isDebugModeEnabled()) {
                    LogManager.getLogger().error(e.getMessage());
                    return null;
                } else {
                    throw new ReportedException(new CrashReport("Warp configuration load fail: " + e.getMessage(), e));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
