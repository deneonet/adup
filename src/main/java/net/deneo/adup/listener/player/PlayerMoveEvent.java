package net.deneo.adup.listener.player;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.PermissionsUtil;
import net.deneo.adup.utility.WarningsUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;

public class PlayerMoveEvent implements Listener {
    private final Map<HumanEntity, PlayerActiveInventoryModifications> containersByOwner = new HashMap<>();
    private final Map<Location, ArrayList<PlayerActiveInventoryModifications>> containersByLocation = new HashMap<>();

    public static Material getInventoryHolderType(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return getInventoryHolderType(((DoubleChest) holder).getLeftSide());
        } else if (holder instanceof BlockState) {
            return ((BlockState) holder).getType();
        }

        return null;
    }

    public static Location getInventoryHolderLocation(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return getInventoryHolderLocation(((DoubleChest) holder).getLeftSide());
        } else if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }

        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        boolean isMoveTrackingEnabled = ConfigUtil.getBoolean("tracking.move");
        if (!isMoveTrackingEnabled) {
            return;
        }

        final HumanEntity entity = event.getPlayer();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        if (Log.getStorageFromInv(event.getInventory()) != null) {
            final PlayerActiveInventoryModifications modifications = containersByOwner.remove(player);

            if (modifications != null) {
                final Location loc = modifications.getLocation();
                ArrayList<PlayerActiveInventoryModifications> atLocation = containersByLocation.get(loc);
                atLocation.remove(modifications);

                if (atLocation.isEmpty()) {
                    containersByLocation.remove(loc);
                }

                modifications.flush();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        boolean isMoveTrackingEnabled = ConfigUtil.getBoolean("tracking.move");
        if (!isMoveTrackingEnabled) {
            return;
        }

        final HumanEntity entity = event.getPlayer();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        if (event.getInventory() != null) {
            InventoryHolder holder = event.getInventory().getHolder();

            if (Log.getStorageFromInv(event.getInventory()) != null) {
                PlayerActiveInventoryModifications modifications = new PlayerActiveInventoryModifications(player, getInventoryHolderLocation(holder), Log.getStorageFromInv(event.getInventory()));
                containersByOwner.put(modifications.getActor(), modifications);
                containersByLocation.compute(modifications.getLocation(), (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }

                    v.add(modifications);
                    return v;
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        boolean isMoveTrackingEnabled = ConfigUtil.getBoolean("tracking.move");
        if (!isMoveTrackingEnabled) {
            return;
        }

        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        if (Log.getStorageFromInv(event.getInventory()) != null) {
            final PlayerActiveInventoryModifications modifications = containersByOwner.get(player);

            if (modifications != null) {
                switch (event.getAction()) {
                    case PICKUP_ONE:
                    case DROP_ONE_SLOT:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCurrentItem(), -1);
                        }
                        break;
                    case PICKUP_HALF:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCurrentItem(), -(event.getCurrentItem().getAmount() + 1) / 2);
                        }
                        break;
                    case PICKUP_SOME:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            int taken = event.getCurrentItem().getAmount() - event.getCurrentItem().getMaxStackSize();
                            modifications.addModification(event.getCursor(), -taken);
                        }
                        break;
                    case PICKUP_ALL:
                    case DROP_ALL_SLOT:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCurrentItem(), -event.getCurrentItem().getAmount());
                        }
                        break;
                    case PLACE_ONE:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCursor(), 1);
                        }
                        break;
                    case PLACE_SOME:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            int placeable = event.getCurrentItem().getMaxStackSize() - event.getCurrentItem().getAmount();
                            modifications.addModification(event.getCursor(), placeable);
                        }
                        break;
                    case PLACE_ALL:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCursor(), event.getCursor().getAmount());
                        }
                        break;
                    case SWAP_WITH_CURSOR:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            modifications.addModification(event.getCursor(), event.getCursor().getAmount());
                            modifications.addModification(event.getCurrentItem(), -event.getCurrentItem().getAmount());
                        }
                        break;
                    case MOVE_TO_OTHER_INVENTORY:
                        boolean removed = event.getRawSlot() < event.getView().getTopInventory().getSize();
                        modifications.addModification(event.getCurrentItem(), event.getCurrentItem().getAmount() * (removed ? -1 : 1));
                        break;
                    case COLLECT_TO_CURSOR:
                        ItemStack cursor = event.getCursor();
                        if (cursor == null) {
                            return;
                        }

                        int toPickUp = cursor.getMaxStackSize() - cursor.getAmount();
                        int takenFromContainer = 0;
                        boolean takeFromFullStacks = false;
                        Inventory top = event.getView().getTopInventory();
                        Inventory bottom = event.getView().getBottomInventory();

                        while (toPickUp > 0) {
                            for (ItemStack stack : top.getStorageContents()) {
                                if (cursor.isSimilar(stack) && takeFromFullStacks == (stack.getAmount() == stack.getMaxStackSize())) {
                                    int take = Math.min(toPickUp, stack.getAmount());
                                    toPickUp -= take;
                                    takenFromContainer += take;

                                    if (toPickUp <= 0) {
                                        break;
                                    }
                                }
                            }

                            if (toPickUp <= 0) {
                                break;
                            }

                            for (ItemStack stack : bottom.getStorageContents()) {
                                if (cursor.isSimilar(stack) && takeFromFullStacks == (stack.getAmount() == stack.getMaxStackSize())) {
                                    int take = Math.min(toPickUp, stack.getAmount());
                                    toPickUp -= take;

                                    if (toPickUp <= 0) {
                                        break;
                                    }
                                }
                            }

                            if (takeFromFullStacks) {
                                break;
                            }

                            takeFromFullStacks = true;
                        }

                        if (takenFromContainer > 0) {
                            modifications.addModification(event.getCursor(), -takenFromContainer);
                        }
                        break;
                    case HOTBAR_SWAP:
                    case HOTBAR_MOVE_AND_READD:
                        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                            ItemStack otherSlot = (event.getClick() == ClickType.valueOf("SWAP_OFFHAND")) ? event.getWhoClicked().getInventory().getItemInOffHand() : event.getWhoClicked().getInventory().getItem(event.getHotbarButton());

                            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                                modifications.addModification(event.getCurrentItem(), -event.getCurrentItem().getAmount());
                            }

                            if (otherSlot != null && otherSlot.getType() != Material.AIR) {
                                modifications.addModification(otherSlot, otherSlot.getAmount());
                            }
                        }
                        break;
                    case DROP_ALL_CURSOR:
                    case DROP_ONE_CURSOR:
                    case CLONE_STACK:
                    case NOTHING:
                    case UNKNOWN:
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        boolean isMoveTrackingEnabled = ConfigUtil.getBoolean("tracking.move");
        if (!isMoveTrackingEnabled) {
            return;
        }

        final HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        if (Log.getStorageFromInv(event.getInventory()) != null) {
            final PlayerActiveInventoryModifications modifications = containersByOwner.get(player);

            if (modifications != null) {
                Inventory container = event.getView().getTopInventory();
                int containerSize = container.getSize();

                for (Entry<Integer, ItemStack> e : event.getNewItems().entrySet()) {
                    int slot = e.getKey();

                    if (slot < containerSize) {
                        ItemStack old = container.getItem(slot);
                        int oldAmount = (old == null || old.getType() == Material.AIR) ? 0 : old.getAmount();
                        modifications.addModification(e.getValue(), e.getValue().getAmount() - oldAmount);
                    }
                }
            }
        }
    }

    private class PlayerActiveInventoryModifications {
        private final Player actor;
        private final Location location;
        private final Log.Storage type;
        private final HashMap<ItemStack, Integer> modifications;

        public PlayerActiveInventoryModifications(Player actor, Location location, Log.Storage type) {
            this.actor = actor;
            this.location = location;
            this.modifications = new HashMap<>();
            this.type = type;
        }

        public void addModification(ItemStack stack, int amount) {
            if (amount == 0) {
                return;
            }

            ArrayList<PlayerActiveInventoryModifications> allViewers = containersByLocation.get(location);
            if (allViewers.size() > 1) {
                for (PlayerActiveInventoryModifications other : allViewers) {
                    if (other != this) {
                        other.flush();
                    }
                }
            }

            stack = new ItemStack(stack);
            stack.setAmount(1);
            Integer existing = modifications.get(stack);

            int newTotal = amount + (existing == null ? 0 : existing);
            if (newTotal == 0) {
                modifications.remove(stack);
                return;
            }

            modifications.put(stack, newTotal);
        }

        public void flush() {
            if (!modifications.isEmpty()) {
                for (Entry<ItemStack, Integer> e : modifications.entrySet()) {
                    ItemStack stack = e.getKey();
                    int itemAmount = e.getValue();
                    stack.setAmount(Math.abs(itemAmount));

                    String world = actor.getWorld().getName();
                    Date date = new Date();

                    int amount = -1;
                    int maxCount = 0;

                    List<Map<String, Object>> trackedItems = ConfigUtil.getMapList("tracking.items");

                    boolean contains = false;
                    boolean enchantments = false;
                    boolean displayName = false;
                    boolean lore = false;

                    String name = "";

                    for (Map<String, Object> item : trackedItems) {
                        ItemMeta trackedItemMeta = (ItemMeta) item.get("metadata");
                        ItemMeta itemMeta = stack.getItemMeta();
                        lore = (boolean) item.get("lore");
                        enchantments = (boolean) item.get("enchantments");
                        displayName = (boolean) item.get("display_name");

                        if (enchantments && !trackedItemMeta.getEnchants().equals(itemMeta.getEnchants())) {
                            continue;
                        }
                        if (displayName && !trackedItemMeta.getDisplayName().equals(itemMeta.getDisplayName())) {
                            continue;
                        }
                        if (lore && !trackedItemMeta.getLore().equals(itemMeta.getLore())) {
                            continue;
                        }

                        String type = (String) item.get("type");
                        if (type.equals(stack.getType().toString())) {
                            int threshold = (Integer) item.get("move_threshold");
                            if (threshold == 0) {
                                continue;
                            }

                            name = (String) item.get("name");
                            contains = true;
                            maxCount = threshold;
                            break;
                        }
                    }

                    if (contains) {
                        List<Log> logs = GlobalTables.logsTable.bSelect(actor.getUniqueId().toString());
                        for (Log log : logs) {
                            if (log.type != Log.Type.MOVED) {
                                continue;
                            }

                            ItemMeta logItemMeta = log.itemStack.getItemMeta();
                            ItemMeta itemMeta = stack.getItemMeta();

                            if (enchantments && !logItemMeta.getEnchants().equals(itemMeta.getEnchants())) {
                                continue;
                            }
                            if (displayName && !logItemMeta.getDisplayName().equals(itemMeta.getDisplayName())) {
                                continue;
                            }
                            if (lore && !logItemMeta.getLore().equals(itemMeta.getLore())) {
                                continue;
                            }

                            if (log.itemStack.getType().equals(stack.getType())) {
                                amount += log.itemStack.getAmount();
                            }
                        }
                    }

                    if (amount == -1) {
                        AdupPlayer adupPlayer = GlobalTables.playersTable.getOrInsertPlayer(actor.getUniqueId());
                        adupPlayer.warnings = 0;
                        adupPlayer.moveMultiplier = 0;

                        GlobalTables.playersTable.updatePlayer(actor.getUniqueId(), adupPlayer, "SET warnings = ?, move_multiplier = ?", (statement -> {
                            statement.setInt(1, 0);
                            statement.setInt(2, 0);
                            return 3;
                        }));
                    }

                    if (contains) {
                        amount += stack.getAmount() + 1;
                    }

                    int multiplier = GlobalTables.playersTable.getOrInsertPlayer(actor.getUniqueId()).moveMultiplier;
                    if (multiplier == 0) {
                        multiplier = 1;
                    }

                    AdupPlayer adupPlayer = GlobalTables.playersTable.getPlayer(actor.getUniqueId());
                    if (amount > 0 && maxCount > 0) {
                        adupPlayer.moveMultiplier = Math.max(amount / maxCount, 1);
                    }

                    Log log = new Log(stack.clone(), name, date, world, Log.Type.MOVED, type, itemAmount < 0);
                    if (amount >= maxCount * multiplier) {
                        adupPlayer.moveMultiplier = multiplier + amount / maxCount;

                        GlobalTables.playersTable.updatePlayer(actor.getUniqueId(), adupPlayer, "SET move_multiplier = ?", (statement -> {
                            statement.setInt(1, adupPlayer.moveMultiplier);
                            return 2;
                        }));

                        WarningsUtil.warn(actor.getUniqueId(), amount, log, "Moved");
                    }

                    GlobalTables.logsTable.addLog(actor.getUniqueId(), log);
                }

                modifications.clear();
            }
        }

        public Player getActor() {
            return actor;
        }

        public Location getLocation() {
            return location;
        }
    }
}