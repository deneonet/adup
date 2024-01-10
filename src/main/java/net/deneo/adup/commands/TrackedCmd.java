package net.deneo.adup.commands;

import net.deneo.adup.gui.tracked.TrackedMenu;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class TrackedCmd {
    public static void cmd(ConsoleCommandSender console, String[] args) {
        if (args.length == 1) {
            MessagesUtil.sendInvalidCommand(null, console);
            return;
        }

        if ("remove".equals(args[1])) {
            if (args.length != 3) {
                MessagesUtil.sendInvalidCommand(null, console);
                return;
            }

            List<Map<String, Object>> items = ConfigUtil.getMapList("tracking.items");

            boolean exists = false;
            for (Map<String, Object> item : items) {
                if (item.get("name").equals(args[2])) {
                    exists = true;
                }
            }

            if (!exists) {
                MessagesUtil.sendTrackedItemDoesNotExists(null, console);
                return;
            }

            items.removeIf((item) -> item.get("name").equals(args[2]));
            ConfigUtil.setValue("tracking.items", items);

            MessagesUtil.sendTrackedItemRemoved(null, console, args[2]);

            return;
        }

        MessagesUtil.sendInvalidCommand(null, console);
    }

    public static void cmd(Player player, String[] args) {
        if (PermissionsUtil.hasTrackedCmdPermission(player)) {
            if (args.length == 1) {
                MessagesUtil.sendInvalidCommand(player, null);
                return;
            }

            switch (args[1]) {
                case "add":
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        MessagesUtil.sendNoHoldingItem(player);
                        return;
                    }

                    new TrackedMenu().displayTo(player);
                    break;
                case "remove":
                    if (args.length != 3) {
                        MessagesUtil.sendInvalidCommand(player, null);
                        return;
                    }

                    List<Map<String, Object>> items = ConfigUtil.getMapList("tracking.items");

                    boolean exists = false;
                    for (Map<String, Object> item : items) {
                        if (item.get("name").equals(args[2])) {
                            exists = true;
                        }
                    }

                    if (!exists) {
                        MessagesUtil.sendTrackedItemDoesNotExists(player, null);
                        return;
                    }

                    items.removeIf((item) -> item.get("name").equals(args[2]));
                    ConfigUtil.setValue("tracking.items", items);

                    MessagesUtil.sendTrackedItemRemoved(player, null, args[2]);

                    break;
                default:
                    MessagesUtil.sendInvalidCommand(player, null);
            }

            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup tracked");
    }
}
