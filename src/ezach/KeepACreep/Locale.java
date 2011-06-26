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

    /**
     * Loads the specified file and uses it as out locale file.
     * @param externalFile - File Object which points to the Locale file (external to .jar)
     */
    public void setLocalFile(File externalFile)
    {
        localeFile = new YMLFile(externalFile, false, false);
    }

    /**
     * returns a localized string from the LocaleID passed in.
     * @param LocaleID - string id which we use to identify which string we want.
     * @param Language - the language we want to retrieve the text for.
     *                   (will return the default language string if we can't find the language required.)
     *                   if the ID isn't found at all, we return the localeID within !!'s
     * @return - returns the localized string.
     */
    // TODO: pass in parameters to insert into the string?
    public String getLocalisedString(String LocaleID, String Language)
    {
        String localeText = localeFile.getString("LocaleData."+LocaleID+"."+Language, "");
        if (localeText.equals(""))
            localeText = localeFile.getString("LocaleData."+localeFile.getString("DefaultLanguage")+"."+Language, "!!"+LocaleID+"!!");
            
        return localeText;
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
