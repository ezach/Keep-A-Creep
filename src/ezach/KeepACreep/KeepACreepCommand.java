/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

// Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Main command handler for Keep-A-Creep
 * TODO: clean this up as it's VERY VERY messy.
 * NOTE: i HATE the current state of this class. i WILL refactor it.
 * @author E_Zach
 */
public class KeepACreepCommand implements CommandExecutor
{
    private final KeepACreep _plugin;

    private final String[] useageInfo = {
                                         "Commands:",
                                         "    /kac reload",
                                         "    /kac creeper",
                                         "    /kac tnt"
                                        };

    private final String[] reloadOnlyUseageInfo = {
                                         "Commands:",
                                         "    /kac reload"
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

        // if we kill the in-game commands, then we only want reload to work.
        if (!dataFlags.instance().UseInGameCommands)
        {
            // if we disabled the ingame commands, we only wan't reload to be avaliable.
            if (split.length == 1 && split[0].equalsIgnoreCase("reload"))
            {
                if (!CheckIfHasPermission(player, PermissionNodes.Reload))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }
                else
                {
                    _plugin.settings.load();
                    _plugin.loadFlags();
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ConfigReload", Messaging.language)));
                }
            }
            else
            {
                sendFormattedUsageString(player, reloadOnlyUseageInfo);
            }
            return true;
        }

        // otherwise start the terible check to see what the player has input.
        // TODO: clean up.
        if (split.length == 1)
        {
            // send back a usage msg
            // NOTE: atm we check each command manually in a giant if else. FIXME! convert commands to enum or something MUCH nicer
            if (split[0].equalsIgnoreCase("creeper"))
            {
                sendFormattedUsageString(player, creeperUsageInfo);
            }
            else if(split[0].equalsIgnoreCase("tnt"))
            {
                sendFormattedUsageString(player, tntUsageInfo);
            }
            if (split[0].equalsIgnoreCase("reload"))
            {
                if (!CheckIfHasPermission(player, PermissionNodes.Reload))
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                }
                else
                {
                    _plugin.settings.load();
                    _plugin.loadFlags();
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ConfigReload", Messaging.language)));
                }
            }
            else
            {
                sendFormattedUsageString(player, useageInfo);
            }
        }
        else if (split.length == 2)
        {
            // our current value
            if((split[0].equalsIgnoreCase("creeper") && (split[1].equalsIgnoreCase("explode") || split[1].equalsIgnoreCase("keep") || split[1].equalsIgnoreCase("spawn")))
                || (split[0].equalsIgnoreCase("tnt") && (split[1].equalsIgnoreCase("explode"))) )
            {
                // is this a creeper command?
                if (split[0].equalsIgnoreCase("creeper"))
                {
                    if (split[1].equalsIgnoreCase("explode"))
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueCheck", Messaging.language).replace("%1%", "ExplodeCreepers").replace("%2%", _plugin.settings.getString("Flags.ExplodeCreepers"))));
                    if (split[1].equalsIgnoreCase("keep"))
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueCheck", Messaging.language).replace("%1%", "KeepCreepers").replace("%2%", _plugin.settings.getString("Flags.KeepCreepers"))));
                    if (split[1].equalsIgnoreCase("spawn"))
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueCheck", Messaging.language).replace("%1%", "SpawnCreepers").replace("%2%", _plugin.settings.getString("Flags.SpawnCreepers"))));
                }
                // then we must be a tnt command (specifically the explode as there isn't any others.)
                else
                {
                    player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueCheck", Messaging.language).replace("%1%", "ExplodeTNT").replace("%2%", _plugin.settings.getString("Flags.ExplodeTNT"))));
                }
            }
            else if(split[0].equalsIgnoreCase("creeper"))
                sendFormattedUsageString(player, creeperUsageInfo);
            else if(split[0].equalsIgnoreCase("tnt"))
                sendFormattedUsageString(player, tntUsageInfo);
            else
                sendFormattedUsageString(player, useageInfo);

        }
        else if (split.length == 3)
        {
            
            if (split[0].equalsIgnoreCase("creeper"))
            {
                if(split[1].equalsIgnoreCase("explode"))
                {
                    String newValue = this.getValue(split[2].toLowerCase());

                    if (!CheckIfHasPermission(player, PermissionNodes.ExplodeCreeper))
                    {
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    }
                    else if(!newValue.equals(""))
                    {
                        setProperty("Flags.ExplodeCreepers", newValue);
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue)));
                    }
                    else
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                }
                else if(split[1].equalsIgnoreCase("spawn"))
                {
                    String newValue = this.getValue(split[2].toLowerCase());

                    if (!CheckIfHasPermission(player, PermissionNodes.SpawnCreeper))
                    {
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    }
                    else if(!newValue.equals(""))
                    {
                        setProperty("Flags.SpawnCreepers", newValue);
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue)));
                    }
                    else
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                }
                else if(split[1].equalsIgnoreCase("keep"))
                {
                    String newValue = this.getValue(split[2].toLowerCase());

                    if (!CheckIfHasPermission(player, PermissionNodes.KeepCreeper))
                    {
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    }
                    else if(!newValue.equals(""))
                    {
                        setProperty("Flags.KeepCreepers", newValue);
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue)));
                    }
                    else
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                }
                else
                {
                    sendFormattedUsageString(player, creeperUsageInfo);
                }
            }
            
            if (split[0].equalsIgnoreCase("tnt"))
            {
                if (split[1].equalsIgnoreCase("explode"))
                {
                    String newValue = this.getValue(split[2].toLowerCase());
                    
                    if (!CheckIfHasPermission(player, PermissionNodes.ExplodeTNT))
                    {
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("NoPermission", Messaging.language)));
                    }
                    else if (!newValue.equals(""))
                    {
                        setProperty("Flags.ExplodeTNT", newValue);
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue)));
                    }
                    else
                        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + Locale.instance().getLocalisedString("ValueHint", Messaging.language)));
                }
                else
                {
                    sendFormattedUsageString(player, tntUsageInfo);
                }
            }
        }
        else
        {
            sendFormattedUsageString(player, useageInfo);
        }
        return true;
    }

    private void setProperty(String Key, String value)
    {
        boolean boolValue = false;
        if (value.equals("true"))
            boolValue = true;
        _plugin.settings.setProperty(Key, boolValue);
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
        if (KeepACreep.permissionHandler != null && dataFlags.instance().UsePermissions)
            return KeepACreep.permissionHandler.has(player, Node);
        else if (player.isOp())
            return true;
        return false;
    }

    private void sendFormattedUsageString(Player player, String[] usageArray)
    {
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix);
        for (String i : usageArray)
        {
            player.sendMessage(Messaging.parse(fullString.append(Messaging.msgColour).append(" ").append(i).toString()));
            fullString.setLength(0);
        }
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