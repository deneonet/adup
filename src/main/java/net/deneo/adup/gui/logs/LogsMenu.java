package net.deneo.adup.gui.logs;

import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.gui.UMenuPagged;
import net.deneo.adup.gui.prompts.TimeSpanPrompt;
import net.deneo.adup.utility.ConversationUtil;
import net.deneo.adup.utility.LogsUtil;
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LogsMenu extends Menu {
    @Position(1)
    private final Button openDropped;
    @Position(3)
    private final Button openSelfDropped;
    @Position(4)
    private final Button openPickedUp;
    @Position(5)
    private final Button openUnknown;
    @Position(7)
    private final Button openMoved;

    public LogsMenu() {
        setTitle("&5&lADUP §8§l» Logs");
        setSize(9);

        openDropped = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new DroppedLogsMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.KNOWLEDGE_BOOK,
                        "&7&lDropped",
                        "",
                        "&5Click to open the dropped logs"
                ).glow(true).make();
            }
        };

        openSelfDropped = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new PickedUpLogsMenu(player, Log.Type.PICKED_UP_SELF_DROPPED).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.KNOWLEDGE_BOOK,
                        "&7&lPicked Up",
                        "&5&lSelf-Dropped",
                        "&5Click to open the self-dropped logs"
                ).glow(true).make();
            }
        };

        openPickedUp = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new PickedUpLogsMenu(player, Log.Type.PICKED_UP_PLAYER).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.KNOWLEDGE_BOOK,
                        "&7&lPicked Up",
                        "&5&lFrom Another Player",
                        "&5Click to open the picked up logs"
                ).glow(true).make();
            }
        };

        openUnknown = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new PickedUpLogsMenu(player, Log.Type.PICKED_UP_UNKNOWN).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.KNOWLEDGE_BOOK,
                        "&7&lPicked Up",
                        "&5&lFrom Unknown Source",
                        "&5Click to open the picked up from unknown items"
                ).glow(true).make();
            }
        };

        openMoved = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new MovedLogsMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(
                        CompMaterial.KNOWLEDGE_BOOK,
                        "&7&lMoved",
                        "",
                        "&5Click to open the moved items"
                ).glow(true).make();
            }
        };
    }

    private static class DroppedLogsMenu extends UMenuPagged<Log> {
        @Position(start = StartPosition.BOTTOM_CENTER, value = -4)
        private final Button back;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
        private final Button nextLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
        private final Button previousLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 4)
        private final Button filter;
        @Position(start = StartPosition.BOTTOM_CENTER)
        private final Button clearLogs;

        DroppedLogsMenu(Player player) {
            super(FilterHandler.loadItems(player.getUniqueId(), Log.Type.DROPPED));
            setTitle("&5&lADUP §8§l» Logs");

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new LogsMenu().displayTo(player);
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
                    "&5Clear Logs"
            ).glow(true).make());

            filter = new ButtonMenu(new FilterMenu(this), ItemCreator.of(
                    CompMaterial.LIME_DYE,
                    "§5Set search filters"
            ).glow(true).make());
        }

        @Override
        protected ItemStack convertToItemStack(Log log) {
            String item = LogsUtil.getFormat(log.trackedName, log.itemStack.getType(), log.itemStack.getAmount());

            return ItemCreator.of(
                    CompMaterial.WRITABLE_BOOK,
                    item,
                    "&7&lAt &5" + TimeUtil.getDateAndTime(log.date),
                    "&7&lIn &5" + log.world
            ).glow(true).make();
        }

        @Override
        protected void onPageClick(Player player, Log log, ClickType clickType) {
            GlobalTables.logsTable.deleteLog(player.getUniqueId(), log.date);
        }

        @Override
        public void update() {
            Player player = getViewer();
            new DroppedLogsMenu(player).displayTo(player);
        }
    }

    private static class PickedUpLogsMenu extends UMenuPagged<Log> {
        @Position(start = StartPosition.BOTTOM_CENTER, value = -4)
        private final Button back;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
        private final Button nextLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
        private final Button previousLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 4)
        private final Button filter;
        @Position(start = StartPosition.BOTTOM_CENTER)
        private final Button clearLogs;

        private final Log.Type type;

        PickedUpLogsMenu(Player player, Log.Type type) {
            super(FilterHandler.loadItems(player.getUniqueId(), type));
            this.type = type;
            setTitle("&5&lADUP §8§l» Logs");

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new LogsMenu().displayTo(player);
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
                    "&5Clear Logs"
            ).glow(true).make());

            filter = new ButtonMenu(new FilterMenu(this), ItemCreator.of(
                    CompMaterial.LIME_DYE,
                    "§5Set search filters"
            ).glow(true).make());
        }

        @Override
        protected ItemStack convertToItemStack(Log log) {
            String item = LogsUtil.getFormat(log.trackedName, log.itemStack.getType(), log.itemStack.getAmount());

            return type == Log.Type.PICKED_UP_PLAYER ? ItemCreator.of(
                    CompMaterial.WRITABLE_BOOK, item,
                    "&7&lAt &5" + TimeUtil.getDateAndTime(log.date),
                    "&7&lIn &5" + log.world,
                    "&7&lFrom &5" + UUIDUtil.getName(log.source),
                    "&7" + log.source).glow(true).make() :
                    ItemCreator.of(
                            CompMaterial.WRITABLE_BOOK,
                            item,
                            "&7&lAt &5" + TimeUtil.getDateAndTime(log.date),
                            "&7&lIn &5" + log.world
                    ).glow(true).make();
        }

        @Override
        protected void onPageClick(Player player, Log log, ClickType clickType) {
            GlobalTables.logsTable.deleteLog(player.getUniqueId(), log.date);
            update();
        }

        @Override
        public void update() {
            Player player = getViewer();
            new PickedUpLogsMenu(player, type).displayTo(player);
        }
    }

    private static class MovedLogsMenu extends UMenuPagged<Log> {
        @Position(start = StartPosition.BOTTOM_CENTER, value = -4)
        private final Button back;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 1)
        private final Button nextLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -1)
        private final Button previousLog;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 4)
        private final Button filter;
        @Position(start = StartPosition.BOTTOM_CENTER)
        private final Button clearLogs;

        MovedLogsMenu(Player player) {
            super(FilterHandler.loadItems(player.getUniqueId(), Log.Type.MOVED));
            setTitle("&5&lADUP §8§l» Logs");

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new LogsMenu().displayTo(player);
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
                    "&5Clear Logs"
            ).glow(true).make());

            filter = new ButtonMenu(new FilterMenu(this), ItemCreator.of(
                    CompMaterial.LIME_DYE,
                    "§5Set search filters"
            ).glow(true).make());
        }

        @Override
        protected ItemStack convertToItemStack(Log log) {
            String item = LogsUtil.getFormat(log.trackedName, log.itemStack.getType(), log.itemStack.getAmount());
            return ItemCreator.of(
                    CompMaterial.WRITABLE_BOOK,
                    item,
                    "&7&lAt &5" + TimeUtil.getDateAndTime(log.date),
                    "&7&lIn &5" + log.world,
                    (log.taken ? "&7&lOut of &5" : "&7&lInto &5") + log.storage.getFancyName()
            ).glow(true).make();
        }

        @Override
        protected void onPageClick(Player player, Log log, ClickType clickType) {
            GlobalTables.logsTable.deleteLog(player.getUniqueId(), log.date);
            update();
        }

        @Override
        public void update() {
            Player player = getViewer();
            new MovedLogsMenu(player).displayTo(player);
        }
    }

    private static class ClearMenu extends Menu {
        @Position(start = StartPosition.BOTTOM_CENTER, value = 2)
        private final Button timeSpan;
        @Position(start = StartPosition.BOTTOM_CENTER, value = 3)
        private final Button all;
        @Position(start = StartPosition.BOTTOM_CENTER, value = -3)
        private final Button back;

        ClearMenu(UMenuPagged<?> parent) {
            setTitle("&5&lADUP §8§l» Logs");
            setSize(9);

            back = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    parent.update();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.ARROW, "&7Back").make();
                }
            };

            timeSpan = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    TimeSpanPrompt prompt = new TimeSpanPrompt() {
                        @Override
                        public void onResult(String in) {
                            UUID key = LogsUtil.getTarget(player.getUniqueId());
                            List<Log> logs = GlobalTables.logsTable.getLogsNoCache(key);

                            for (Log log : GlobalTables.logsTable.getLogs(key)) {
                                if (!logs.contains(log)) {
                                    logs.add(log);
                                }
                            }

                            for (Log log : logs) {
                                Date currentDate = new Date();

                                String[] timeSpanSplit = in.split(":");
                                String timeSpanStart = timeSpanSplit[0];
                                String timeSpanEnd = timeSpanSplit[1];

                                int parsedTime = TimeUtil.parseTime(timeSpanStart);
                                Date start = new Date(log.date.getTime() + TimeUtil.getTimeUnit(timeSpanStart).getTime(parsedTime));

                                parsedTime = TimeUtil.parseTime(timeSpanEnd);
                                Date end = new Date(log.date.getTime() + TimeUtil.getTimeUnit(timeSpanEnd).getTime(parsedTime));

                                if (currentDate.after(start) && currentDate.before(end)) {
                                    GlobalTables.logsTable.deleteLog(key, log.date);
                                    GlobalTables.logsTable.ncDelete(key.toString(), "AND date_time = ?", (stmt) -> stmt.setTimestamp(2, new Timestamp(log.date.getTime())));
                                }
                            }

                            parent.update();
                        }
                    };
                    ConversationUtil.buildAndBegin(prompt, player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.BARRIER, "&5&lTime span", "&7Delete all logs in a specific time span").glow(true).make();
                }
            };

            all = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    GlobalTables.logsTable.deleteLogs(LogsUtil.getTarget(player.getUniqueId()));
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.BARRIER, "&5&lDelete All").glow(true).make();
                }
            };
        }
    }
}