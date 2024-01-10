package net.deneo.adup.gui.bans;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.gui.UMenuPagged;
import net.deneo.adup.gui.prompts.TimeSpanPrompt;
import net.deneo.adup.utility.ConversationUtil;
import net.deneo.adup.utility.TimeUtil;
import net.deneo.adup.utility.UUIDUtil;
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

public class BansMenu extends UMenuPagged<AdupPlayer> {
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

    public BansMenu(Player player) {
        super(FilterHandler.loadBans(player.getUniqueId()));
        setTitle("&5&lADUP §8§l» Bans");

        back = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.ARROW, "&7Back"
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
                "&5Clear Bans"
        ).make());

        filter = new ButtonMenu(new FilterMenu(this), ItemCreator.of(
                CompMaterial.LIME_DYE,
                "§5Set search filters"
        ).make());
    }

    @Override
    protected ItemStack convertToItemStack(AdupPlayer player) {
        if (player.unbanDate == null) {
            return ItemCreator.of(
                    CompMaterial.WRITABLE_BOOK,
                    "&7" + UUIDUtil.getName(player.uuid),
                    "&7&lUnbanned &r&7(Reopen to remove)",
                    "&5" + player.uuid
            ).make();
        }

        return ItemCreator.of(
                CompMaterial.WRITABLE_BOOK,
                "&7" + UUIDUtil.getName(player.uuid),
                "&7&lBanned at §5" + TimeUtil.getDateAndTime(player.banDate),
                "&7&lBanned until §5" + TimeUtil.getDateAndTime(player.unbanDate),
                "&5" + player.uuid
        ).glow(true).make();
    }

    @Override
    protected void onPageClick(Player player, AdupPlayer adupPlayer, ClickType clickType) {
        GlobalTables.playersTable.resetBan(adupPlayer);
        update();
    }

    @Override
    public void update() {
        Player player = getViewer();
        new BansMenu(player).displayTo(player);
    }

    private static class ClearMenu extends Menu {
        @Position(start = StartPosition.BOTTOM_CENTER, value = 2)
        private final Button timeSpan;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
        private final Button all;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
        private final Button back;

        ClearMenu(UMenuPagged<?> parent) {
            setTitle("&5&lADUP §8§l» Bans");
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
                            for (AdupPlayer player : GlobalTables.playersTable.getBannedPlayers()) {
                                Date currentDate = new Date();

                                String[] timeSpanSplit = in.split(":");
                                String timeSpanStart = timeSpanSplit[0];
                                String timeSpanEnd = timeSpanSplit[1];

                                int parsedTime = TimeUtil.parseTime(timeSpanStart);
                                Date start = new Date(player.banDate.getTime() + TimeUtil.getTimeUnit(timeSpanStart).getTime(parsedTime));

                                parsedTime = TimeUtil.parseTime(timeSpanEnd);
                                Date end = new Date(player.banDate.getTime() + TimeUtil.getTimeUnit(timeSpanEnd).getTime(parsedTime));

                                if (currentDate.after(start) && currentDate.before(end)) {
                                    GlobalTables.playersTable.resetBan(player);
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
                            "&7Delete all bans in a specific time span"
                    ).glow(true).make();
                }
            };

            all = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    for (AdupPlayer adupPlayer : GlobalTables.playersTable.getBannedPlayers()) {
                        GlobalTables.playersTable.resetBan(adupPlayer);
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
