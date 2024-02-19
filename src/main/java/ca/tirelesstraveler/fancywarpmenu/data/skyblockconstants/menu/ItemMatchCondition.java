/*
 * Copyright (c) 2024. TirelessTraveler
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

package ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

/**
 * A match condition that checks if a given SkyBlock {@code GuiChest} menu contains an item with the same
 * item name, inventory slot index, Minecraft item ID, and SkyBlock item ID as the item specified in this condition.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class ItemMatchCondition {
    public static final Logger logger = LogManager.getLogger();

    /** Display name of the {@code ItemStack} (excluding formatting codes) */
    private String itemName;
    /** The slot index of the slot the item occupies in the {@code IInventory} provided to {@link #inventoryContainsMatchingItem(IInventory)} */
    private int inventorySlotIndex = -1;
    /** Minecraft item ID string */
    private String minecraftItemID;
    /** SkyBlock Item ID string */
    private String skyBlockItemID;
    /**
     * Pattern to test against the item's lore.
     * Note the lore will be combined into one string with lines separated by {@code \n} and then that string will be matched against the pattern.
     **/
    private Pattern loreMatchPattern;

    private ItemMatchCondition() {}

    public String getItemName() {
        return itemName;
    }

    public int getInventorySlotIndex() {
        return inventorySlotIndex;
    }

    public String getMinecraftItemID() {
        return minecraftItemID;
    }

    public String getSkyBlockItemID() {
        return skyBlockItemID;
    }

    public Pattern getLoreMatchPattern() {
        return loreMatchPattern;
    }

    /**
     * Checks whether the given {@code IInventory} contains an item that satisfies this item match condition.
     *
     * @param inventory the inventory to check for a matching item
     * @return {@code true} if an item in {@code inventory} satisfies this item match condition, {@code false} otherwise
     */
    public boolean inventoryContainsMatchingItem(IInventory inventory) {
        if (inventory == null) {
            throw new NullPointerException("Inventory cannot be null");
        } else if (inventory.getSizeInventory() <= 0) {
            throw new IllegalArgumentException("Cannot check for matching item in empty inventory");
        } else if (inventory.getSizeInventory() < inventorySlotIndex) {
            throw new IllegalArgumentException(
                    String.format("Inventory size (%d) is smaller than match condition slot index (%d)",
                            inventory.getSizeInventory(), inventorySlotIndex));
        }

        ItemStack itemStack = inventory.getStackInSlot(inventorySlotIndex);

        if (itemStack != null) {
            boolean itemNameMatches;
            boolean minecraftItemIDMatches;
            boolean skyBlockItemIDMatches;
            boolean lorePatternMatches;

            if (itemName != null) {
                itemNameMatches = itemStack.hasDisplayName() &&
                        StringUtils.stripControlCodes(itemStack.getDisplayName()).equals(itemName);
                logger.debug("Item name matches: {}", itemNameMatches);

                if (!itemNameMatches) return false;
            }

            if (minecraftItemID != null) {
                minecraftItemIDMatches = itemStack.getItem().getRegistryName().equals(minecraftItemID);
                logger.debug("Minecraft registry ID matches: {}", minecraftItemIDMatches);

                if (!minecraftItemIDMatches) return false;
            }

            // Following checks require NBT data, fail if NBT data not present
            if (!itemStack.hasTagCompound()) return false;

            if (skyBlockItemID != null) {
                if (!itemStack.getTagCompound().hasKey("ExtraAttributes", Constants.NBT.TAG_COMPOUND)) {
                    return false;
                }

                NBTTagCompound extraAttributesTag = itemStack.getSubCompound("ExtraAttributes", false);
                skyBlockItemIDMatches = extraAttributesTag.hasKey("id", Constants.NBT.TAG_STRING) &&
                        extraAttributesTag.getString("id").equals(skyBlockItemID);
                logger.debug("SkyBlock Item ID matches: {}", skyBlockItemIDMatches);

                if (!skyBlockItemIDMatches) return false;
            }

            if (loreMatchPattern != null && loreMatchPattern.pattern() != null) {
                if (!itemStack.getTagCompound().hasKey("display", Constants.NBT.TAG_COMPOUND)) {
                    return false;
                }

                NBTTagCompound displayTag = itemStack.getSubCompound("display", false);

                if (displayTag.hasKey("Lore", Constants.NBT.TAG_LIST)) {
                    NBTTagList loreTag = displayTag.getTagList("Lore", Constants.NBT.TAG_STRING);

                    if (loreTag.tagCount() > 0) {
                        StringBuilder loreStringBuilder = new StringBuilder();

                        for (int i = 0; i < loreTag.tagCount(); i++) {
                            loreStringBuilder.append(loreTag.getStringTagAt(i)).append("\n");
                        }
                        loreStringBuilder.deleteCharAt(loreStringBuilder.length() - 1);

                        String loreString = loreStringBuilder.toString();

                        lorePatternMatches = loreMatchPattern.asPredicate().test(loreString);
                        logger.debug("Lore pattern matches: {}", lorePatternMatches);

                        return lorePatternMatches;
                    }
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Verifies that this match condition's properties are valid.
     * This is called for conditions that have just been deserialized.
     */
    public void validateCondition() {

        if (this.inventorySlotIndex <= -1) {
            throw new IllegalArgumentException("inventorySlotIndex must be greater than or equal to 0");
        }

        if (this.itemName == null && this.minecraftItemID == null && this.skyBlockItemID == null && this.loreMatchPattern == null) {
            throw new IllegalArgumentException("No item name, Minecraft item ID, SkyBlock item ID, or lore criteria specified.");
        }

        if (this.loreMatchPattern != null && loreMatchPattern.pattern() == null) {
            throw new IllegalArgumentException(
                    String.format("Lore match pattern for item in slot %d lacks a regex string.", inventorySlotIndex));
        }
    }
}
