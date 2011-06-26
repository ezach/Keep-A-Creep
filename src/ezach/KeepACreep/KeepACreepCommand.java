/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handler for the /pos sample command.
 * @author SpaceManiac
 */
public class KeepACreepCommand implements CommandExecutor {
    private final KeepACreep _plugin;

    public KeepACreepCommand(KeepACreep plugin)
    {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (split.length == 0)
        {
            // send back a usage msg

            return false;
        }
        else if (split.length == 2)
        {
            if (split[0].equalsIgnoreCase("creeper"))
            {
                if (!KeepACreep.permissionHandler.has(player, PermissionNodes.EnableCreeper))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    return true;
                }

                String newValue = split[1].toLowerCase();

                if (newValue.equals("t"))
                    newValue = "true";
                else if (newValue.equals("f"))
                    newValue = "false";

                if (newValue.equals("true") || newValue.equals("false"))
                {
                    setProperty("Flags.ExplodeCreepers", newValue);
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[0].toLowerCase()).replace("%2%", newValue)));
                }
                else
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                return true;
            }
            else if (split[0].equalsIgnoreCase("tnt"))
            {
                if (!KeepACreep.permissionHandler.has(player, PermissionNodes.EnableTNT))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }

                String newValue = split[1].toLowerCase();

                if (newValue.equals("t"))
                    newValue = "true";
                else if (newValue.equals("f"))
                    newValue = "false";

                if (newValue.equals("true") || newValue.equals("false"))
                {
                    setProperty("Flags.ExplodeTNT", newValue);
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[0].toLowerCase()).replace("%2%", newValue)));
                }
                else
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                return true;
            }
        }
        return false;
    }

    private void setProperty(String Key, String value)
    {
        _plugin.settings.setProperty(Key, value);
        _plugin.loadFlags();
        _plugin.settings.save();
    }
}