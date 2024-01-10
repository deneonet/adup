package net.deneo.adup.gui.mce;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.gui.prompts.AddWhitelistPrompt;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.ConversationUtil;
import net.deneo.adup.utility.MaintenanceUtil;
import net.deneo.adup.utility.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.StartPosition;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceMenu extends MenuPagged<AdupPlayer> {
    @Position(start = StartPosition.BOTTOM_CENTER, value = -4)
    private final Button back;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
    private final Button nextLog;
    @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
    private final Button previousLog;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 4)
    private final Button toggle;
    @Position(start = StartPosition.BOTTOM_CENTER)
    private final Button add;

    public MaintenanceMenu() {
        super(loadWhitelist());
        setTitle("&5&lADUP §8§l» Maintenance");

        back = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.ARROW,
                        "&7Back"
                ).make();
            }
        };

        nextLog = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setCurrentPage(
                        Math.min(getCurrentPage() + 1, getPages().size())
                );
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.PLAYER_HEAD,
                        "&7Next Page"
                ).skullOwner("MHF_arrowright").glow(true).make();
            }
        };

        previousLog = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setCurrentPage(
                        Math.max(getCurrentPage() - 1, 1)
                );
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.PLAYER_HEAD,
                        "&7Previous Page"
                ).skullOwner("MHF_arrowleft").glow(true).make();
            }
        };

        toggle = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                MaintenanceUtil.toggleWhitelist();
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                boolean maintenance = ConfigUtil.getBoolean("maintenance.enabled");
                return ItemCreator.of(
                        maintenance ? CompMaterial.LIME_DYE : CompMaterial.GRAY_DYE,
                        "&5&lMaintenance"
                ).glow(true).make();
            }
        };

        add = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                AddWhitelistPrompt addWhitelistPrompt = new AddWhitelistPrompt() {
                    @Override
                    public void onResult(String name) {
                        /*
                          todo: find a better solution here

                          Issue(s):
                            1. getOfflinePlayer is deprecated and slow
                          Solution(s):
                            1. storing the name + uuid -
                                checking then on every login if the name matches the stored uuid
                        */

                        OfflinePlayer target = Bukkit.getOfflinePlayer(name);

                        AdupPlayer adupPlayer = GlobalTables.playersTable.getPlayer(target.getUniqueId());
                        adupPlayer.isWhitelisted = true;
                        GlobalTables.playersTable.updateFlags(adupPlayer);

                        new MaintenanceMenu().displayTo(player);
                    }
                };

                ConversationUtil.buildAndBegin(addWhitelistPrompt, player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.BEACON,
                        "&5&lAdd Player"
                ).glow(true).make();
            }
        };
    }

    private static List<AdupPlayer> loadWhitelist() {
        List<AdupPlayer> whitelist = GlobalTables.playersTable.cSelect()
                .stream()
                .filter(player -> player.isWhitelisted)
                .collect(Collectors.toList());
        Collections.reverse(whitelist);

        return whitelist;
    }

    @Override
    protected ItemStack convertToItemStack(AdupPlayer player) {
        return ItemCreator.of(
                CompMaterial.WRITABLE_BOOK,
                "&7" + UUIDUtil.getName(player.uuid),
                "&7&lWhitelisted"
        ).glow(true).make();
    }

    @Override
    protected void onPageClick(Player player, AdupPlayer adupPlayer, ClickType clickType) {
        adupPlayer.isWhitelisted = false;
        GlobalTables.playersTable.updateFlags(adupPlayer);

        new MaintenanceMenu().displayTo(player);
    }
}
