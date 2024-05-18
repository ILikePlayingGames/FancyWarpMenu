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

package ca.tirelesstraveler.fancywarpmenu.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ChatUtils {
    public static final String COPY_TO_CLIPBOARD_TRANSLATION_KEY = "fancywarpmenu.gui.buttons.copyToClipboard";

    /**
     * Sends a client-side chat message with a warning message and a clickable component used to copy the stacktrace of
     * the given throwable.
     *
     * @param warningMessageTranslationKey translation key of the warning message to be displayed before the clickable component
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    public static void sendWarningMessageWithCopyableThrowable(String warningMessageTranslationKey, Throwable throwable) {
        ChatStyle errorMessageStyle = new ChatStyle().setColor(EnumChatFormatting.GOLD);
        sendMessageWithCopyableThrowable(warningMessageTranslationKey, errorMessageStyle, throwable);
    }

    /**
     * Sends a client-side chat message with an error message and a clickable component used to copy the stacktrace of
     * the given throwable.
     *
     * @param errorMessageTranslationKey translation key of the error message to be displayed before the clickable component
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    public static void sendErrorMessageWithCopyableThrowable(String errorMessageTranslationKey, Throwable throwable) {
        ChatStyle errorMessageStyle = new ChatStyle().setColor(EnumChatFormatting.RED);
        sendMessageWithCopyableThrowable(errorMessageTranslationKey, errorMessageStyle, throwable);
    }

    /**
     * Sends a client-side chat message with the mod name acronym as a prefix.
     * <br>
     * Example: [FWM] message
     *
     * @param message the message to send
     */
    public static void sendMessageWithModNamePrefix(String message) {
        sendMessageWithModNamePrefix(new ChatComponentText(message));
    }

    /**
     * Sends a client-side chat message with the mod name acronym as a prefix.
     * <br>
     * Example: [FWM] message
     *
     * @param message the message to send
     */
    public static void sendMessageWithModNamePrefix(IChatComponent message) {
        IChatComponent prefixComponent = createModNamePrefixComponent();
        prefixComponent.appendSibling(message);
        Minecraft.getMinecraft().thePlayer.addChatMessage(prefixComponent);
    }

    /**
     * Returns an {@code IChatComponent} with the acronym of the mod name ("FWM") to be used as a prefix in chat messages
     * sent by the mod
     *
     * @return an {@code IChatComponent} with the acronym of the mod name ("FWM") to be used as a prefix in chat messages
     * sent by the mod
     */
    private static IChatComponent createModNamePrefixComponent() {
        ChatStyle plainStyle = new ChatStyle().setColor(EnumChatFormatting.RESET);
        ChatStyle acronymStyle = new ChatStyle()
                .setColor(EnumChatFormatting.LIGHT_PURPLE)
                .setChatHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Fancy Warp Menu")));

        return new ChatComponentText("[")
                .appendSibling(new ChatComponentText("FWM").setChatStyle(acronymStyle))
                .appendSibling(new ChatComponentText("] ").setChatStyle(plainStyle));
    }

    /**
     * Sends a client-side chat message with a clickable component used to copy the stacktrace of a given throwable.
     *
     * @param messageTranslationKey translation key of the message to be displayed before the clickable component
     * @param messageStyle the {@link ChatStyle} to assign to the {@code IChatComponent} containing the message
     * @param throwable the throwable to be copied when the prompt is clicked
     */
    private static void sendMessageWithCopyableThrowable(String messageTranslationKey, ChatStyle messageStyle, Throwable throwable) {
        if (messageTranslationKey == null) {
            throw new NullPointerException("messageTranslationKey cannot be null");
        } else if (messageStyle == null) {
            throw new NullPointerException("messageStyle cannot be null");
        } else if (throwable == null) {
            throw new NullPointerException("throwable cannot be null");
        }

        ChatStyle plainStyle = new ChatStyle().setColor(EnumChatFormatting.RESET);
        // setInsertion gives the component a unique identifier for ca.tirelesstraveler.fancywarpmenu.listeners.ChatListener to look for
        ChatStyle copyThrowableStyle = new ChatStyle().setColor(EnumChatFormatting.BLUE)
                .setInsertion(messageTranslationKey)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ExceptionUtils.getStackTrace(throwable)));

        IChatComponent chatComponent = createModNamePrefixComponent()
                .appendSibling(new ChatComponentTranslation(messageTranslationKey).setChatStyle(messageStyle))
                .appendSibling(new ChatComponentText(" [").setChatStyle(plainStyle))
                .appendSibling(new ChatComponentTranslation(COPY_TO_CLIPBOARD_TRANSLATION_KEY).setChatStyle(copyThrowableStyle))
                .appendSibling(new ChatComponentText("]").setChatStyle(plainStyle));
        Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
    }
}