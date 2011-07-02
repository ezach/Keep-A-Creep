/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

// System Includes
import java.util.logging.Level;
import java.util.HashMap;

// Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Main command handler for Keep-A-Creep
 * TODO: group bool commands into one class, similar to the value query command class.
 * @author E_Zach
 */
public class KeepACreepCommand implements CommandExecutor
{
    private final KeepACreep _plugin;
    private HashMap<Integer, ExecutableCommand> commandMap = new HashMap<Integer, ExecutableCommand>();
    private CommandParser mainParser = new CommandParser("/ezach/KeepACreep/Resources/Commands.yml");

    public KeepACreepCommand(KeepACreep plugin)
    {
        _plugin = plugin;

        // add all our commands
        // info commands first
        commandMap.put(0, new CmdMainHelp()); // main commandList
        commandMap.put(1, new CmdReloadOnlyHelp()); // reload only
        commandMap.put(2, new CmdCreeperHelp()); // Creeper info
        commandMap.put(3, new CmdTntHelp()); // Tnt info
        commandMap.put(4, new CmdBoolInfo()); // bool command info
        // now actual commands
        commandMap.put(10, new CmdCreeperExplode()); // Creeper Explode
        commandMap.put(11, new CmdCreeperSpawn()); // Creeper Spawn
        commandMap.put(12, new CmdCreeperKeep()); // Creeper Keep
        commandMap.put(13, new CmdCreeperAreaDamage()); // Creeper Damage Player
        commandMap.put(14, new CmdCreeperPlayerDamage()); // Creeper Damage Area
        commandMap.put(15, new CmdTNTExplode()); // TNT Explode
        commandMap.put(16, new CmdReload()); // Reload
        // error messages
        commandMap.put(20, new CmdPermissionsFail()); // Permission Error
        commandMap.put(21, new CmdBoolError()); // Bool error
        commandMap.put(22, new CmdMainHelp()); // others?
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
        boolean cmdResult = false;
        boolean isPlayer = (sender instanceof Player);

        // TODO: change this, make sure we have server compatable commands.
        if (!(sender instanceof Player))
        {
            return false;
        }
        
        Player player = (Player) sender;

        // if we kill the in-game commands, then we only want reload to work.
        // TODO: make this work again. (whitelist commands possibly?)
        if (!dataFlags.instance().UseInGameCommands)
        {
            
        }

        int[] parseResult = mainParser.parseCommand(sender, command.getName(), split, !isPlayer);
        
        if (parseResult[0] != -1 && commandMap.containsKey(parseResult[0]))
            cmdResult = commandMap.get(parseResult[0]).runCommand(sender, command, label, split);
        if (!cmdResult && parseResult[1] != -1 && commandMap.containsKey(parseResult[1]))
            cmdResult = commandMap.get(parseResult[1]).runCommand(sender, command, label, split);

        return cmdResult;
    }
}

// our base command class.
// all commands are derived from this.
// TODO: move this out of the command class into it's own?
class ExecutableCommand
{
    // grab a copy of the main plugin class in case we need it.
    KeepACreep _plugin;
    
    public ExecutableCommand(KeepACreep plugin)
    {
        _plugin = plugin;
    }

    public ExecutableCommand()
    {
        _plugin = KeepACreep.getMainInstance();
    }

    // this is the method we want to override.
    // it's basically a smaller, modular version of the onCommand but i control it. :P
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        return false;
    }

    // helper commands
    /**
     * small helper function to get a t or f result. "" is bad input.
     * @param value - input string
     * @return 'true', 'false' or "" value
     */
    protected String getBoolValue(String value)
    {
        if (value.equals("t"))
            value = "true";
        else if (value.equals("f"))
            value = "false";

        if (value.equals("true") || value.equals("false"))
            return value;

        return "";
    }

    /**
     * sets a boolean property in the config.
     * @param Key
     * @param value
     */
    protected void setBoolProperty(String Key, String value)
    {
        boolean boolValue = false;
        if (value.equals("true"))
            boolValue = true;
        _plugin.settings.setProperty(Key, boolValue);
        reloadAndSaveSettings();
    }

    /**
     * does what it says it does. :)
     */
    private void reloadAndSaveSettings()
    {
        _plugin.loadFlags();
        _plugin.settings.save();
    }
}

// ID 0
class CmdMainHelp extends ExecutableCommand
{
    // Main help message if we've completely fucked up.
    // TODO: localize this
    private final String[] useageInfo = {
                                         "Commands:",
                                         "    /kac reload",
                                         "    /kac creeper",
                                         "    /kac tnt"
                                        };
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        // TODO: server specific msg
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        // we do this so the first line has the [pluginName] at the beginning.
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix);
        for (String i : useageInfo)
        {
            // Me no likey. Minecraft Chat box doesn't support newlines.
            player.sendMessage(Messaging.parse(fullString.append(Messaging.msgColour).append(" ").append(i).toString()));
            fullString.setLength(0);
        }
        
        return true;
    }
}


// ID 1
class CmdReloadOnlyHelp extends ExecutableCommand
{
    // Main help message if we've completely fucked up.
    // TODO: localize this
    private final String[] useageInfo = {
                                         "Commands:",
                                         "    /kac reload"
                                        };
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        // TODO: server specific msg
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        // we do this so the first line has the [pluginName] at the beginning.
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix);
        for (String i : useageInfo)
        {
            // Me no likey. Minecraft Chat box doesn't support newlines.
            player.sendMessage(Messaging.parse(fullString.append(Messaging.msgColour).append(" ").append(i).toString()));
            fullString.setLength(0);
        }

        return true;
    }
}

// ID 2
class CmdCreeperHelp extends ExecutableCommand
{
    // Main help message if we've completely fucked up.
    // TODO: localize this
    private final String[] useageInfo = {
                                        "Creeper Specific Commands:",
                                        "    /kac creeper explode [t/f]",
                                        "    /kac creeper spawn [t/f]",
                                        "    /kac creeper keep [t/f]",
                                        "    /kac creeper damageplayer [t/f]",
                                        "    /kac creeper damagearea [t/f]"
                                        };
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        // TODO: server specific msg
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        // we do this so the first line has the [pluginName] at the beginning.
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix);
        for (String i : useageInfo)
        {
            // Me no likey. Minecraft Chat box doesn't support newlines.
            player.sendMessage(Messaging.parse(fullString.append(Messaging.msgColour).append(" ").append(i).toString()));
            fullString.setLength(0);
        }

        return true;
    }
}


// ID 3
class CmdTntHelp extends ExecutableCommand
{
    // Main help message if we've completely fucked up.
    // TODO: localize this
    private final String[] useageInfo = {
                                        "TNT Specific Commands:",
                                        "    /kac tnt explode [t/f]"
                                        };
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        // TODO: server specific msg
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        // we do this so the first line has the [pluginName] at the beginning.
        StringBuilder fullString = new StringBuilder(Messaging.msgPrefix);
        for (String i : useageInfo)
        {
            // Me no likey. Minecraft Chat box doesn't support newlines.
            player.sendMessage(Messaging.parse(fullString.append(Messaging.msgColour).append(" ").append(i).toString()));
            fullString.setLength(0);
        }

        return true;
    }
}

// ID 20
class CmdPermissionsFail extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredLocalisedPlayerMessage((Player)sender, "NoPermission");
        else
            Messaging.sendLocalisedServerMessage(sender, "NoPermission");

        return true;
    }
}

// ID 4
class CmdBoolInfo extends ExecutableCommand
{
    static HashMap<String, String> stringLookup = null;

    public CmdBoolInfo()
    {
        super();

        if (stringLookup == null)
        {
            // TODO: add any others we want here.
            stringLookup = new HashMap<String, String>();
            stringLookup.put("creeperspawn", "SpawnCreepers");
            stringLookup.put("creeperexplode", "ExplodeCreepers");
            stringLookup.put("creeperkeep", "KeepCreepers");
            stringLookup.put("creeperdamageplayer", "KeepCreepers");
            stringLookup.put("creeperdamagearea", "KeepCreepers");
            stringLookup.put("tntexplode", "ExplodeTNT");
            //stringLookup.put("commandlower", "Upper");
        }
    }

    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // dirty, yes i know.
        String message = stringLookup.get(split[0]+split[1]);
        message = Locale.instance().getLocalisedString("ValueCheck", Messaging.language).replace("%1%", message).replace("%2%", _plugin.settings.getString("Flags."+message));

        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

// ID 21
class CmdBoolError extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredLocalisedPlayerMessage((Player)sender, "ValueHint");
        else
            Messaging.sendLocalisedServerMessage(sender, "ValueHint");

        return true;
    }
}

class CmdReload extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        _plugin.settings.load();
        _plugin.loadFlags();
        
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredLocalisedPlayerMessage((Player)sender, "ConfigReload");
        else
            Messaging.sendLocalisedServerMessage(sender, "ConfigReload");

        return true;
    }
}

class CmdCreeperExplode extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value? 
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.ExplodeCreepers", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

class CmdCreeperSpawn extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value?
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.SpawnCreepers", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

class CmdCreeperKeep extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value?
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.KeepCreepers", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

class CmdCreeperPlayerDamage extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value?
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.ExplodeCreepers", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

class CmdCreeperAreaDamage extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value?
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.ExplodeCreepers", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}

class CmdTNTExplode extends ExecutableCommand
{
    @Override
    boolean runCommand(CommandSender sender, Command command, String label, String[] split)
    {
        String newValue = getBoolValue(split[2].toLowerCase());

        // not a bool value?
        if(newValue.equals(""))
            return false;

        setBoolProperty("Flags.ExplodeTNT", newValue);

        String message = Locale.instance().getLocalisedString("ValueResult", Messaging.language).replace("%1%", split[1].toLowerCase()+ " " +split[0].toLowerCase()).replace("%2%", newValue);
        // only send the message to the player.
        if (sender instanceof Player)
            Messaging.sendColouredPlayerMessage((Player)sender, message);
        else
            Messaging.sendServerMessage(sender, message);

        return true;
    }
}