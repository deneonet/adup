package net.deneo.adup.commands;

import net.deneo.adup.Adup;
import net.deneo.adup.utility.MessagesUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;

public class AdupCmd extends SimpleCommand {
    Adup plugin;

    public AdupCmd(Adup plugin) {
        super("adup");
        this.plugin = plugin;
    }

    @Override
    protected void onCommand() {
        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender console = (ConsoleCommandSender) sender;

            if (args.length > 0) {
                switch (args[0]) {
                    case "reload":
                        ReloadCmd.cmd(console, this.plugin);
                        break;
                    case "tracked":
                        TrackedCmd.cmd(console, args);
                        break;
                    case "mce":
                        MaintenanceCmd.cmd();
                        break;
                    case "unban":
                        UnbanCmd.cmd(console, args);
                        break;
                    default:
                        MessagesUtil.sendInvalidCommand(null, console);
                }

                return;
            }

            MessagesUtil.sendInvalidCommand(null, console);
            return;
        }

        Player player = (Player) sender;
        if (args.length > 0) {
            switch (args[0]) {
                case "reload":
                    ReloadCmd.cmd(player, this.plugin);
                    break;
                case "logs":
                    LogsCmd.cmd(player, args);
                    break;
                case "mce":
                    MaintenanceCmd.cmd(player);
                    break;
                case "unban":
                    UnbanCmd.cmd(player, args);
                    break;
                case "suspects":
                    SuspectsCmd.cmd(player);
                    break;
                case "spectate":
                    SpectateCmd.cmd(player, args);
                    break;
                case "bans":
                    BansCmd.cmd(player);
                    break;
                case "tracked":
                    TrackedCmd.cmd(player, args);
                    break;
                default:
                    MessagesUtil.sendInvalidCommand(player, null);
            }

            return;
        }

        MessagesUtil.sendInvalidCommand(player, null);
    }
}
