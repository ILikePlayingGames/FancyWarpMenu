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
import ca.tirelesstraveler.fancywarpmenu.OpenConfigCommand;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpMessages;
import ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuConfigScreen;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiRiftFastTravel;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonConfig;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFastTravel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * General purpose event listener
 */
@ChannelHandler.Sharable
public class WarpMenuListener extends ChannelOutboundHandlerAdapter implements IResourceManagerReloadListener {
    private static final Minecraft mc;
    private static final FancyWarpMenu modInstance;
    private static final Logger logger;

    private static GuiFancyWarp warpScreen;
    private static boolean openMenuRequested;
    private static boolean openConfigMenuRequested;

    static {
        mc = Minecraft.getMinecraft();
        modInstance = FancyWarpMenu.getInstance();
        logger = LogManager.getLogger();
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        // type 0 is a standard chat message
        if (event.type == 0 && warpScreen != null && mc.currentScreen == warpScreen) {
            String unformattedText = event.message.getUnformattedText();

            if (FancyWarpMenu.getSkyBlockConstants().getWarpMessages().getWarpSuccessMessages().contains(unformattedText)) {
                mc.displayGuiScreen(null);
            } else if (FancyWarpMenu.getSkyBlockConstants().getWarpMessages().getWarpFailMessages().containsKey(unformattedText)) {
                WarpMessages warpMessages = FancyWarpMenu.getSkyBlockConstants().getWarpMessages();
                Map<String, String> warpFailMessages = warpMessages.getWarpFailMessages();
                String failMessageKey = warpFailMessages.get(unformattedText);
                warpScreen.onWarpFail(failMessageKey);
            }
        }
    }

    /**
     * Minecraft closes the current screen after executing a command,
     * meaning any {@code GuiScreen} opened by the command is closed.
     * This method interrupts the closing of the current screen to get around this behavior.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            if (openMenuRequested) {
                createFastTravelMenu(false);
                event.gui = warpScreen;
                openMenuRequested = false;
            } else if (openConfigMenuRequested) {
                event.gui = new FancyWarpMenuConfigScreen(null);
                openConfigMenuRequested = false;
            }
        }

        if (warpScreen != null && (mc.currentScreen == warpScreen || mc.currentScreen instanceof GuiChest)) {
            warpScreen = null;
        }
    }

    /**
     * Add a button to the regular warp menu for players to enable the fancy warp menu or switch to it.
     */
    @SubscribeEvent
    public void afterGuiInit(GuiScreenEvent.InitGuiEvent.Post e) {
        if (Settings.isWarpMenuEnabled()
                && modInstance.isPlayerOnSkyBlock()
                && e.gui instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) e.gui;
            ContainerChest containerChest = (ContainerChest) guiChest.inventorySlots;
            String containerName = containerChest.getLowerChestInventory().getName();

            if (containerName.equals("Fast Travel")) {
                e.buttonList.add(new GuiButtonConfig(e.buttonList.size(), new ScaledResolution(mc)));
            } else if (containerName.equals("Porhtal")) {
                warpScreen = new GuiRiftFastTravel(FancyWarpMenu.getRiftLayout());
                warpScreen.setWorldAndResolution(mc, guiChest.width, guiChest.height);
            }
        }
    }

    @SubscribeEvent
    public void beforeGuiDraw(GuiScreenEvent.DrawScreenEvent.Pre e) {
        if (warpScreen instanceof GuiRiftFastTravel) {
            warpScreen.drawScreen(e.mouseX, e.mouseY, e.renderPartialTicks);
            e.setCanceled(true);
        }
    }

    /**
     * Open the fancy warp menu when the player presses the open warp menu hotkey while the mod is enabled
     */
    @SubscribeEvent
    public void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (Settings.isWarpMenuEnabled()
                && modInstance.isPlayerOnSkyBlock()
                && FancyWarpMenu.getKeyBindingOpenWarpMenu().isPressed()) {
            createFastTravelMenu(true);
        }
    }

    /**
     * Redirect to the fancy warp menu when the player attempts to access the warp menu from the SkyBlock menu
     */
    @SubscribeEvent
    public void beforeGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
        if (warpScreen instanceof GuiRiftFastTravel) {
            warpScreen.handleMouseInput();
            event.setCanceled(true);
        }

        // Redirect the player to the Fancy Warp Menu if they click on the fast travel button in the SkyBlock menu
        if (Settings.isWarpMenuEnabled()
                && modInstance.isPlayerOnSkyBlock()
                && Mouse.getEventButton() == 0
                && Mouse.getEventButtonState()
                && event.gui instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) event.gui;

            if (guiChest.inventorySlots instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) guiChest.inventorySlots;

                if (container.getLowerChestInventory() != null
                        && container.getLowerChestInventory().hasCustomName()
                        && container.getLowerChestInventory().getName().equals("SkyBlock Menu")
                        && guiChest.getSlotUnderMouse() != null
                        && guiChest.getSlotUnderMouse().getSlotIndex() == 47
                        // Rift SkyBlock Menu has a return to hub button in slot 47
                        && StringUtils.stripControlCodes(guiChest.getSlotUnderMouse().getStack().getDisplayName()).equals("Fast Travel")) {
                    createFastTravelMenu(true);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void beforeGuiKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre e) throws IOException {
        if (warpScreen instanceof GuiRiftFastTravel) {
            if (Keyboard.getEventKey() != Keyboard.KEY_ESCAPE && Keyboard.getEventKey() != mc.gameSettings.keyBindInventory.getKeyCode()) {
                warpScreen.handleKeyboardInput();
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(modInstance.getModId())) {
            Settings.syncConfig(false);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        FancyWarpMenu.getInstance().reloadLayouts();
    }

    /**
     * Called when the {@code /warp} command is run without any arguments
     */
    public void onWarpCommand() {
        if (Settings.shouldAddWarpCommandToChatHistory()) {
            mc.ingameGUI.getChatGUI().addToSentMessages("/warp");
        }

        // GuiChat closes itself after executing the command so wait until after it closes to open the warp menu.
        if (mc.currentScreen instanceof GuiChat) {
            openMenuRequested = true;
        } else {
            createFastTravelMenu(true);
        }
    }

    /**
     * Called when {@link OpenConfigCommand} is run
     */
    public void onOpenConfigMenuCommand() {
        openConfigMenuRequested = true;
    }

    public void createFastTravelMenu(boolean display) {
        checkLateWinter();
        warpScreen = new GuiFastTravel(FancyWarpMenu.getLayout());

        if (display) {
            mc.displayGuiScreen(warpScreen);
        }
    }

    /**
     * Checks if the SkyBlock season is Late Winter, used for hiding Jerry's Workshop when it's closed
     */
    private void checkLateWinter() {
        // Don't run outside of SB to prevent exceptions
        if (!Settings.shouldSkipSkyBlockCheck()) {
            try {
                Scoreboard sb = mc.theWorld.getScoreboard();
                // SkyBlock sidebar objective
                ArrayList<Score> scores = (ArrayList<Score>) sb.getSortedScores(sb.getObjective("SBScoreboard"));

                // The date is always near the top (highest score) so we iterate backwards.
                for (int i = scores.size(); i > 0; i--) {
                    Score score = scores.get(i - 1);
                    String playerNameDisplayFormat = sb.getPlayersTeam(score.getPlayerName()).formatString("");

                    if (playerNameDisplayFormat.trim().startsWith("Late Winter")) {
                        GameState.setLateWinter(true);
                        return;
                    }
                }

                GameState.setLateWinter(false);
            } catch (RuntimeException e) {
                logger.warn("Failed to check scoreboard season for late winter", e);
            }
        }
    }

    /**
     * Checks if a given command is the warp command or any of its variants and returns the corresponding
     * {@code WarpCommandVariant} object if one is found.
     *
     * @param command the command the player sent
     * @return a {@link WarpCommandVariant} if one with the same command is found, or {@code null} otherwise
     */
    public static WarpCommandVariant getWarpCommandVariant(String command) {
        // Trim off the slash and all arguments
        String baseCommand = command.toLowerCase(Locale.US).substring(1).split(" ")[0];

        for (WarpCommandVariant commandVariant : FancyWarpMenu.getSkyBlockConstants().getWarpCommandVariants()) {
            if (commandVariant.getCommand().equals(baseCommand)) {
                return commandVariant;
            }
        }

        return null;
    }

    public static void sendReminderToUseWarpScreen() {
        mc.thePlayer.addChatMessage(new ChatComponentTranslation(FancyWarpMenu
                .getFullLanguageKey("messages.useWarpMenuInsteadOfCommand"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }
}
