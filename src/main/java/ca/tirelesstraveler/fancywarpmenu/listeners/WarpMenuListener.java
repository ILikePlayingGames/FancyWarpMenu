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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.Menu;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuConfigScreen;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFastTravel;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiRiftFastTravel;
import ca.tirelesstraveler.fancywarpmenu.gui.buttons.GuiButtonConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

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
                String failMessageKey = FancyWarpMenu.getSkyBlockConstants().getWarpMessages().getWarpFailMessages().get(unformattedText);
                warpScreen.onWarpFail(failMessageKey);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (warpScreen != null && (mc.currentScreen == warpScreen || mc.currentScreen instanceof GuiChest)) {
            warpScreen = null;
            return;
        }

        /*
        Minecraft closes the current screen after executing a command, meaning any GuiScreen opened by the command is closed.
        This section interrupts the closing of the current screen to get around this behavior.
         */
        if (event.gui == null) {
            if (openMenuRequested) {
                createFastTravelMenu(Menu.FAST_TRAVEL, false);
                event.gui = warpScreen;
                openMenuRequested = false;
            } else if (openConfigMenuRequested) {
                event.gui = new FancyWarpMenuConfigScreen(null);
                openConfigMenuRequested = false;
            }
        } else if (event.gui instanceof GuiChest) {
            IInventory chestInventory = ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory();

            if (chestInventory.hasCustomName()) {
                /*
                Guess which in-game GuiChest menu the player is in based on its title.
                This is not accurate and should be used with more specific checks.
                 */
                String containerTitle = chestInventory.getDisplayName().getUnformattedText();

                for (Menu menu : Menu.values()) {
                    if (containerTitle.equals(menu.getMenuTitle())) {
                        GameState.setCurrentMenu(menu);
                        return;
                    }
                }
            }

            GameState.setCurrentMenu(Menu.NONE);
        }
    }

    @SubscribeEvent
    public void afterGuiInit(GuiScreenEvent.InitGuiEvent.Post e) {
        if (GameState.isOnSkyBlock() && e.gui instanceof GuiChest) {
            Menu currentMenu = GameState.getCurrentMenu();

            if (currentMenu == Menu.FAST_TRAVEL) {
                if (Settings.isWarpMenuEnabled()) {
                    createFastTravelMenu(Menu.FAST_TRAVEL, false);
                } else {
                    // Add a button to the regular warp menu for players to enable the fancy warp menu or switch to it.
                    e.buttonList.add(new GuiButtonConfig(e.buttonList.size(), new ScaledResolution(mc)));
                }
            } else if (currentMenu == Menu.PORHTAL) {
                createFastTravelMenu(Menu.PORHTAL, false);
            }
        }
    }

    @SubscribeEvent
    public void beforeGuiDraw(GuiScreenEvent.DrawScreenEvent.Pre e) {
        if (isFancyWarpMenuOpen()) {
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
                && GameState.isOnSkyBlock()
                && FancyWarpMenu.getKeyBindingOpenWarpMenu().isPressed()) {
            createFastTravelMenu(Menu.FAST_TRAVEL, true);
        }
    }

    @SubscribeEvent
    public void beforeGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) throws IOException {
        if (isFancyWarpMenuOpen()) {
            warpScreen.handleMouseInput();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void beforeGuiKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre e) throws IOException {
        if (isFancyWarpMenuOpen()) {
            // Pass requests to close the chest to the underlying GuiChest to make sure it is closed properly
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
            createFastTravelMenu(Menu.FAST_TRAVEL, true);
        }
    }

    /**
     * Called when {@link OpenConfigCommand} is run
     */
    public void onOpenConfigMenuCommand() {
        openConfigMenuRequested = true;
    }

    public void createFastTravelMenu(Menu menuType, boolean display) {
        if (menuType == null) {
            throw new NullPointerException("menuType cannot be null");
        } else if (menuType != Menu.FAST_TRAVEL && menuType != Menu.PORHTAL) {
            throw new IllegalArgumentException(menuType + " is not a valid menu type");
        }

        checkLateWinter();
        warpScreen = menuType == Menu.FAST_TRAVEL ?
                new GuiFastTravel(FancyWarpMenu.getLayout()) :
                new GuiRiftFastTravel(FancyWarpMenu.getRiftLayout());

        if (display) {
            mc.displayGuiScreen(warpScreen);
        } else if (mc.currentScreen instanceof GuiChest) {
            /*
            If drawing over a GuiChest, setup the dimensions of the GuiFancyWarp manually since the normal setup process is skipped
            */
            warpScreen.setWorldAndResolution(mc, mc.currentScreen.width, mc.currentScreen.height);
        }
    }

    private boolean isFancyWarpMenuOpen() {
        return warpScreen != null && mc.currentScreen != warpScreen;
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
