package ca.tirelesstraveler.fancywarpmenu.hooks;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.LogHelper;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

public class EntityPlayerSPHook {
    public static void onSendChatMessage(String message, CallbackInfo ci) {
        if (Settings.isWarpMenuEnabled() && FancyWarpMenu.getInstance().isPlayerOnSkyBlock() && message.startsWith("/")) {
            WarpCommandVariant warpCommandVariant = WarpMenuListener.getWarpCommandVariant(message);

            LogHelper.logDebug("Caught sent command: {}", message);

            if (warpCommandVariant != null) {
                // Check if the command is a warp command alias without any arguments
                if (warpCommandVariant.getType() == WarpCommandVariant.WarpCommandType.ALIAS && message.trim().toLowerCase(Locale.US).equals("/" + warpCommandVariant.getCommand())) {
                    FancyWarpMenu.getInstance().getWarpMenuListener().onWarpCommand();
                    ci.cancel();
                } else if (Settings.shouldSuggestWarpMenuOnWarpCommand()) {
                    WarpMenuListener.sendReminderToUseWarpScreen();
                }
            }
        }
    }
}
