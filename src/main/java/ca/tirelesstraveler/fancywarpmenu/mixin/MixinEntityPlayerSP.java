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

package ca.tirelesstraveler.fancywarpmenu.mixin;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

/**
 * This mixin intercepts warp menu requests that use {@link EntityPlayerSP#sendChatMessage(String)} to send
 * commands. This includes requests from {@link net.minecraft.client.gui.GuiScreen#sendChatMessage(String, boolean)} and
 * other mods.
 */
@SuppressWarnings("unused")
@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true, require = 1)
    public void onSendChatMessage(String message, CallbackInfo ci) {
        String lowerCaseMessage = message.toLowerCase(Locale.US);

        if (Settings.isWarpMenuEnabled() && FancyWarpMenu.getInstance().isPlayerOnSkyBlock() && lowerCaseMessage.startsWith("/")) {
            if (lowerCaseMessage.equals("/warp")) {
                GuiFancyWarp warpScreen = new GuiFancyWarp();
                WarpMenuListener.setWarpScreen(warpScreen);
                Minecraft.getMinecraft().displayGuiScreen(warpScreen);
                ci.cancel();
            } else if (Settings.shouldSuggestWarpMenuOnWarpCommand() &&
                    WarpMenuListener.isWarpCommand(lowerCaseMessage.substring(1, lowerCaseMessage.indexOf(' ')))) {
                WarpMenuListener.sendReminderToUseWarpScreen();
            }
        }
    }
}
