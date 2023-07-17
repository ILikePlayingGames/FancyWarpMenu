package ca.tirelesstraveler.fancywarpmenu.hooks;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.LogHelper;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

public class EntityPlayerSPHook {
    public static void onSendChatMessage(String message, CallbackInfo ci) {
        String lowerCaseMessage = message.toLowerCase(Locale.US).trim();

        if (message.contains("/warp")) {
            LogHelper.logDebug("Caught send message: {}" +
                    "\nMenu Enabled: {}", lowerCaseMessage, Settings.isWarpMenuEnabled());
        }

        if (Settings.isWarpMenuEnabled() && FancyWarpMenu.getInstance().isPlayerOnSkyBlock() && lowerCaseMessage.startsWith("/")) {
            if (lowerCaseMessage.equals("/warp")) {
                FancyWarpMenu.getInstance().getWarpMenuListener().onWarpCommand();
                ci.cancel();
            } else if (Settings.shouldSuggestWarpMenuOnWarpCommand() &&
                    WarpMenuListener.isWarpCommand(lowerCaseMessage)) {
                WarpMenuListener.sendReminderToUseWarpScreen();
            }
        }
    }
}
