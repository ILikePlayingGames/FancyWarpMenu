package ca.tirelesstraveler.skyblockwarpmenu.data;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

/**
 * Warp entry data used to create the warp buttons on the GUI
 */
@SuppressWarnings("unused")
public class Warp {
    // Height scale is the same as width
    /** Grid unit width is islandWidth / widthFactor */
    public static float GRID_UNIT_WIDTH_FACTOR = 40;
    public static WarpIcon warpIcon;
    private static int width;
    private static int height;
    private int gridX;
    private int gridY;
    private String displayName;
    private String commandName;

    private Warp() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceLocation getWarpTextureLocation() {
        return warpIcon.getTextureLocation();
    }

    /**
     * Returns the command the player has to send to use this warp.
     * If the {@code commandName} doesn't start with a '/', "/warp " is prepended.
     */
    public String getWarpCommand() {
        return commandName.startsWith("/") ? commandName : "/warp " + commandName;
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

    /**
     * Initializes width and height for all warp buttons
     */
    public static void initDefaults(ScaledResolution res) {
        calculateAndSetWidth(res);
        calculateAndSetHeight();
    }

    public static void setWarpIcon(WarpIcon warpIcon) {
        Warp.warpIcon = warpIcon;
    }

    private static void calculateAndSetWidth(ScaledResolution res) {
        width = (int) (res.getScaledWidth() * warpIcon.getWidthPercentage());
    }

    private static void calculateAndSetHeight() {
        height = (int) (width * warpIcon.getHeightPercentage());
    }
}
