/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */
public class dataFlags
{
    // our instance var
    private static dataFlags _instance = null;

    // now our data flags we want to be editable.
    boolean explodeCreepers = false;
    boolean explodeTNT = false;
    boolean keepCreepers = false;
    boolean spawnCreepers = true;

    private dataFlags()
    {
        // init me.
    }

    public static dataFlags instance()
    {
        if (_instance == null)
            _instance = new dataFlags();
        
        return _instance;
    }
}
