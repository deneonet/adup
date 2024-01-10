package net.deneo.adup.gui.tracked;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class SettingsHandler {
    private static final HashMap<UUID, Setting> settings = new HashMap<>();

    public static void setName(UUID uuid, String name) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setName(name);
        settings.put(uuid, setting);
    }

    public static String getName(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting == null ? "" : setting.getName();
    }

    public static void setDisplayName(UUID uuid, boolean displayName) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setDisplayName(displayName);
        settings.put(uuid, setting);
    }

    public static boolean isDisplayName(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting != null && setting.isDisplayName();
    }

    public static void setEnchantments(UUID uuid, boolean enchantments) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setEnchantments(enchantments);
        settings.put(uuid, setting);
    }

    public static boolean isEnchantments(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting != null && setting.isEnchantments();
    }

    public static void setLore(UUID uuid, boolean lore) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setLore(lore);
        settings.put(uuid, setting);
    }

    public static boolean isLore(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting != null && setting.isLore();
    }

    public static void setPickUpThreshold(UUID uuid, int threshold) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setPickUpThreshold(threshold);
        settings.put(uuid, setting);
    }

    public static int getPickUpThreshold(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting == null ? 0 : setting.getPickUpThreshold();
    }

    public static void setDropThreshold(UUID uuid, int threshold) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setDropThreshold(threshold);
        settings.put(uuid, setting);
    }

    public static int getDropThreshold(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting == null ? 0 : setting.getDropThreshold();
    }

    public static void setMoveThreshold(UUID uuid, int threshold) {
        Setting setting = settings.get(uuid);
        if (setting == null) {
            setting = new Setting();
        }

        setting.setMoveThreshold(threshold);
        settings.put(uuid, setting);
    }

    public static int getMoveThreshold(UUID uuid) {
        Setting setting = settings.get(uuid);
        return setting == null ? 0 : setting.getMoveThreshold();
    }

    public static void remove(UUID uuid) {
        settings.remove(uuid);
    }

    private static class Setting {
        @Setter
        @Getter
        private String name = "";
        @Setter
        @Getter
        private boolean displayName;
        @Setter
        @Getter
        private boolean enchantments;
        @Setter
        @Getter
        private boolean lore;
        @Setter
        @Getter
        private int pickUpThreshold;
        @Setter
        @Getter
        private int dropThreshold;
        @Setter
        @Getter
        private int moveThreshold;
    }
}
