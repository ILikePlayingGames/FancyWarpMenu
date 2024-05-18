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

import java.util.List;
import java.util.regex.Pattern;

/**
 * A match condition that checks if a given SkyBlock {@code GuiChest} menu contains an item with the same
 * item name, inventory slot index, Minecraft item ID, and SkyBlock item ID as the item specified in this condition.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class ItemMatchCondition {
    public static final Logger logger = LogManager.getLogger();

    /**
     * Display name of the {@code ItemStack} (excluding formatting codes).
     * Either {@code itemName} or {@code itemNames} can be set. {@code IllegalArgumentException} will be thrown if both are set.
     */
    private String itemName;
    /**
     * List of possible display names of the {@code ItemStack} (excluding formatting codes), used when the item name varies.
     * Either {@code itemName} or {@code itemNames} can be set. {@code IllegalArgumentException} will be thrown if both are set.
     */
    private List<String> itemNames;
    /** The slot index of the slot the item occupies in the {@code IInventory} provided to {@link #inventoryContainsMatchingItem(IInventory)} */
    private int inventorySlotIndex = -1;
    /**
     * Minecraft item ID string of the {@code ItemStack}.
     * Either {@code minecraftItemID} or {@code minecraftItemIDs} can be set.
     * {@code IllegalArgumentException} will be thrown if both are set.
     */
    private String minecraftItemID;
    /**
     * List of possible Minecraft item ID strings of the {@code ItemStack}, used when Minecraft item ID varies
     * Either {@code minecraftItemID} or {@code minecraftItemIDs} can be set.
     * {@code IllegalArgumentException} will be thrown if both are set.
     */
    private List<String> minecraftItemIDs;

    /**
     * SkyBlock Item ID string
     * Either {@code skyBlockItemID} or {@code skyBlockItemIDs} can be set.
     * {@code IllegalArgumentException} will be thrown if both are set.
     **/

    private String skyBlockItemID;
    /**
     * List of possible SkyBlock item ID strings of the {@code ItemStack}, used when SkyBlock item ID varies.
     * Either {@code skyBlockItemID} or {@code skyBlockItemIDs} can be set.
     * {@code IllegalArgumentException} will be thrown if both are set.
     */
    private List<String> skyBlockItemIDs;

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

            if (itemName != null || itemNames != null) {
                String itemStackName = itemStack.hasDisplayName() ?
                        StringUtils.stripControlCodes(itemStack.getDisplayName()) : null;
                itemNameMatches = itemStackName != null
                        && (itemStackName.equals(itemName) || itemNames.contains(itemStackName));

                if (!itemNameMatches) {
                    logger.warn("Item name mismatch\nExpected {} ; Found {}",
                            itemName, itemStackName);
                    return false;
                }
            }

            if (minecraftItemID != null || minecraftItemIDs != null) {
                String itemStackMinecraftItemID = itemStack.getItem().getRegistryName();
                minecraftItemIDMatches = itemStackMinecraftItemID.equals(minecraftItemID)
                        || minecraftItemIDs.contains(itemStackMinecraftItemID);

                if (!minecraftItemIDMatches) {
                    logger.warn("Minecraft Item ID mismatch\nExpected {} ; Found {}",
                            minecraftItemID, itemStackMinecraftItemID);
                    return false;
                }
            }

            // Following checks require NBT data, fail if NBT data not present
            if (!itemStack.hasTagCompound()) return false;

            if (skyBlockItemID != null || skyBlockItemIDs != null) {
                if (!itemStack.getTagCompound().hasKey("ExtraAttributes", Constants.NBT.TAG_COMPOUND)) {
                    return false;
                }

                NBTTagCompound extraAttributesTag = itemStack.getSubCompound("ExtraAttributes", false);
                String itemStackSkyBlockID = extraAttributesTag.hasKey("id", Constants.NBT.TAG_STRING) ?
                        extraAttributesTag.getString("id") : null;
                skyBlockItemIDMatches = itemStackSkyBlockID != null &&
                        (itemStackSkyBlockID.equals(skyBlockItemID) || skyBlockItemIDs.contains(itemStackSkyBlockID));

                if (!skyBlockItemIDMatches) {
                    logger.warn("SkyBlock Item ID mismatch\nExpected {} ; Found {}",
                            skyBlockItemID, itemStackSkyBlockID);
                    return false;
                }
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

                        if (!lorePatternMatches) {
                            logger.warn("Lore did not match pattern\nItem lore: {}",  loreTag);
                            return false;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifies that this match condition's properties are valid.
     * This is called for conditions that have just been deserialized.
     */
    public void validateCondition() {
        if (this.inventorySlotIndex <= -1) {
            throw new IllegalArgumentException("inventorySlotIndex must be greater than or equal to 0");
        }

        if (this.itemName == null && (this.itemNames == null || this.itemNames.isEmpty())
                && this.minecraftItemID == null && (this.minecraftItemIDs == null || this.minecraftItemIDs.isEmpty())
                && this.skyBlockItemID == null && (skyBlockItemIDs == null || this.skyBlockItemIDs.isEmpty())
                && this.loreMatchPattern == null) {
            throw new IllegalArgumentException("No item name, Minecraft item ID, SkyBlock item ID, or lore criteria specified.");
        }

        if (this.itemName != null && this.itemNames != null) {
            throw new IllegalArgumentException("itemName and itemNames cannot both be set. Only one can be set.");
        }

        if (this.minecraftItemID != null && this.minecraftItemIDs != null) {
            throw new IllegalArgumentException("minecraftItemID and minecraftItemIDs cannot both be set. Only one can be set.");
        }

        if (this.skyBlockItemID != null && this.skyBlockItemIDs != null) {
            throw new IllegalArgumentException("skyBlockItemID and skyBlockItemIDs cannot both be set. Only one can be set.");
        }

        if (this.loreMatchPattern != null && loreMatchPattern.pattern() == null) {
            throw new IllegalArgumentException(
                    String.format("Lore match pattern for item in slot %d lacks a regex string.", inventorySlotIndex));
        }
    }
}
