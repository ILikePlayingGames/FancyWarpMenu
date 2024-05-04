/*
 * Copyright (c) 2024. TirelessTraveler
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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import ca.tirelesstraveler.fancywarpmenu.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Locale;

public class ChatListener {
    private final Minecraft mc;
    private boolean chatMessageSendDetected;

    public ChatListener() {
        mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        // type 0 is a standard chat message
        if (event.type == 0 && FancyWarpMenuState.isFancyWarpMenuOpen()) {
            String unformattedText = event.message.getUnformattedText();

            if (FancyWarpMenu.getSkyBlockConstants().getWarpMessages().getWarpFailMessages().containsKey(unformattedText)) {
                String failMessageKey = FancyWarpMenu.getSkyBlockConstants().getWarpMessages().getWarpFailMessages().get(unformattedText);
                ((GuiFancyWarp) mc.currentScreen).onWarpFail(failMessageKey);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (chatMessageSendDetected && mc.currentScreen instanceof GuiChat && event.gui == null) {
            chatMessageSendDetected = false;

            List<String> sentMessages = mc.ingameGUI.getChatGUI().getSentMessages();

            if (!sentMessages.isEmpty()) {
                checkChatMessageForReminder(sentMessages.get(sentMessages.size() - 1));
            }
        }
    }

    /**
     * Copies the throwable provided when the component from
     * {@link ChatUtils#sendErrorMessageWithCopyableThrowable(String, Throwable)} is clicked.
     *
     * @param event the mouse event
     */
    @SubscribeEvent
    public void mouseClicked(GuiScreenEvent.MouseInputEvent.Pre event) {
        // Left mouse button down
        if (event.gui instanceof GuiChat && Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            IChatComponent chatComponent =  mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (chatComponent != null && chatComponent.getChatStyle() != null) {
                ChatStyle chatStyle = chatComponent.getChatStyle();

                if (chatStyle.getInsertion().equals(ChatUtils.COPY_ERROR_TRANSLATION_KEY)
                        && chatStyle.getChatClickEvent() != null) {
                    String clickEventValue = chatStyle.getChatClickEvent().getValue();

                    if (clickEventValue != null) {
                        String copiedMessageTranslationKey = "fancywarpmenu.gui.buttons.copyToClipboard.copied";

                        GuiScreen.setClipboardString(clickEventValue);
                        ChatUtils.sendMessageWithModNamePrefix(new ChatComponentTranslation(copiedMessageTranslationKey)
                                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void keyTyped(GuiScreenEvent.KeyboardInputEvent event) {
        if (event.gui instanceof GuiChat &&
                (Keyboard.getEventKey() == Keyboard.KEY_RETURN || Keyboard.getEventKey() == Keyboard.KEY_NUMPADENTER) &&
                Keyboard.getEventKeyState()) {
            chatMessageSendDetected = true;
        }
    }

    /**
     * If the reminder feature is enabled, check a given chat message for a warp command variant.
     * If the message is a warp command variant, remind the player to use the Fancy Warp Menu instead of commands.
     *
     * @param sentChatMessage the chat message that was just sent
     * @see Settings#shouldSuggestWarpMenuOnWarpCommand()
     */
    private void checkChatMessageForReminder(String sentChatMessage) {
        if (Settings.shouldSuggestWarpMenuOnWarpCommand() && getWarpCommandVariant(sentChatMessage) != null) {
            sendReminderToUseFancyMenu();
        }
    }

    /**
     * Checks if a given command is the warp command or any of its variants and returns the corresponding
     * {@code WarpCommandVariant} object if one is found.
     *
     * @param command the command the player sent
     * @return a {@link WarpCommandVariant} if one with the same command is found, or {@code null} otherwise
     */
    private WarpCommandVariant getWarpCommandVariant(String command) {
        // Trim off the slash and all arguments
        String baseCommand = command.toLowerCase(Locale.US).substring(1).split(" ")[0];

        for (WarpCommandVariant commandVariant : FancyWarpMenu.getSkyBlockConstants().getWarpCommandVariants()) {
            if (commandVariant.getCommand().equals(baseCommand)) {
                return commandVariant;
            }
        }

        return null;
    }

    private void sendReminderToUseFancyMenu() {
        ChatUtils.sendMessageWithModNamePrefix(new ChatComponentTranslation(FancyWarpMenu
                .getFullLanguageKey("messages.useWarpMenuInsteadOfCommand"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }
}
