package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

public class Messaging
{
    // current special tags: %NAME% = name of the plugin.
    // TODO: add others?
    static String pluginName = "";
    static String msgColour = "&2";
    static String logPrefix = "[%NAME%]"; // Prefix to go in front of all log entries
    static String msgPrefix = "&4[&6%NAME%&4]&f";
    static String language = "English";
    
    /**
     * Parses the original string against color specific codes. This one converts &[code] to \u00A7[code]
     * <br /><br />
     * Example:
     * <blockquote><pre>
     * Messaging.parse("Hello &2world!"); // returns: Hello \u00A72world!
     * </pre></blockquote>
     *
     * @param original The original string used for conversions.
     *
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String parse(String original)
    {
	original = colorize(original);
	return original.replaceAll("(&([a-z0-9]))", "\u00A7$2").replace("&&", "&").replace("%NAME%", pluginName);
    }
    
    /**
     * Converts color codes into the simoleon code. Sort of a HTML format color code tag.
     * <p>
     * Color codes allowed: black, navy, green, teal, red, purple, gold, silver, gray, blue, lime, aqua, rose, pink, yellow, white.</p>
     * Example:
     * <blockquote<pre>
     * Messaging.colorize("Hello <green>world!"); // returns: Hello \u00A72world!
     * </pre></blockquote>
     *
     * @param original Original string to be parsed against group of color names.
     *
     * @return <code>String</code> - The parsed string after conversion.
     */
    public static String colorize(String original) {
    //Removed the weird character
	return original.replace("<black>", "\u00A70").replace("<navy>", "\u00A71").replace("<green>", "\u00A72").replace("<teal>", "\u00A73").replace("<red>", "\u00A74").replace("<purple>", "\u00A75").replace("<gold>", "\u00A76").replace("<silver>", "\u00A77").replace("<gray>", "\u00A78").replace("<blue>", "\u00A79").replace("<lime>", "\u00A7a").replace("<aqua>", "\u00A7b").replace("<rose>", "\u00A7c").replace("<pink>", "\u00A7d").replace("<yellow>", "\u00A7e").replace("<white>", "\u00A7f");
    }

    public static void sendColouredPlayerMessage(Player player, String msg)
    {
        player.sendMessage(Messaging.parse(Messaging.msgPrefix + Messaging.msgColour + msg));
    }

    public static void sendColouredLocalisedPlayerMessage(Player player, String localeID)
    {
        sendColouredLocalisedPlayerMessage(player, localeID, Messaging.language);
    }

    public static void sendColouredLocalisedPlayerMessage(Player player, String localeID, String language)
    {
        sendColouredPlayerMessage(player, Locale.instance().getLocalisedString(localeID, language));
    }

    public static void sendServerMessage(CommandSender server, String msg)
    {
        server.sendMessage(Messaging.msgPrefix + Messaging.msgColour + msg);
    }

    public static void sendLocalisedServerMessage(CommandSender server, String localeID)
    {
        sendLocalisedServerMessage(server, localeID, Messaging.language);
    }

    public static void sendLocalisedServerMessage(CommandSender server, String localeID, String language)
    {
        sendServerMessage(server, Locale.instance().getLocalisedString(localeID, language));
    }
}
