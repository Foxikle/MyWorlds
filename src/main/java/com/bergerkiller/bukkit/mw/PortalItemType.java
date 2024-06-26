package com.bergerkiller.bukkit.mw;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * A type of item used to place down portals that can otherwise not be placed
 */
public enum PortalItemType {
    NETHER_FRAME("nether", "Nether Portal"),
    END_FRAME("end", "End Portal"),
    END_GATE("gate", "End Gateway");

    private final String _name;
    private final String _displayName;

    private PortalItemType(String name, String displayName) {
        this._name = name;
        this._displayName = displayName;
    }

    public String getName() {
        return this._name;
    }

    public String getVisualName() {
        return this._displayName;
    }

    public BlockData getPlacedData(float playerYaw) {
        // Block that is placed down
        switch (this) {
        case NETHER_FRAME:
            BlockData data = BlockData.fromMaterial(MaterialUtil.getFirst("NETHER_PORTAL", "LEGACY_PORTAL"));
            if (FaceUtil.isAlongX(FaceUtil.yawToFace(playerYaw))) {
                data = data.setState("axis", "X");
            } else {
                data = data.setState("axis", "Z");
            }
            return data;
        case END_FRAME:
            return BlockData.fromMaterial(MaterialUtil.getFirst("END_PORTAL", "LEGACY_ENDER_PORTAL"));
        case END_GATE:
            return BlockData.fromMaterial(MaterialUtil.getFirst("END_GATEWAY", "LEGACY_END_GATEWAY"));
        }
        return BlockData.AIR;
    }

    public ItemStack createItem() {
        CommonItemStack item;

        // Visual representation of the item
        if (this == NETHER_FRAME) {
            Material m = MaterialUtil.getMaterial("PURPLE_STAINED_GLASS");
            if (m != null) {
                item = CommonItemStack.create(m, 1);
            } else {
                m = MaterialUtil.getMaterial("LEGACY_STAINED_GLASS");
                item = BlockData.fromMaterialData(m, 2).createCommonItem(1);
            }
        } else if (this == END_FRAME) {
            Material m = MaterialUtil.getMaterial("BLACK_STAINED_GLASS");
            if (m != null) {
                item = CommonItemStack.create(m, 1);
            } else {
                m = MaterialUtil.getMaterial("LEGACY_STAINED_GLASS");
                item = BlockData.fromMaterialData(m, 15).createCommonItem(1);
            }
        } else {
            item = CommonItemStack.create(MaterialUtil.getFirst("COAL_BLOCK", "LEGACY_COAL_BLOCK"), 1);
        }

        // Set metadata
        item.setCustomNameMessage(getVisualName() + " (My Worlds)");
        item.updateCustomData(tag -> {
            tag.putValue("myworlds.specialportal", getName());
        });
        return item.toBukkit();
    }

    public static PortalItemType fromItem(ItemStack item) {
        return fromItem(CommonItemStack.of(item));
    }

    public static PortalItemType fromItem(CommonItemStack item) {
        if (item.isEmpty() || !item.hasCustomData()) {
            return null;
        }

        Object value = item.getCustomData().getValue("myworlds.specialportal");
        if (value instanceof Number) {
            // This is from version 1.12.2 and before, where the Material type Id was used
            // This logic may be removed at a later time
            int id = ((Number) value).intValue();
            if (id == 90) {
                return NETHER_FRAME;
            } else if (id == 119) {
                return END_FRAME;
            } else if (id == 209) {
                return END_GATE;
            }
        } else if (value instanceof String) {
            // String key
            String name = (String) value;
            for (PortalItemType type : PortalItemType.values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
        }
        return null;
    }
}
