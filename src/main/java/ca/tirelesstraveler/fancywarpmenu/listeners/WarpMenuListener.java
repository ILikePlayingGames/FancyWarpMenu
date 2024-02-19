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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.SkyBlockConstants;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.WarpCommandVariant;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuConfigScreen;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiFastTravel;
import ca.tirelesstraveler.fancywarpmenu.gui.GuiRiftFastTravel;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import ca.tirelesstraveler.fancywarpmenu.state.GameState;
import ca.tirelesstraveler.fancywarpmenu.utils.GameChecks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

/**
 * General purpose event listener
 */
public class WarpMenuListener implements IResourceManagerReloadListener {
    private static final Minecraft mc;
    private static final FancyWarpMenu modInstance;
    private static final Logger logger;

    static {
        mc = Minecraft.getMinecraft();
        modInstance = FancyWarpMenu.getInstance();
        logger = LogManager.getLogger();
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
        /*
        Minecraft closes the current screen after executing a command, meaning any GuiScreen opened by the command is closed.
        This section interrupts the closing of the current screen to get around this behavior.
        */
        if (event.gui == null) {
            if (FancyWarpMenuState.isOpenConfigMenuRequested()) {
                event.gui = new FancyWarpMenuConfigScreen(null);
                FancyWarpMenuState.setOpenConfigMenuRequested(false);
            }
        } else if (event.gui instanceof GuiChest) {
            IInventory playerInventory = mc.thePlayer.inventory;
            IInventory chestInventory = ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory();

            Menu currentMenu = GameChecks.determineOpenMenu(chestInventory);

            if (currentMenu == Menu.FAST_TRAVEL) {
                event.gui = new GuiFastTravel(playerInventory, chestInventory, FancyWarpMenuState.getOverworldLayout());
            } else if (currentMenu == Menu.PORHTAL) {
                event.gui = new GuiRiftFastTravel(playerInventory, chestInventory, FancyWarpMenuState.getRiftLayout());
            }
        }
    }

    @SubscribeEvent
    public void keyTyped(InputEvent.KeyInputEvent event) {
        if (Settings.isWarpMenuEnabled() && (GameState.isOnSkyBlock() || Settings.shouldSkipSkyBlockCheck()) &&
                FancyWarpMenu.getKeyBindingOpenWarpMenu().isPressed()) {
            mc.thePlayer.sendChatMessage(SkyBlockConstants.WARP_COMMAND_BASE);
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(modInstance.getModId())) {
            Settings.syncConfig(false);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        FancyWarpMenu.getInstance().reloadLayouts();
    }

    /**
     * Checks if a given command is the warp command or any of its variants and returns the corresponding
     * {@code WarpCommandVariant} object if one is found.
     *
     * @param command the command the player sent
     * @return a {@link WarpCommandVariant} if one with the same command is found, or {@code null} otherwise
     */
    public static WarpCommandVariant getWarpCommandVariant(String command) {
        // Trim off the slash and all arguments
        String baseCommand = command.toLowerCase(Locale.US).substring(1).split(" ")[0];

        for (WarpCommandVariant commandVariant : FancyWarpMenu.getSkyBlockConstants().getWarpCommandVariants()) {
            if (commandVariant.getCommand().equals(baseCommand)) {
                return commandVariant;
            }
        }

        return null;
    }

    public static void sendReminderToUseWarpScreen() {
        mc.thePlayer.addChatMessage(new ChatComponentTranslation(FancyWarpMenu
                .getFullLanguageKey("messages.useWarpMenuInsteadOfCommand"))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }
}
