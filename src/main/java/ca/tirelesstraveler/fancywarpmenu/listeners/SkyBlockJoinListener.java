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

package ca.tirelesstraveler.fancywarpmenu.listeners;

import ca.tirelesstraveler.fancywarpmenu.state.GameState;
import io.netty.channel.ChannelHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Forge event and packet listener that detects when the player joins/leaves SkyBlock
 */
@ChannelHandler.Sharable
public class SkyBlockJoinListener {
    private static final String SERVER_BRAND_START = "Hypixel BungeeCord";
    private static final int SCOREBOARD_CHECK_TIME_OUT = 3000;

    private static final Logger logger = LogManager.getLogger();
    private boolean serverBrandChecked;
    private boolean onHypixel;
    private boolean scoreboardChecked;
    private long lastWorldSwitchTime;

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if (onHypixel) {
            serverBrandChecked = false;
            onHypixel = false;
            GameState.setOnSkyBlock(false);
            logger.debug("Disconnected from Hypixel.");
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        // Reset on world switch
        if (event.gui instanceof GuiDownloadTerrain) {
            lastWorldSwitchTime = Minecraft.getSystemTime();
            scoreboardChecked = false;
            GameState.setOnSkyBlock(false);
        }
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (!serverBrandChecked || onHypixel && !scoreboardChecked) {
            // type 0 is a standard chat message
            if (event.type == 0) {
                EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;

                if (!serverBrandChecked) {
                    onHypixel = thePlayer.getClientBrand().startsWith(SERVER_BRAND_START);
                    serverBrandChecked = true;

                    if (onHypixel) {
                        logger.debug("Player joined Hypixel.");
                    }
                }

                if (onHypixel && !scoreboardChecked) {
                    Scoreboard scoreboard = thePlayer.getWorldScoreboard();
                    boolean newSkyBlockState = scoreboard != null && scoreboard.getObjective("SBScoreboard") != null;

                    if (newSkyBlockState != GameState.isOnSkyBlock()) {
                        if (newSkyBlockState) {
                            logger.debug("Player joined SkyBlock.");
                        } else {
                            logger.debug("Player left SkyBlock.");
                        }

                        GameState.setOnSkyBlock(newSkyBlockState);
                    }

                    if (Minecraft.getSystemTime() - lastWorldSwitchTime > SCOREBOARD_CHECK_TIME_OUT) {
                        scoreboardChecked = true;
                    }
                }
            }
        }
    }
}
