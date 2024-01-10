package net.deneo.adup.utility;

import org.bukkit.entity.Player;

public class PermissionsUtil {
    public static boolean hasTrackBypassPerm(Player player) {
        String permission = ConfigUtil.getString("permissions.track_bypass");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(adminPerm) || player.hasPermission(permission);
    }

    public static boolean hasBannedBypassPerm(Player player) {
        String permission = ConfigUtil.getString("permissions.banned_bypass");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(adminPerm) || player.hasPermission(permission);
    }

    public static boolean hasMaintenanceBypassPerm(Player player) {
        String permission = ConfigUtil.getString("permissions.maintenance_bypass");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(adminPerm) || player.hasPermission(permission);
    }

    public static boolean hasMaintenanceCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.maintenance");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasLogsCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.logs");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasUnbanCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.unban");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasBansCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.bans");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasReloadCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.reload");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasSuspectsCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.suspects");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasSpectateCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.spectate");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }

    public static boolean hasTrackedCmdPermission(Player player) {
        String permission = ConfigUtil.getString("permissions.tracked");
        String adminPerm = ConfigUtil.getString("permissions.admin");
        return player.hasPermission(permission) || player.hasPermission(adminPerm);
    }
}
