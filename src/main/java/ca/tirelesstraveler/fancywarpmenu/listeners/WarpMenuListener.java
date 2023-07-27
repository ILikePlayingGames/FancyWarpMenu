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
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.WarpMessages;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
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
import org.lwjgl.input.Mouse;

import java.util.Map;

/**
 * General purpose event listener
 */
@ChannelHandler.Sharable
public class WarpMenuListener extends ChannelOutboundHandlerAdapter {
    private static final Minecraft mc;
    private static final FancyWarpMenu modInstance;

    private static GuiFancyWarp warpScreen;
    private static boolean openMenuRequested;

    static {
        mc = Minecraft.getMinecraft();
        modInstance = FancyWarpMenu.getInstance();
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        // type 0 is a standard chat message
        if (mc.currentScreen == warpScreen && event.type == 0) {
            String unformattedText = event.message.getUnformattedText();

            if (modInstance.getWarpMessages().getWarpSuccessMessages().contains(unformattedText)) {
                mc.displayGuiScreen(null);
            } else if (modInstance.getWarpMessages().getWarpFailMessages().containsKey(unformattedText)) {
                WarpMessages warpMessages = modInstance.getWarpMessages();
                Map<String, String> warpFailMessages = warpMessages.getWarpFailMessages();
                String failMessageKey = warpFailMessages.get(unformattedText);
                warpScreen.onWarpFail(failMessageKey);
            }
        }
    }

    /**
     * If the player requested the warp menu to open by executing the warp command, switch to the warp menu instead
     * of closing the chat window.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null && openMenuRequested) {
            event.gui = warpScreen;
            openMenuRequested = false;
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
            warpScreen = new GuiFancyWarp();
            mc.displayGuiScreen(warpScreen);
        }
    }

    /**
     * Redirect to the fancy warp menu when the player attempts to access the warp menu from the SkyBlock menu
     */
    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Settings.isWarpMenuEnabled()
                && modInstance.isPlayerOnSkyBlock()
                && Mouse.getEventButton() == 0
                && Mouse.getEventButtonState()
                && event.gui instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) event.gui;

            if (guiChest.inventorySlots instanceof ContainerChest) {
                ContainerChest container = (ContainerChest) guiChest.inventorySlots;

                if (container.getLowerChestInventory() != null
                        && container.getLowerChestInventory().getDisplayName() != null
                        && container.getLowerChestInventory().getDisplayName().getUnformattedText().equals("SkyBlock Menu")
                        && guiChest.getSlotUnderMouse() != null
                        && guiChest.getSlotUnderMouse().getSlotIndex() == 47
                        // Rift SkyBlock Menu has a return to hub button in slot 47
                        && StringUtils.stripControlCodes(guiChest.getSlotUnderMouse().getStack().getDisplayName()).equals("Fast Travel")) {
                    mc.displayGuiScreen(new GuiFancyWarp());
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(modInstance.getModId())) {
            Settings.syncConfig(false);
        }
    }

    /**
     * Called when the {@code /warp} command is run without any arguments
     */
    public void onWarpCommand() {
        warpScreen = new GuiFancyWarp();
        mc.ingameGUI.getChatGUI().addToSentMessages("/warp");

        // GuiChat closes itself after executing the command so wait until after it closes to open the warp menu.
        if (mc.currentScreen instanceof GuiChat) {
            openMenuRequested = true;
        } else {
            mc.displayGuiScreen(warpScreen);
        }
    }

    /**
     * Checks if the command name of the command the player sent is the name of the warp command or any of its variants
     *
     * @param commandName name of the command the player sent, excluding the slash and any arguments
     */
    public static boolean isWarpCommand(String commandName) {
        String baseCommand = commandName.substring(1).split(" ")[0];
        return modInstance.getWarpCommandVariants().contains(baseCommand);
    }

    public static void sendReminderToUseWarpScreen() {
        mc.thePlayer.addChatMessage(new ChatComponentTranslation(FancyWarpMenu.getInstance()
                .getFullLanguageKey("messages.useWarpMenuInsteadOfCommand"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }
}
