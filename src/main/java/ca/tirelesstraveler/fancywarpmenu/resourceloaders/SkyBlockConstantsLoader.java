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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.SkyBlockConstants;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SkyBlockConstantsLoader extends ResourceLoader {
    private static final ResourceLocation SKY_BLOCK_CONSTANTS_LOCATION = new ResourceLocation("fancywarpmenu",
            "data/skyBlockConstants.json");

    public static SkyBlockConstants loadSkyBlockConstants() {
        try {
            IResource skyBlockConstantsResource = Minecraft.getMinecraft().getResourceManager().getResource(SKY_BLOCK_CONSTANTS_LOCATION);

            try (InputStream stream = skyBlockConstantsResource.getInputStream();
                 JsonReader reader = new JsonReader(new InputStreamReader(stream))) {
                SkyBlockConstants skyBlockConstants = gson.fromJson(reader, SkyBlockConstants.class);
                SkyBlockConstants.validateSkyBlockConstants(skyBlockConstants);

                return skyBlockConstants;
            } catch (RuntimeException e) {
                boolean fatal = FancyWarpMenu.getSkyBlockConstants() == null;

                handleResourceLoadException(skyBlockConstantsResource, fatal, e);
                return null;
            }
        } catch (IOException e) {
            boolean fatal = FancyWarpMenu.getSkyBlockConstants() == null;

            handleGetResourceException(SKY_BLOCK_CONSTANTS_LOCATION.toString(), fatal, e);
            return null;
        }
    }
}
