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

package ca.tirelesstraveler.fancywarpmenu.commands;

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import ca.tirelesstraveler.fancywarpmenu.utils.ChatUtils;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

/**
 * This is the main command of the mod.<br>
 * <bold>Syntax</bold><br>
 * <ul>
 *     <li>/fancywarpmenu &lt;args&gt;</li>
 *     <li>/fwm &lt;args&gt;</li>
 * </ul>
 * <br>
 * <bold>Arguments</bold>
 * <ul>
 *     <li>(empty) - Open mod config menu</li>
 *     <li>"on" or "1" - Enable Fancy Warp Menu</li>
 *     <li>"off" or "0" - Disable Fancy Warp Menu</li>
 * </ul>
 */
public class FancyWarpMenuCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("fwm");
    }

    @Override
    public String getCommandName() {
        return "fancywarpmenu";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        // Is not shown in a server-side help menu
        return "";
    }

    /**
     * The chat GUI closes the current screen after executing the command.
     * The {@link net.minecraftforge.event.CommandEvent} is intercepted instead to prevent the screen from being closed.
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            switch (args[0]) {
                case "1":
                case "on":
                    Settings.setWarpMenuEnabled(true);
                    ChatUtils.sendMessageWithModNamePrefix(
                            new ChatComponentTranslation("fancywarpmenu.messages.fancyWarpMenuEnabled")
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                    break;
                case "0":
                case "off":
                    Settings.setWarpMenuEnabled(false);
                    ChatUtils.sendMessageWithModNamePrefix(
                            new ChatComponentTranslation("fancywarpmenu.messages.fancyWarpMenuDisabled")
                                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                    break;
                default:
                    throw new SyntaxErrorException();
            }
        } else {
            FancyWarpMenuState.setOpenConfigMenuRequested(true);
        }
    }
}
