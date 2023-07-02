package ca.tirelesstraveler.fancywarpmenu.hooks;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

public class EntityPlayerSPHook {
    public static void onSendChatMessage(String message, CallbackInfo ci) {
        String lowerCaseMessage = message.toLowerCase(Locale.US);
        if (Settings.isWarpMenuEnabled() && FancyWarpMenu.getInstance().isPlayerOnSkyBlock() && lowerCaseMessage.startsWith("/")) {
            if (lowerCaseMessage.equals("/warp")) {
                GuiFancyWarp warpScreen = new GuiFancyWarp();
                WarpMenuListener.setWarpScreen(warpScreen);
                Minecraft.getMinecraft().displayGuiScreen(warpScreen);
                ci.cancel();
            } else if (Settings.shouldSuggestWarpMenuOnWarpCommand() &&
                    WarpMenuListener.isWarpCommand(lowerCaseMessage)) {
                WarpMenuListener.sendReminderToUseWarpScreen();
            }
        }
    }
}
