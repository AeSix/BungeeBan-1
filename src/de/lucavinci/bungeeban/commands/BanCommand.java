package de.lucavinci.bungeeban.commands;

import de.lucavinci.bungeeban.BungeeBan;
import de.lucavinci.bungeeban.util.Ban;
import de.lucavinci.bungeeban.util.BungeeBanCommand;
import de.lucavinci.bungeeban.util.BungeeBanPlayer;
import de.lucavinci.bungeeban.util.ConfigManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * This command bans a given player permanently
 */
public class BanCommand extends BungeeBanCommand {

    public BanCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission(ConfigManager.cv("general.permissions.ban"))) {
            if(args.length >= 2) {
                String playername = args[0];
                if(BungeeBan.getApi().playerExists(playername)) {
                    UUID uuid = BungeeBan.getApi().getUuid(playername);
                    BungeeBanPlayer bbp = BungeeBan.getApi().getPlayer(uuid);
                    playername = bbp.getPlayername();
                    String reason = "";
                    for(int i = 1; i < args.length; i++) {
                        reason += args[i] + " ";
                    }
                    Ban ban = new Ban(uuid, sender.getName(), reason, -1);
                    bbp.ban(ban);
                    bbp.save();
                    sender.sendMessage(BungeeBan.PREFIX + ConfigManager.txt("commands.ban.success").replace("%PLAYER%", playername));
                    for(ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
                        if(o.hasPermission(ConfigManager.cv("general.permissions.ban-broadcast"))) {
                            for(String line : ConfigManager.txt3("commands.ban.broadcast")) {
                                line = line.replace("%PLAYER%", playername);
                                line = line.replace("%BANNEDBY%", sender.getName());
                                line = line.replace("%REASON%", reason);
                                line = line.replace("%LENGTH%", ConfigManager.txt("permanenttime"));
                                o.sendMessage(BungeeBan.PREFIX + line);
                            }
                        }
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + ConfigManager.txt("errors.playernotfound").replace("%PLAYERNAME%", playername));
                }
            } else {
                sender.sendMessage(BungeeBan.PREFIX + ConfigManager.txt("commands.ban.syntax"));
            }
        } else {
            sender.sendMessage(BungeeBan.PREFIX + ConfigManager.txt("errors.nopermissions"));
        }
    }
}
