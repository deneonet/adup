package net.deneo.adup.data;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;

/**
 * <h3>A class for holding data about a log.</h3>
 *
 * @see Type
 * @since 1.2, at 1.5 - rework
 */
public class Log {
    public final String trackedName;
    public final ItemStack itemStack;
    public final Date date;
    public final String world;
    public final Log.Type type;
    public final Log.Storage storage;
    public final boolean taken;
    public final UUID source;

    public Log(ItemStack itemStack, String trackedName, Date date, String world, Log.Type type, Log.Storage storage, boolean taken, UUID source) {
        this.itemStack = itemStack;
        this.date = date;
        this.trackedName = trackedName;
        this.world = world;
        this.type = type;
        this.storage = storage;
        this.taken = taken;
        this.source = source;
    }

    public Log(ItemStack itemStack, String trackedName, Date date, String world, Log.Type type, Log.Storage storage, boolean taken) {
        this(itemStack, trackedName, date, world, type, storage, taken, null);
    }

    public Log(ItemStack itemStack, String trackedName, Date date, String world, Log.Type type, Log.Storage storage) {
        this(itemStack, trackedName, date, world, type, storage, false, null);
    }

    public Log(ItemStack itemStack, String trackedName, Date date, String world, Log.Type type, UUID source) {
        this(itemStack, trackedName, date, world, type, null, false, source);
    }

    public Log(ItemStack itemStack, String trackedName, Date date, String world, Log.Type type) {
        this(itemStack, trackedName, date, world, type, null, false, null);
    }

    /**
     * <h3>Returns the storage type for a inventory holder.</h3>
     *
     * @see Log.Storage
     */
    public static Log.@Nullable Storage getStorageFromInv(@NotNull Inventory inv) {
        // Storage entities
        if (inv.getHolder() instanceof HopperMinecart) {
            return Log.Storage.HOPPER_MINECART;
        }
        if (inv.getHolder() instanceof StorageMinecart) {
            return Log.Storage.CHEST_MINECART;
        }
        if (inv.getHolder() instanceof Donkey) {
            return Log.Storage.DONKEY_CHEST;
        }
        if (inv.getHolder() instanceof Llama) {
            return Log.Storage.LLAMA_CHEST;
        }
        if (inv.getHolder() instanceof Horse) {
            return Log.Storage.HORSE_CHEST;
        }

        // Double chest
        if (inv.getHolder() instanceof DoubleChest) {
            return Log.Storage.DOUBLE_CHEST;
        }

        // Storage blocks that exists >= 1.14
        try {
            if (inv.getType() == InventoryType.valueOf("BARREL")) {
                return Log.Storage.BARREL;
            }
            if (inv.getType() == InventoryType.valueOf("BLAST_FURNACE")) {
                return Log.Storage.BLAST_FURNACE;
            }
            if (inv.getType() == InventoryType.valueOf("SMOKER")) {
                return Log.Storage.SMOKER;
            }
        } catch (IllegalArgumentException ignored) {

        }

        // Storage block that exists <= 1.13
        switch (inv.getType()) {
            case CHEST:
                if (inv.getHolder() instanceof Chest) {
                    Chest chest = (Chest) inv.getHolder();
                    if (chest.getType() == Material.TRAPPED_CHEST) {
                        return Log.Storage.TRAPPED_CHEST;
                    }
                    return Log.Storage.CHEST;
                }

                return Log.Storage.BOAT_CHEST;
            case FURNACE:
                return Log.Storage.FURNACE;
            case SHULKER_BOX:
                return Log.Storage.SHULKER_BOX;
            case ENDER_CHEST:
                return Log.Storage.ENDER_CHEST;
            case DISPENSER:
                return Log.Storage.DISPENSER;
            case DROPPER:
                return Log.Storage.DROPPER;
            case HOPPER:
                return Log.Storage.HOPPER;
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Log)) {
            return false;
        }
        Log log = (Log) obj;
        if (log.type != type) {
            return false;
        }
        if (!log.itemStack.equals(itemStack)) {
            return false;
        }
        if (log.taken != taken) {
            return false;
        }
        if (!log.trackedName.equals(trackedName)) {
            return false;
        }
        if (!log.date.equals(date)) {
            return false;
        }
        if (log.source != source) {
            return false;
        }
        if (log.storage != storage) {
            return false;
        }
        return log.world.equals(world);
    }

    /**
     * <h3>All types of log that can happen in Adup</h3>
     * <pre>{@code PICKED_UP_PLAYER}</pre> One (or more) item(s) got dropped by another player and then picked up by another one.
     * <pre>{@code PICKED_UP_UNKNOWN}</pre> The player picked up an item, dropped from an entity or an explosion (everything not dropped from a player)
     * <pre>{@code PICKED_UP_SELF_DROPPED}</pre> One (or more) item(s) got dropped by the same player, that picked the items up.
     * <pre>{@code DROPPED}</pre> One (or more) item(s) got dropped, the log is removed, if the same player picks the item up or if the item despawns (because no one picked it up)
     * <pre>{@code MOVED}</pre> One (or more) item(s) got moved into a storage entity/block,
     */
    public enum Type {
        PICKED_UP_PLAYER,
        PICKED_UP_UNKNOWN,
        PICKED_UP_SELF_DROPPED,
        DROPPED,
        MOVED,
    }

    /**
     * <h3>All storage entity/blocks that Adup supports along a fancy name.</h3>
     *
     * @version <= 1.20.x
     */
    public enum Storage {
        HOPPER_MINECART("Hopper Minecart"),
        CHEST_MINECART("Chest Minecart"),
        HORSE_CHEST("Horse Chest"),
        LLAMA_CHEST("Llama Chest"),
        DONKEY_CHEST("Donkey Chest"),
        BARREL("Barrel"),
        BLAST_FURNACE("Blast Furnace"),
        SMOKER("Smoker"),
        CHEST("Chest"),
        TRAPPED_CHEST("Trapped Chest"),
        DOUBLE_CHEST("Double Chest"),
        FURNACE("Furnace"),
        SHULKER_BOX("Shulker Box"),
        BOAT_CHEST("Boat Chest"),
        ENDER_CHEST("Ender Chest"),
        DISPENSER("Dispenser"),
        DROPPER("Dropper"),
        HOPPER("Hopper");

        @Getter
        private final String fancyName;

        Storage(String fancyName) {
            this.fancyName = fancyName;
        }
    }
}
