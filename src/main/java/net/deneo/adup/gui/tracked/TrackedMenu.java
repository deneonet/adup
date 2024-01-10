package net.deneo.adup.gui.tracked;

import net.deneo.adup.gui.prompts.NumberPrompt;
import net.deneo.adup.gui.prompts.SetNamePrompt;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.ConversationUtil;
import net.deneo.adup.utility.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackedMenu extends Menu {
    @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
    private final Button back;
    @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
    private final Button thresholds;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
    private final Button metadata;
    @Position(start = StartPosition.BOTTOM_CENTER)
    private final Button name;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
    private final Button done;

    public TrackedMenu() {
        setTitle("&5&lADUP §8§l» Add Tracked Item");
        setSize(9);

        back = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.ARROW,
                        "&7&lBack"
                ).make();
            }
        };

        thresholds = new ButtonMenu(new ThresholdsMenu(), ItemCreator.of(
                CompMaterial.BARRIER,
                "&7&lThresholds",
                "",
                "&5Set the Thresholds"
        ).glow(true).make());

        metadata = new ButtonMenu(new MetadataMenu(), ItemCreator.of(
                CompMaterial.REDSTONE_TORCH,
                "&7&lMetadata",
                "",
                "&5Set the Metadata to Compare"
        ).glow(true).make());

        name = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                SetNamePrompt setNamePrompt = new SetNamePrompt() {
                    @Override
                    public void onResult(String name) {
                        SettingsHandler.setName(player.getUniqueId(), name);
                        new TrackedMenu().displayTo(player);
                    }
                };
                ConversationUtil.buildAndBegin(setNamePrompt, player);
            }

            @Override
            public ItemStack getItem() {
                String name = SettingsHandler.getName(getViewer().getUniqueId());
                return ItemCreator.of(
                        CompMaterial.ANVIL,
                        "&7&lName",
                        "&5&l" + name,
                        "&5Set the Name"
                ).glow(true).make();
            }
        };

        done = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                String name = SettingsHandler.getName(player.getUniqueId());
                if (name.isEmpty()) {
                    return;
                }

                List<Map<String, Object>> items = ConfigUtil.getMapList("tracking.items");

                boolean exists = false;
                for (Map<String, Object> item : items) {
                    if (item.get("name").equals(name)) {
                        exists = true;
                    }
                }

                if (exists) {
                    MessagesUtil.sendTrackedItemAlreadyExists(player);
                    return;
                }

                Map<String, Object> item = new HashMap<>();

                item.put("name", name);
                item.put("enchantments", SettingsHandler.isEnchantments(player.getUniqueId()));
                item.put("display_name", SettingsHandler.isDisplayName(player.getUniqueId()));
                item.put("lore", SettingsHandler.isLore(player.getUniqueId()));
                item.put("pick_up_threshold", SettingsHandler.getPickUpThreshold(player.getUniqueId()));
                item.put("drop_threshold", SettingsHandler.getDropThreshold(player.getUniqueId()));
                item.put("move_threshold", SettingsHandler.getMoveThreshold(player.getUniqueId()));
                item.put("type", player.getInventory().getItemInMainHand().getType().toString());
                item.put("metadata", player.getInventory().getItemInMainHand().getItemMeta());
                items.add(item);

                ConfigUtil.setValue("tracking.items", items);

                MessagesUtil.sendTrackedItemAdded(player, name);
                SettingsHandler.remove(player.getUniqueId());

                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.GREEN_CONCRETE,
                        "&7&lDone"
                ).glow(true).make();
            }
        };
    }

    public static class MetadataMenu extends Menu {
        @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
        private final Button back;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
        private final Button lore;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
        private final Button enchantments;
        @Position(start = StartPosition.BOTTOM_CENTER)
        private final Button displayName;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
        private final Button reset;

        public MetadataMenu() {
            setTitle("&5&lADUP §8§l» Add Tracked Item");
            setSize(9);

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    new TrackedMenu().displayTo(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.ARROW,
                            "&7&lBack"
                    ).make();
                }
            };

            lore = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    boolean lore = SettingsHandler.isLore(player.getUniqueId());
                    SettingsHandler.setLore(player.getUniqueId(), !lore);
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.MINECART,
                            "&7&lLore",
                            "",
                            "&5Set if the lore should be compared"
                    ).glow(
                            SettingsHandler.isLore(getViewer().getUniqueId())
                    ).make();
                }
            };

            enchantments = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    boolean enchantments = SettingsHandler.isEnchantments(player.getUniqueId());
                    SettingsHandler.setEnchantments(player.getUniqueId(), !enchantments);
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.ENCHANTED_BOOK,
                            "&7&lEnchantments",
                            "",
                            "&5Set if the enchantments should be compared"
                    ).glow(
                            SettingsHandler.isEnchantments(getViewer().getUniqueId())
                    ).make();
                }
            };

            displayName = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    boolean displayName = SettingsHandler.isDisplayName(player.getUniqueId());
                    SettingsHandler.setDisplayName(player.getUniqueId(), !displayName);
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.NAME_TAG,
                            "&7&lDisplay Name",
                            "",
                            "&5Set if the display name should be compared"
                    ).glow(
                            SettingsHandler.isDisplayName(getViewer().getUniqueId())
                    ).make();
                }
            };

            reset = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    SettingsHandler.setEnchantments(player.getUniqueId(), false);
                    SettingsHandler.setDisplayName(player.getUniqueId(), false);
                    SettingsHandler.setLore(player.getUniqueId(), false);
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.RED_CONCRETE,
                            "&7&lReset",
                            "",
                            "&5Reset everything to default"
                    ).glow(true).make();
                }
            };
        }
    }

    public static class ThresholdsMenu extends Menu {
        @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
        private final Button back;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
        private final Button pickUpThreshold;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
        private final Button dropThreshold;
        @Position(start = StartPosition.BOTTOM_CENTER)
        private final Button moveThreshold;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
        private final Button reset;

        public ThresholdsMenu() {
            setTitle("&5&lADUP §8§l» Add Tracked Item");
            setSize(9);

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    new TrackedMenu().displayTo(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.ARROW,
                            "&7&lBack"
                    ).make();
                }
            };

            pickUpThreshold = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    NumberPrompt prompt = new NumberPrompt("Enter the pick up threshold of the tracked item") {
                        @Override
                        public void onResult(Integer number) {
                            SettingsHandler.setPickUpThreshold(player.getUniqueId(), number);
                            new ThresholdsMenu().displayTo(player);
                        }
                    };
                    ConversationUtil.buildAndBegin(prompt, player);
                }

                @Override
                public ItemStack getItem() {
                    int threshold = SettingsHandler.getPickUpThreshold(getViewer().getUniqueId());

                    return ItemCreator.of(
                            CompMaterial.ANVIL,
                            "&7&lPickUp Threshold",
                            "&5&l" + threshold
                    ).glow(threshold != 0).make();
                }
            };

            dropThreshold = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    NumberPrompt prompt = new NumberPrompt("Enter the drop threshold of the tracked item") {
                        @Override
                        public void onResult(Integer number) {
                            SettingsHandler.setDropThreshold(player.getUniqueId(), number);
                            new ThresholdsMenu().displayTo(player);
                        }
                    };
                    ConversationUtil.buildAndBegin(prompt, player);
                }

                @Override
                public ItemStack getItem() {
                    int threshold = SettingsHandler.getDropThreshold(getViewer().getUniqueId());

                    return ItemCreator.of(
                            CompMaterial.ANVIL,
                            "&7&lDrop Threshold",
                            "&5&l" + threshold
                    ).glow(threshold != 0).make();
                }
            };

            moveThreshold = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                    NumberPrompt prompt = new NumberPrompt("Enter the move threshold of the tracked item") {
                        @Override
                        public void onResult(Integer number) {
                            SettingsHandler.setMoveThreshold(player.getUniqueId(), number);
                            new ThresholdsMenu().displayTo(player);
                        }
                    };
                    ConversationUtil.buildAndBegin(prompt, player);
                }

                @Override
                public ItemStack getItem() {
                    int threshold = SettingsHandler.getMoveThreshold(getViewer().getUniqueId());

                    return ItemCreator.of(
                            CompMaterial.ANVIL,
                            "&7&lMove Threshold",
                            "&5&l" + threshold
                    ).glow(threshold != 0).make();
                }
            };

            reset = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    SettingsHandler.setDropThreshold(player.getUniqueId(), 0);
                    SettingsHandler.setPickUpThreshold(player.getUniqueId(), 0);
                    SettingsHandler.setMoveThreshold(player.getUniqueId(), 0);
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.RED_CONCRETE,
                            "&7&lReset",
                            "",
                            "&5Reset everything to default"
                    ).glow(true).make();
                }
            };
        }
    }
}
