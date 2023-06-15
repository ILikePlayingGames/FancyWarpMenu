package ca.tirelesstraveler.skyblockwarpmenu;

import ca.tirelesstraveler.skyblockwarpmenu.data.Island;
import ca.tirelesstraveler.skyblockwarpmenu.data.Settings;
import ca.tirelesstraveler.skyblockwarpmenu.data.Warp;
import ca.tirelesstraveler.skyblockwarpmenu.data.WarpConfiguration;
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
    private static final ResourceLocation WARP_CONFIG_LOCATION = new ResourceLocation("skyblockwarpmenu",
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
