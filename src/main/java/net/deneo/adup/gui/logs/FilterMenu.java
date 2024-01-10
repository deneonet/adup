package net.deneo.adup.gui.logs;

import net.deneo.adup.gui.UMenuPagged;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class FilterMenu extends Menu {
    @Position(start = StartPosition.BOTTOM_CENTER)
    private final Button trackedItems;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
    private final Button untrackedItems;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 2)
    private final Button removedItems;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
    private final Button addedItems;
    @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
    private final Button back;

    FilterMenu(UMenuPagged<?> parent) {
        setTitle("&5&lADUP §8§l» Logs");
        setSize(9);

        back = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                parent.update();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.PLAYER_HEAD,
                        "&7Back"
                ).skullOwner("MHF_arrowleft").glow(true).make();
            }
        };

        trackedItems = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                boolean isOnlyTrackedItems = FilterHandler.getIsOnlyTrackedItems(player.getUniqueId());
                FilterHandler.setIsOnlyTrackedItems(player.getUniqueId(), !isOnlyTrackedItems);
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                boolean isOnlyTrackedItems = FilterHandler.getIsOnlyTrackedItems(getViewer().getUniqueId());
                return ItemCreator.of(
                        isOnlyTrackedItems ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "§5Show only tracked items"
                ).glow(true).make();
            }
        };
        untrackedItems = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                boolean isOnlyUntrackedItems = FilterHandler.getIsOnlyUntrackedItems(getViewer().getUniqueId());
                FilterHandler.setIsOnlyUntrackedItems(player.getUniqueId(), !isOnlyUntrackedItems);
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                boolean isOnlyUntrackedItems = FilterHandler.getIsOnlyUntrackedItems(getViewer().getUniqueId());
                return ItemCreator.of(
                        isOnlyUntrackedItems ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "§5Show only un-tracked items"
                ).glow(true).make();
            }
        };
        removedItems = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                boolean isOnlyRemovedItems = FilterHandler.getIsOnlyRemovedItems(getViewer().getUniqueId());
                FilterHandler.setIsOnlyRemovedItems(player.getUniqueId(), !isOnlyRemovedItems);
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                boolean isOnlyRemovedItems = FilterHandler.getIsOnlyRemovedItems(getViewer().getUniqueId());
                return ItemCreator.of(
                        isOnlyRemovedItems ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "§5Show only removed items (only moved logs)"
                ).glow(true).make();
            }
        };
        addedItems = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                boolean isOnlyAddedItems = FilterHandler.getIsOnlyAddedItems(getViewer().getUniqueId());
                FilterHandler.setIsOnlyAddedItems(player.getUniqueId(), !isOnlyAddedItems);
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                boolean isOnlyAddedItems = FilterHandler.getIsOnlyAddedItems(getViewer().getUniqueId());
                return ItemCreator.of(
                        isOnlyAddedItems ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "§5Show only added items (only moved logs)"
                ).glow(true).make();
            }
        };
    }
}