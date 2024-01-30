package ca.tirelesstraveler.fancywarpmenu.hooks;

import ca.tirelesstraveler.fancywarpmenu.LogHelper;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import ca.tirelesstraveler.fancywarpmenu.state.GameState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

public class EntityPlayerSPHook {
    public static void onSendChatMessage(String message, CallbackInfo ci) {
        if (Settings.isWarpMenuEnabled() && GameState.isOnSkyBlock() && message.startsWith("/")) {
            WarpCommandVariant warpCommandVariant = WarpMenuListener.getWarpCommandVariant(message);

            LogHelper.logDebug("Caught sent command: {}", message);

            // Check if the command is a warp command alias without any arguments
            if (warpCommandVariant != null && warpCommandVariant.getType() == WarpCommandVariant.WarpCommandType.ALIAS &&
                    message.trim().toLowerCase(Locale.US).equals("/" + warpCommandVariant.getCommand()) &&
                    Settings.shouldSuggestWarpMenuOnWarpCommand()) {
                WarpMenuListener.sendReminderToUseWarpScreen();
            }
        }
    }
}
