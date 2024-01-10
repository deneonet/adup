package net.deneo.adup.gui.suspects;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.gui.UMenuPagged;
import net.deneo.adup.gui.prompts.TimeSpanPrompt;
import net.deneo.adup.utility.*;
import org.bukkit.Bukkit;
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

import java.util.Date;

public class SuspectsMenu extends UMenuPagged<AdupPlayer> {
    @Position(start = StartPosition.BOTTOM_CENTER, value = -4)
    private final Button back;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
    private final Button nextLog;
    @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
    private final Button previousLog;
    @Position(start = StartPosition.BOTTOM_CENTER)
    private final Button clearLogs;
    @Position(start = StartPosition.BOTTOM_CENTER, value = 4)
    private final Button filter;

    public SuspectsMenu(Player player) {
        super(FilterHandler.loadSuspects(player.getUniqueId()));
        setTitle("&5&lADUP §8§l» Suspects");

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

        clearLogs = new ButtonMenu(new ClearMenu(this), ItemCreator.of(
                CompMaterial.BARRIER,
                "&5Clear Suspects"
        ).glow(true).make());

        filter = new ButtonMenu(new FilterMenu(this), ItemCreator.of(
                CompMaterial.LIME_DYE,
                "§5Set search filters"
        ).glow(true).make());
    }

    @Override
    protected ItemStack convertToItemStack(AdupPlayer player) {
        return ItemCreator.of(
                CompMaterial.WRITABLE_BOOK,
                "&7" + UUIDUtil.getName(player.uuid),
                "&7&lLeft click to §5Remove",
                "&7&lRight click to §5Spectate",
                "&5" + player.uuid
        ).glow(true).make();
    }

    @Override
    protected void onPageClick(Player player, AdupPlayer adupPlayer, ClickType clickType) {
        if (clickType.isLeftClick()) {
            adupPlayer.isSuspected = false;
            GlobalTables.playersTable.updateFlags(adupPlayer);
        } else if (clickType.isRightClick()) {
            Player target = Bukkit.getPlayer(adupPlayer.uuid);
            if (target == null) {
                MessagesUtil.sendPlayerNotOnline(player);
                return;
            }

            SpectateUtil.spectatePlayer(player, target);
        }

        update();
    }

    @Override
    public void update() {
        Player player = getViewer();
        new SuspectsMenu(player).displayTo(player);
    }

    private static class ClearMenu extends Menu {
        @Position(start = StartPosition.BOTTOM_CENTER, value = 2)
        private final Button timeSpan;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
        private final Button all;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
        private final Button back;

        ClearMenu(UMenuPagged<?> parent) {
            setTitle("&5&lADUP §8§l» Suspects");
            setSize(9);

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    parent.update();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.ARROW,
                            "&7Back"
                    ).make();
                }
            };

            timeSpan = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    TimeSpanPrompt prompt = new TimeSpanPrompt() {
                        @Override
                        public void onResult(String in) {
                            for (AdupPlayer player : GlobalTables.playersTable.cSelect()) {
                                Date currentDate = new Date();

                                String[] timeSpanSplit = in.split(":");
                                String timeSpanStart = timeSpanSplit[0];
                                String timeSpanEnd = timeSpanSplit[1];

                                int parsedTime = TimeUtil.parseTime(timeSpanStart);
                                Date start = new Date(player.suspectedDate.getTime() + TimeUtil.getTimeUnit(timeSpanStart).getTime(parsedTime));

                                parsedTime = TimeUtil.parseTime(timeSpanEnd);
                                Date end = new Date(player.suspectedDate.getTime() + TimeUtil.getTimeUnit(timeSpanEnd).getTime(parsedTime));

                                if (currentDate.after(start) && currentDate.before(end)) {
                                    player.isSuspected = false;
                                    GlobalTables.playersTable.updateFlags(player);
                                }
                            }

                            parent.update();
                        }
                    };
                    ConversationUtil.buildAndBegin(prompt, player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.BARRIER,
                            "&5&lTime span",
                            "",
                            "&7Delete all suspects in a specific time span"
                    ).glow(true).make();
                }
            };

            all = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    for (AdupPlayer adupPlayer : GlobalTables.playersTable.cSelect()) {
                        adupPlayer.isSuspected = false;
                        GlobalTables.playersTable.updateFlags(adupPlayer);
                    }
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(
                            CompMaterial.BARRIER,
                            "&5&lDelete All"
                    ).glow(true).make();
                }
            };
        }
    }
}
