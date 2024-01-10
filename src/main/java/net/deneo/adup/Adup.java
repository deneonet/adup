package net.deneo.adup;

import net.deneo.adup.commands.AdupCmd;
import net.deneo.adup.database.Database;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.listener.player.PlayerDropEvent;
import net.deneo.adup.listener.player.PlayerMoveEvent;
import net.deneo.adup.listener.player.PlayerPickUpEvent;
import net.deneo.adup.listener.server.PlayerJoinEvent;
import net.deneo.adup.listener.server.PlayerLoginEvent;
import net.deneo.adup.listener.server.PlayerQuitEvent;
import net.deneo.adup.listener.world.EntityCombustEvent;
import net.deneo.adup.listener.world.ItemDespawnEvent;
import net.deneo.adup.listener.world.ItemMergeEvent;
import net.deneo.adup.utility.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.logging.Logger;

/*
/adup reload                         DONE
/adup mce -> Menu                    DONE
/adup logs <player> -> Menu          DONE
/adup bans -> Menu                   DONE
/adup reload                         DONE
/adup suspects -> Menu               DONE
/adup unban                          DONE

Check if everything in the menu works and is properly saved in the database - DONE
+ test every container - DONE
+ test every pick up type - DONE
+ test every clear in the menus - DONE
+ test every time span clear/filter in the menus - DONE

TODO:
MySQL support - DONE
Unban Command - DONE
Messages and Permissions check - DONE
Spectating - DONE
Move - DONE
Auto Save Cached Data - DONE (test)
Fix main class - DONE
Tracked Item Menu
Time span clear in logs/bans/suspects - DONE
 */

public final class Adup extends SimplePlugin implements Listener {
    public static Logger logger = Bukkit.getLogger();
    public static String prefix = "§5§lADUP §8§l» §7";

    public static void error(Exception ex) {
        logger.severe(convert(prefix) + ex);
    }

    public static void error(String s) {
        logger.severe(convert(prefix + s));
    }

    public static void error(String s, Exception ex) {
        logger.severe(convert(prefix + s));
        logger.severe(convert(prefix) + ex);
    }

    public static void fatal(String s) {
        logger.severe(convert(prefix + s));
        Bukkit.getPluginManager().disablePlugin(Adup.getInstance());
    }

    public static void fatal(String s, Exception ex) {
        logger.severe(convert(prefix + s));
        logger.severe(convert(prefix) + ex);
        Bukkit.getPluginManager().disablePlugin(Adup.getInstance());
    }

    public static void log(String s) {
        logger.info(convert(prefix + s));
    }

    private static String convert(String s) {
        return s.replace("§5", "\u001b[38;5;135m")
                .replace("§7", "\u001b[38;5;246m")
                .replace("§8", "\u001b[38;5;238m")
                .replace("§l", "\u001b[1m")
                + "\u001b[0m";
    }

    @Override
    protected void onPluginStop() {
        GlobalTables.logsTable.save();
        GlobalTables.playersTable.save();
        GlobalTables.itemsTable.save();
        Database.close();
    }

    public void onPluginStart() {
        ConfigUtil.init(this);
        saveDefaultConfig();

        if (!Database.init()) {
            return;
        }

        GlobalTables.start();

        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new PlayerDropEvent(), this);
        manager.registerEvents(new PlayerPickUpEvent(), this);
        manager.registerEvents(new PlayerMoveEvent(), this);

        manager.registerEvents(new EntityCombustEvent(), this);
        manager.registerEvents(new ItemDespawnEvent(), this);
        manager.registerEvents(new ItemMergeEvent(), this);

        manager.registerEvents(new PlayerLoginEvent(), this);
        manager.registerEvents(new PlayerJoinEvent(), this);
        manager.registerEvents(new PlayerQuitEvent(), this);

        new AdupCmd(this).register();
    }
}
