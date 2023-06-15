package ca.tirelesstraveler.skyblockwarpmenu.data;

import java.util.List;

@SuppressWarnings("unused")
public class WarpConfiguration {
    private List<Island> islandList;
    private WarpIcon warpIcon;

    private WarpConfiguration(){}

    public List<Island> getIslandList() {
        return islandList;
    }

    public WarpIcon getWarpIcon() {
        return warpIcon;
    }
}
