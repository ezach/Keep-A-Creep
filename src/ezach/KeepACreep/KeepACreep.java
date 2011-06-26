/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */

// System Includes
import java.util.HashMap;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

// Bukkit Includes
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// Permissions includes
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class KeepACreep extends JavaPlugin
{
    private final KeepACreepEntityListener entityListener = new KeepACreepEntityListener(this);
    public static final Logger log = Logger.getLogger("Minecraft"); // Minecraft log and console
    public File pFolder = new File("plugins" + File.separator + "KeepACreep"); // Folder to store plugin settings file and database
    public static PermissionHandler permissionHandler = null;
    // as we are unable to define custom items within the Plugin.yml we stick the version of the config here.
    public final String settingsVersion = "1.0";
    public YMLFile settings = new YMLFile(new File(pFolder.getPath(), "Config.yml"), true, true);
    
    public void onDisable()
    {
        // let the user know we've been disabled.
       log.log(Level.INFO, Messaging.parse(new StringBuilder(Messaging.logPrefix).append(" is Disabled!").toString() ));
       // NOTE: we do NOT save out settings here. (residence bug. =P)
    }

    public void onEnable()
    {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.EXPLOSION_PRIME, entityListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.High, this);

        // Set the Plugin Name for any console Text.
        Messaging.pluginName = this.getDescription().getName();

        Locale.instance().setLocalFile("/ezach/KeepACreep/Resources/Locale.yml");
        
        // first check our settings file to make sure we have the correct version,
        // and that we actually have the correct version (haven't just upgraded)
        if (!settings.exists())
            settings.writeTemplate();
        else if (!settings.getString("Version", "UNKNOWN").equalsIgnoreCase(settingsVersion))
        {
            // attempt to set our locale before upgrading
            Messaging.language = settings.getString("Messages.Language", "English");
            log.log(Level.INFO,  Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("UpgradeConfigText", Messaging.language)).append(settings.getString("Version", "UNKNOWN")).toString()));
            if (settings.upgrade(settings.getString("Version", "UNKNOWN")))
            {
                log.log(Level.INFO,  Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("UpgradeConfigSuccess", Messaging.language)).toString()));
            }
            else
            {
                log.log(Level.INFO,  Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("UpgradeConfigFail", Messaging.language)).toString()));
                // if we failed to upgrade the plugin, then kill the plugin
                this.setEnabled(false);
                return;
            }
        }

        getCommand("kac").setExecutor(new KeepACreepCommand(this));

        // get our custom Messaging stuff
        Messaging.msgPrefix = settings.getString("Messages.Prefix", Messaging.msgPrefix);
        Messaging.msgColour = "&"+settings.getString("Messages.TextColour", "f");

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        log.log( Level.INFO, Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("EnablePlugin", Messaging.language).replace("%VERSION%", this.getDescription().getVersion())).toString()));
        
        // attempt to hook into permissions
        setupPermissions();

        // load in and set our data flags from the config.
        loadFlags();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {

        if(cmd.getName().equalsIgnoreCase("basic"))
        {
            //doSomething
            return true;
        }
        return false;
    }

    private void setupPermissions()
    {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (permissionHandler == null)
        {
            if (permissionsPlugin != null)
            {
                permissionHandler = ((Permissions) permissionsPlugin).getHandler();
                log.log(Level.INFO , Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("PermissionsSuccess", Messaging.language)).toString()));

            }
            else
            {
                log.log(Level.INFO, Messaging.parse(new StringBuilder(Messaging.logPrefix).append(Locale.instance().getLocalisedString("PermissionsFail", Messaging.language)).toString()));
            }
        }
    }

    /**
     * this loads in our flags from the config file
     *
     * so fare we only grab whether we should disable creepers and tnt.
     */
    public void loadFlags()
    {
        dataFlags.instance().explodeCreepers = settings.getBoolean("Flags.ExplodeCreepers", true);
        dataFlags.instance().explodeTNT = settings.getBoolean("Flags.ExplodeTNT", true);
        dataFlags.instance().keepCreepers = settings.getBoolean("Flags.KeepCreepers", false);
        dataFlags.instance().spawnCreepers = settings.getBoolean("Flags.SpawnCreepers", true);
    }
}