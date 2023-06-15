package ca.tirelesstraveler.skyblockwarpmenu.gui;

import ca.tirelesstraveler.skyblockwarpmenu.data.Settings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;

public class SWMConfigGuiScreen extends GuiConfig {
    public SWMConfigGuiScreen(GuiScreen parent)
    {
        super(parent, Settings.getConfigElements(), "skyblockwarpmenu", "main", false, false, I18n.format("skyblockwarpmenu.config.title"));
    }


}
