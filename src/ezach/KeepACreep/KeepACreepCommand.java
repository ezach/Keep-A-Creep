/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

// Bukkit imports
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Main command handler for Keep-A-Creep
 * TODO: clean this up as it's VERY VERY messy.
 * @author E_Zach
 */
public class KeepACreepCommand implements CommandExecutor
{
    private final KeepACreep _plugin;

    private final String[] useageInfo = {
                                         "Keep-A-Creep Commands:",
                                         "    /kac creeper",
                                         "    /kac tnt"
                                        };
    private final String[] creeperUsageInfo = {
                                                "Creeper Specific Commands:",
                                                "    /kac creeper explode [t/f]",
                                                "    /kac creeper spawn [t/f]",
                                                "    /kac creeper keep [t/f]"
                                              };
    private final String[] tntUsageInfo = {
                                            "TNT Specific Commands:",
                                            "    /kac tnt explode [t/f]"
                                          };

    public KeepACreepCommand(KeepACreep plugin)
    {
        _plugin = plugin;
    }

    // MESSY MESSY HATE HATE!!!1!!1one
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
        if (!(sender instanceof Player))
        {
            return false;
        }
        
        Player player = (Player) sender;

        if (split.length == 1)
        {
            // send back a usage msg
            // NOTE: atm we check each command manually in a giant if else. FIXME! convert commands to enum or something MUCH nicer
            if (split[0].equalsIgnoreCase("creeper"))
            {
                if (!this.CheckIfHasPermission(player, PermissionNodes.Creeper))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    return true;
                }
                else
                {
                    player.sendMessage(Messaging.parse(getFormattedUsageString(creeperUsageInfo)));
                }
            }
            if (split[0].equalsIgnoreCase("tnt"))
            {
                if (!CheckIfHasPermission(player, PermissionNodes.TNT))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }
                else
                {
                    player.sendMessage(Messaging.parse(getFormattedUsageString(tntUsageInfo)));
                }
            }
        }
        else if (split.length == 2)
        {
            if (split[0].equalsIgnoreCase("creeper") && !CheckIfHasPermission(player, PermissionNodes.Creeper))
                player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
            else if(split[0].equalsIgnoreCase("tnt") && !CheckIfHasPermission(player, PermissionNodes.TNT))
                player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
            else if((split[0].equalsIgnoreCase("creeper") && (split[1].equalsIgnoreCase("explode") || split[1].equalsIgnoreCase("keep") || split[1].equalsIgnoreCase("spawn") || split[1].equalsIgnoreCase("explode")))
                || (split[0].equalsIgnoreCase("tnt") && (split[1].equalsIgnoreCase("explode"))) )
                player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + " /kac "+split[0]+" "+split[1]+" [t/f]"));
            else if(split[0].equalsIgnoreCase("creeper"))
                player.sendMessage(Messaging.parse(getFormattedUsageString(creeperUsageInfo)));
            else if(split[0].equalsIgnoreCase("tnt"))
                player.sendMessage(Messaging.parse(getFormattedUsageString(tntUsageInfo)));
            else
                player.sendMessage(Messaging.parse(getFormattedUsageString(useageInfo)));

        }
        else if (split.length == 3)
        {
            
            if (split[0].equalsIgnoreCase("creeper"))
            {
                if (!CheckIfHasPermission(player, PermissionNodes.TNT))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }
                String newValue = split[1].toLowerCase();

                if (!this.getValue(newValue).equals(""))
                {
                    setProperty("Flags.ExplodeCreepers", newValue);
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[0].toLowerCase()).replace("%2%", newValue)));
                }
                else
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                //return true;
            }
            
            if (split[0].equalsIgnoreCase("tnt"))
            {
                if (!CheckIfHasPermission(player, PermissionNodes.TNT))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }

                String newValue = split[1].toLowerCase();

                if (!this.getValue(newValue).equals(""))
                {
                    setProperty("Flags.ExplodeTNT", newValue);
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[0].toLowerCase()).replace("%2%", newValue)));
                }
                else
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
            }
        }
        else
        {
            player.sendMessage(Messaging.parse(getFormattedUsageString(useageInfo)));
        }
        return true;
    }

    private void setProperty(String Key, String value)
    {
        _plugin.settings.setProperty(Key, value);
        _plugin.loadFlags();
        _plugin.settings.save();
    }

    /**
     * small helper function to get a t or f result. "" is bad input.
     * @param value - input string
     * @return 'true', 'false' or "" value
     */
    private String getValue(String value)
    {
        if (value.equals("t"))
            value = "true";
        else if (value.equals("f"))
            value = "false";

        if (value.equals("true") || value.equals("false"))
            return value;
        
        return "";
    }

    private boolean CheckIfHasPermission(Player player, String Node)
    {
        if (KeepACreep.permissionHandler != null)
            return KeepACreep.permissionHandler.has(player, Node);
        else if (player.isOp())
            return true;
        return false;
    }

    private String getFormattedUsageString(String[] usageArray)
    {
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix).append(Messaging.msgColour).append(" ");
        for (String i : usageArray)
            fullString.append(i).append("\r\n");
        if (usageArray.length > 0)
            fullString.setLength(fullString.length()-2);
        
        return fullString.toString();
    }

    /**
     * Helper function which returns an id of the function we want to use.
     * @param command - the split param list
     * @return - command id.
     * TODO: actually implement this. possibly have string lookup table?
     */
    private int getCommandID(String[] command)
    {
        return 0;
    }
}