package ca.tirelesstraveler.skyblockwarpmenu.data;

import ca.tirelesstraveler.skyblockwarpmenu.SkyBlockWarpMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Island data used to create the island buttons on the GUI
 */
@SuppressWarnings("unused")
public class Island {
    /** Grid unit width is screenWidth / widthFactor */
    public static final float GRID_UNIT_WIDTH_FACTOR = 32;
    /** Grid unit height is screenHeight / heightFactor */
    public static final float GRID_UNIT_HEIGHT_FACTOR = 18;

    private String name;
    private String texturePath;
    private int gridX;
    private int gridY;
    /** Width to render the island texture, given as a percentage of total screen width */
    private float widthPercentage;
    /** Height to render the island texture, given as a percentage of island texture width */
    private float heightPercentage;
    private List<Warp> warpList;
    private transient ResourceLocation textureLocation;
    private transient int width;
    private transient int height;

    private Island() {
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public List<Warp> getWarps() {
        return warpList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void init(ScaledResolution res) {
        calculateAndSetDimensions(res);
    }

    public void setTextureLocation() {
        textureLocation = new ResourceLocation(SkyBlockWarpMenu.getInstance().getModId(), texturePath);
    }

    private void calculateAndSetDimensions(ScaledResolution res) {
        width = (int) (res.getScaledWidth() * widthPercentage);
        height = (int) (width * heightPercentage);
    }
}
