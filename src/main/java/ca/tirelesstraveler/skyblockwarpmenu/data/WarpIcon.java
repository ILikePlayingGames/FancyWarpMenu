package ca.tirelesstraveler.skyblockwarpmenu.data;

import ca.tirelesstraveler.skyblockwarpmenu.SkyBlockWarpMenu;
import net.minecraft.util.ResourceLocation;

/**
 * Class that holds the settings for drawing the warp icon (portal)
 */
@SuppressWarnings("unused")
public class WarpIcon {
    private String texturePath;
    private float widthPercentage;
    private float heightPercentage;
    private transient ResourceLocation textureLocation;

    private WarpIcon(){}

    public void init() {
        textureLocation = new ResourceLocation(SkyBlockWarpMenu.getInstance().getModId(), texturePath);
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public float getWidthPercentage() {
        return widthPercentage;
    }

    public float getHeightPercentage() {
        return heightPercentage;
    }
}
