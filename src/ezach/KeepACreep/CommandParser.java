
package ezach.KeepACreep;

// System includes
import java.util.List;

// Bukkit Includes
import java.util.logging.Level;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

/**
 * class specifically designed to parse the 'Commands.yml' and
 * return the correct function identifier (an int)
 * @author E_Zach
 */
public class CommandParser
{
    private YMLFile commandFile;

    public CommandParser(String ResourcePath)
    {
        // load in the commands yml from the package
        commandFile = new YMLFile(this.getClass().getResource(ResourcePath), false, false);
    }

    /**
     * parses the arguments passed in and returns an identifier to run, and a fail function as well (if the command still fails)
     * @param BaseCommand - our 'root' command. in '/bla main sub' bla would be the baseCommand
     * @param argList - all the other arguments so in above example, it would be [main, sub].
     * @param isServer - is this command sent from the server, if so, then don't permissions. Server is almighty!
     * @return - pair of int's one is the command to run, second is an on fail command (ALL commands should have this)
     */
    public int[] parseCommand(CommandSender sender, String BaseCommand, String[] argList, boolean isServer)
    {
        int[] command = {-1,-1};
        int argPos = 0;
        String commandString = BaseCommand.toLowerCase();

        // first get the root node.
        ConfigurationNode currentNode = commandFile.getNode(commandString);
        if (currentNode == null)
            return command;
        
        boolean found = false;
        while (!found)
        {
            // grab common args once.
            int mainCmd = currentNode.getInt("Main", -1);
            int failCmd = currentNode.getInt("Fail", -1);

            // first see if this command is executable
            if (mainCmd != -1)
            {
                // again, grab common args once
                int argsLength = currentNode.getInt("Args", -1);
                int optArgLength = currentNode.getInt("OptArgs", 0);
                int pError = currentNode.getInt("PError", -1);
                String permissionNode = currentNode.getString("PNode", "");

                // check if we want permissions and see if we pass them.
                if (!permissionNode.equals("") && pError != -1 &&
                        !isServer && !CheckIfHasPermission((Player)sender, permissionNode))
                {
                    // if not, throw a permission error.
                    command[0] = pError;
                    return command;
                }
                // now check if the command length meets our expectations.
                else if (argsLength < 0 || argList.length - argPos < argsLength || argList.length - argPos > argsLength+optArgLength)
                {
                    // if it doesn't, but this command has an info tag, and is specifically the command length, show the info.
                    if (currentNode.getInt("Info", -1) != -1 && argList.length == argPos)
                    {
                        // NOTE: we have the fail command here just in case.
                        command[0] = currentNode.getInt("Info", -1);
                        command[1] = failCmd;
                        return command;
                    }
                    // otherwise fail.
                    command[0] = failCmd;
                    return command;
                }

                // if we get here then we do want to run this command. pass back our command and fail case function ID's.
                command[0] = mainCmd;
                command[1] = failCmd;
                return command;
            }
            // if not, then we're posibly trying to display info for this command.
            else if (currentNode.getInt("Info", -1) != -1 && argList.length == argPos)
            {
                // NOTE: we have the fail command here just in case.
                command[0] = currentNode.getInt("Info", -1);
                command[1] = failCmd;
                return command;
            }
            // otherwise we might have an inner sub command.
            else
            {
                List<String> nodes = currentNode.getStringList("Sub", null);
                // no sub commands? throw a fail. also throw a fail if we're at this point with no more args
                if (nodes == null || argList.length <= argPos)
                {
                    command[0] = failCmd;
                    return command;
                }
                // if we do, try to match the next sub command with the arg list.
                else
                {
                    boolean foundMe = false;
                    // search through all the sub commands
                    for (String i : nodes)
                    {
                        // find a match? add it to the lookup
                        if (argList[argPos].equalsIgnoreCase(i))
                        {
                            commandString += "~"+i.toLowerCase();
                            // grab the new node
                            currentNode = commandFile.getNode(commandString);
                            // check to see if the node actually exists
                            if (currentNode == null)
                            {
                                // if not, throw a fail.
                                // TODO: possibly throw a message to the player?
                                command[0] = failCmd;
                                return command;
                            }
                            argPos += 1;
                            foundMe = true;
                            break;
                        }
                    }

                    if (!foundMe)
                    {
                        // found nothing? return the fail command
                        command[0] = failCmd;
                        return command;
                    }
                }
            }
        }

        return command;
    }

    /**
     * Helper function which checks if the specific player has permission for the
     * Specific permission Node.
     * @param player
     * @param Node
     * @return
     */
    private boolean CheckIfHasPermission(Player player, String Node)
    {
        if (KeepACreep.permissionHandler != null && dataFlags.instance().UsePermissions)
            return KeepACreep.permissionHandler.has(player, Node);
        else if (player.isOp())
            return true;
        return false;
    }
}
