/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ezach.KeepACreep;

/**
 *
 * @author E_Zach
 */

import java.util.logging.Level;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityListener;

public class KeepACreepEntityListener extends EntityListener
{
    private KeepACreep _plugin = null;
    public KeepACreepEntityListener(KeepACreep plugin)
    {
        _plugin = plugin;
    }

    /**
     * check if we're attempting to spawn a creeper, and if we are, stop it if we don't want it to.
     * @param event
     */
    @Override
    public void onCreatureSpawn( CreatureSpawnEvent event)
    {
        if (event.getCreatureType() == CreatureType.CREEPER && !dataFlags.instance().spawnCreepers)
        {
            event.setCancelled(true);
        }
        super.onCreatureSpawn(event);
    }

    /**
     * here is where we actually stop the creeper from exploding and kill it if we don't want it hanging around.
     * @param event
     */
    @Override
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        Entity ent = event.getEntity();
        // tnt? kill it if we don't want it.
        if (ent instanceof TNTPrimed && !dataFlags.instance().explodeTNT)
        {
            event.setCancelled(true);
        }
        // do we want to stop the creeper?
        else if(ent instanceof Creeper && !dataFlags.instance().explodeCreepers)
        {
            // kill the explosion
            event.setCancelled(true);
            // also remove the creeper after explosion?
            if (!dataFlags.instance().keepCreepers)
            {
                ent.remove();
            }
        }
        super.onExplosionPrime(event);
    }

    /**
     * same as above. this is a just in case. (if the prime event isn't caught for some reason.)
     * @param event
     */
    @Override
    public void onEntityExplode(EntityExplodeEvent event)
    {
        Entity ent = event.getEntity();

        if (ent instanceof TNTPrimed && !dataFlags.instance().explodeTNT)
        {
            event.setCancelled(true);
        }
        else if(ent instanceof Creeper && !dataFlags.instance().explodeCreepers)
        {
            event.setCancelled(true);
            if (!dataFlags.instance().keepCreepers)
            {
                ent.remove();
            }
        }
        super.onEntityExplode(event);
    }
}
