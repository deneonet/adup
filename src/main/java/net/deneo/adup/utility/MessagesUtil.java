package net.deneo.adup.utility;

import net.deneo.adup.Adup;
import net.deneo.adup.data.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

public class MessagesUtil {
    public static void sendInsufficientPermissions(Player player, String command) {
        player.sendMessage(ConfigUtil.getString("messages.insufficient_permissions").replace("{command}", command));
    }

    public static void sendTrackedItemAlreadyExists(Player player) {
        player.sendMessage(ConfigUtil.getString("messages.tracked_item_already_exists"));
    }

    public static void sendTrackedItemDoesNotExists(Player player, ConsoleCommandSender console) {
        if (console != null) {
            console.sendMessage(ConfigUtil.getString("messages.tracked_item_does_not_exists"));
            return;
        }

        player.sendMessage(ConfigUtil.getString("messages.tracked_item_does_not_exists"));
    }

    public static void sendMissingTargetArgument(Player player, String command) {
        player.sendMessage(ConfigUtil.getString("messages.missing_target").replace("{command}", command));
    }

    public static void sendNoHoldingItem(Player player) {
        player.sendMessage(ConfigUtil.getString("messages.no_holding_item"));
    }

    public static void sendInvalidCommand(Player player, ConsoleCommandSender console) {
        if (console == null) {
            player.sendMessage(ConfigUtil.getString("messages.invalid_command_player"));
            return;
        }

        console.sendMessage(ConfigUtil.getString("messages.invalid_command_console"));
    }

    public static void sendPlayerNotOnline(Player player) {
        player.sendMessage(ConfigUtil.getString("messages.player_not_online"));
    }

    public static void sendUnbanComplete(Player player, String target) {
        player.sendMessage(ConfigUtil.getString("messages.unbanned").replace("{target}", target));
    }

    public static void sendUnbanComplete(ConsoleCommandSender console, String target) {
        console.sendMessage(ConfigUtil.getString("messages.unbanned").replace("{target}", target));
    }

    public static void sendTrackedItemAdded(Player player, String name) {
        player.sendMessage(ConfigUtil.getString("messages.tracked_item_added").replace("{name}", name));
    }

    public static void sendTrackedItemRemoved(Player player, ConsoleCommandSender console, String name) {
        if (console != null) {
            console.sendMessage(ConfigUtil.getString("messages.tracked_item_removed").replace("{name}", name));
            return;
        }

        player.sendMessage(ConfigUtil.getString("messages.tracked_item_removed").replace("{name}", name));
    }

    public static void sendSpectating(Player player, String target) {
        player.sendMessage(ConfigUtil.getString("messages.spectating_message").replace("{target}", target));
    }

    public static void sendUnSpectating(Player player, String target) {
        player.sendMessage(ConfigUtil.getString("messages.un_spectating_message").replace("{target}", target));
    }

    public static void sendAlertMessage(UUID uuid, int amount, int warnings, Log log, String type, boolean banned) {
        boolean discordAlerts = ConfigUtil.getBoolean("discord.send_alerts");

        String item = ConfigUtil.getString(log.trackedName.isEmpty() ? "tracking.item_format_noname" : "tracking.item_format")
                .replace("{tracked_name}", log.trackedName)
                .replace("{item_type}", "" + log.itemStack.getType())
                .replace("{item_amount}", "" + amount);

        String alertMessage = ConfigUtil.getString(banned ? "tracking.banned_alert" : "tracking.warned_alert")
                .replace("{player}", UUIDUtil.getName(uuid))
                .replace("{item}", item)
                .replace("{world}", log.world)
                .replace("{warnings}", "" + warnings);
        alertMessage = alertMessage.replace("{type}", ConfigUtil.getString("tracking." + log.type));

        if (discordAlerts) {
            DiscordUtil dw = new DiscordUtil(ConfigUtil.getString("discord.webhook_url"));
            dw.setUsername(ConfigUtil.getString("discord.bot_username"));
            dw.setAvatarUrl(ConfigUtil.getString("discord.bot_avatar"));
            DiscordUtil.EmbedObject embed = new DiscordUtil.EmbedObject();
            embed.addField("", alertMessage, false);
            String player = UUIDUtil.getName(uuid) + ", UUID: " + uuid;
            embed.addField("Player", player, true);
            embed.addField("Item", item, false);
            embed.addField("World", log.world, false);
            embed.addField("Warnings", "" + warnings, false);
            embed.addField("Type", type, true);

            if (log.type == Log.Type.MOVED) {
                embed.addField("Storage Block", log.storage.getFancyName(), true);
            } else if (log.type == Log.Type.PICKED_UP_PLAYER) {
                String fromNameAndUUID = "Name: " + UUIDUtil.getName(log.source) + ", UUID: " + log.source;
                embed.addField("From player", fromNameAndUUID, false);
            }

            embed.setFooter("ADUP / Dupe Detection", "https://i.imgur.com/xRAc4TL.png");
            embed.setColor(Color.BLACK);
            dw.addEmbed(embed);
            dw.execute();
        }

        boolean chatAlerts = ConfigUtil.getBoolean("tracking.send_chat_alerts");
        if (chatAlerts) {
            String permission = ConfigUtil.getString("permissions.receive_alerts");
            Adup.log(alertMessage);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(permission)) {
                    player.sendMessage(alertMessage);
                }
            }
        }
    }
}
