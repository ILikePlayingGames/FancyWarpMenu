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

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.GameState;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Forge event and packet listener that detects when the player joins/leaves SkyBlock
 */
@ChannelHandler.Sharable
public class SkyBlockJoinListener extends SimpleChannelInboundHandler<S3DPacketDisplayScoreboard> {
    private static final String SERVER_BRAND_START = "Hypixel BungeeCord";
    private static final String PACKET_LISTENER_NAME = String.format("%s:skyblock_join_listener",
            FancyWarpMenu.getInstance().getModId());

    private static final Logger logger = LogManager.getLogger();
    private boolean serverBrandChecked;
    private boolean onHypixel;

    public SkyBlockJoinListener() {
        super(false);
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.manager.channel().pipeline().get(PACKET_LISTENER_NAME) == null) {
            e.manager.channel().pipeline().addBefore("packet_handler", PACKET_LISTENER_NAME, this);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if (onHypixel) {
            serverBrandChecked = false;
            onHypixel = false;
            GameState.setOnSkyBlock(false);
            logger.info("Disconnected from Hypixel.");
        }
    }

    /**
     * This method listens for {@link S3DPacketDisplayScoreboard} packets. These are sent once per world join/switch
     * and contain the name of the game mode, thus making them excellent for checking whether the player is on SkyBlock.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, S3DPacketDisplayScoreboard packet) {
        if (channelHandlerContext.channel().isOpen()) {
            EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;

            try {
                logger.debug("Received S3DPacketDisplayScoreboard packet with title \"{}\"", packet.func_149370_d());

                // Player hasn't been spawned yet, skip this packet.
                if (thePlayer != null && thePlayer.getClientBrand() != null) {
                    if (!serverBrandChecked) {
                        onHypixel = thePlayer.getClientBrand().startsWith(SERVER_BRAND_START);
                        serverBrandChecked = true;

                        if (onHypixel) {
                            logger.info("Player joined Hypixel.");
                        }
                    }

                    // 1 is the sidebar objective slot.
                    if (onHypixel && packet.func_149371_c() == 1) {
                        String objectiveName = packet.func_149370_d();
                        boolean newSkyBlockState = objectiveName.equals("SBScoreboard");

                        if (newSkyBlockState && !GameState.isOnSkyBlock()) {
                            logger.info("Player joined SkyBlock.");
                        } else if (!newSkyBlockState && GameState.isOnSkyBlock()) {
                            logger.info("Player left SkyBlock.");
                        }

                        GameState.setOnSkyBlock(newSkyBlockState);
                    }
                }
            } catch (RuntimeException e) {
                logger.error(String.format("SkyBlock Join Check Failed: %s" +
                        "\nBrand: %s" +
                        "\nPacket contents:" +
                        "\n  Position: %s" +
                        "\n  Score Name: %s", e.getMessage(), thePlayer.getClientBrand(), packet.func_149371_c(), packet.func_149370_d()), e);
            }
        }

        channelHandlerContext.fireChannelRead(packet);
    }

    public boolean isOnSkyBlock() {
        return (Settings.isDebugModeEnabled() && Settings.shouldSkipSkyBlockCheck()) || GameState.isOnSkyBlock();
    }
}
