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
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.crash.CrashReport;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * This class listens for the player's attempts to access the warp menu and redirects them to the fancy warp menu instead
 * of the regular Hypixel menu. It also listens for changes to the mod's settings and saves them to the config file.
 */
@ChannelHandler.Sharable
public class WarpMenuListener extends ChannelOutboundHandlerAdapter {
    private static final Minecraft mc;
    private static final FancyWarpMenu modInstance;
    private static final Field chatInputField;
    private static final MethodHandle inputFieldGetterHandle;

    private static GuiFancyWarp warpScreen;

    static {
        mc = Minecraft.getMinecraft();
        modInstance = FancyWarpMenu.getInstance();
        
        try {
            String inputFieldName = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ? "inputField" : "field_146415_a";
            chatInputField = GuiChat.class.getDeclaredField(inputFieldName);
            chatInputField.setAccessible(true);
            inputFieldGetterHandle = MethodHandles.lookup().unreflectGetter(chatInputField);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Failed to access chat text box"));
        }
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        // type 0 is a standard chat message
        if (mc.currentScreen == warpScreen && event.type == 0) {
            String unformattedText = event.message.getUnformattedText();

            if (modInstance.getWarpMessages().getWarpSuccessMessages().contains(unformattedText)) {
                mc.displayGuiScreen(null);
            } else if (modInstance.getWarpMessages().getWarpFailMessages().containsKey(unformattedText)) {
                warpScreen.onWarpFail(modInstance.getWarpMessages().getWarpFailMessages().get(unformattedText));
            }
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
     * Redirect to the fancy warp menu when the player attempts to access the warp menu with the /warp command
     */
    @SubscribeEvent
    public void onGuiKeyboardInput(GuiScreenEvent.KeyboardInputEvent event) {
        if (Settings.isWarpMenuEnabled()
                && modInstance.isPlayerOnSkyBlock()
                && event.gui instanceof GuiChat
                && (Keyboard.getEventKey() == Keyboard.KEY_RETURN
                || Keyboard.getEventKey() == Keyboard.KEY_NUMPADENTER)) {
            try {
                GuiTextField textField = (GuiTextField) inputFieldGetterHandle.invokeExact((GuiChat) event.gui);
                String chatMessage = textField.getText().trim().toLowerCase();

                if (chatMessage.equals("/warp")) {
                    warpScreen = new GuiFancyWarp();
                    mc.displayGuiScreen(warpScreen);
                    mc.ingameGUI.getChatGUI().addToSentMessages(chatMessage);
                    event.setCanceled(true);
                }
            } catch (Throwable e) {
                throw new ReportedException(CrashReport.makeCrashReport(e, "Failed to get chat message from GuiChat"));
            }
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
}
