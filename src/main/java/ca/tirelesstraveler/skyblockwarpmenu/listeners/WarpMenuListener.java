package ca.tirelesstraveler.skyblockwarpmenu.listeners;

import ca.tirelesstraveler.skyblockwarpmenu.SkyBlockWarpMenu;
import ca.tirelesstraveler.skyblockwarpmenu.data.Settings;
import ca.tirelesstraveler.skyblockwarpmenu.gui.GuiFancyWarp;
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    private static final Field chatInputField;
    private static final MethodHandle inputFieldGetterHandle;

    static {
        try {
            String inputFieldName = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ? "inputField" : "field_146415_a";
            chatInputField = GuiChat.class.getDeclaredField(inputFieldName);
            chatInputField.setAccessible(true);
            inputFieldGetterHandle = MethodHandles.lookup().unreflectGetter(chatInputField);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Failed to access chat text box"));
        }
    }

    /**
     * Redirect to the fancy warp menu when the player attempts to access the warp menu with the /warp command
     */
    @SubscribeEvent
    public void onGuiKeyboardInput(GuiScreenEvent.KeyboardInputEvent event) {
        if (Settings.isWarpMenuEnabled()
                && SkyBlockWarpMenu.getInstance().isPlayerOnSkyblock()
                && event.gui instanceof GuiChat
                && (Keyboard.getEventKey() == Keyboard.KEY_RETURN
                || Keyboard.getEventKey() == Keyboard.KEY_NUMPADENTER)) {
            try {
                GuiTextField textField = (GuiTextField) inputFieldGetterHandle.invokeExact((GuiChat) event.gui);
                String chatMessage = textField.getText().trim();

                if (chatMessage.trim().equals("/warp")) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiFancyWarp());
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
                && SkyBlockWarpMenu.getInstance().isPlayerOnSkyblock()
                && Mouse.getEventButton() == 0
                && Mouse.getEventButtonState()
                && event.gui instanceof GuiChest) {
                GuiChest guiChest = (GuiChest) event.gui;

                if (guiChest.inventorySlots instanceof ContainerChest
                        && ((ContainerChest)guiChest.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText().equals("SkyBlock Menu")
                        && guiChest.getSlotUnderMouse().getSlotIndex() == 47) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiFancyWarp());
                    event.setCanceled(true);
                }
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(SkyBlockWarpMenu.getInstance().getModId())) {
            Settings.syncConfig(false);
        }
    }
}
