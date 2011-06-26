/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */
// Bukkit includes
import java.io.File;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationException;

public class Locale
{
    private static Locale _instance = null;

    private YMLFile localeFile = null;
    
    private Locale()
    {
        // init me.
    }

    public static Locale instance()
    {
        if (_instance == null)
            _instance = new Locale();

        return _instance;
    }

    /**
     * Loads the specified file and uses it as our locale file
     * @param filePath - path to the locale file (within the .jar)
     */
    public void setLocalFile(String filePath)
    {
        localeFile = new YMLFile(this.getClass().getResource(filePath), false, false);
    }
    
    public String getLocalisedString(String LocaleID, String Language)
    {
        return localeFile.getString(LocaleID+"."+Language, LocaleID);
    }

    /**
     * Checks the locale file to see if we have a Localized string in the required language
     * @param LocaleID - ID of the localized string
     * @param Language - the language of the specific string
     * @return - a boolean whether we have a localized string or not
     */
    public boolean hasLocalisedString(String LocaleID, String Language)
    {
        return !localeFile.getString(LocaleID+"."+Language, LocaleID).equals("");
    }
}
