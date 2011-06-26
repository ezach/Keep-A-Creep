package ezach.KeepACreep;

/**
 * YMLFile.java
 * @author E_Zach
 * Credit goes out to the Bukkit team for a bunch of this code.
 */

// System Imports
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;


// Bukkit Specific
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationException;

// SnakeYML
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public final class YMLFile extends Configuration
{
    private Yaml yaml;
    private File file;
    private boolean useTemplates = false;
    private boolean shouldCreate = false;
    private String header = null;
    private boolean readOnly = false;

    private final String templateFile = "/ezach/KeepACreep/Templates.yml";

    public YMLFile(File file, boolean create, boolean useTemplates)
    {
        super(null);
        this.file = file;
        this.useTemplates = useTemplates;
        this.shouldCreate = create;

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        
        this.yaml = new Yaml(new SafeConstructor(), new NullRepresenter(), options);
        this.load();
        if (create)
        {
            this.createFile(useTemplates);
        }
    }

    public YMLFile(URL file, boolean create, boolean useTemplates)
    {
        super(null);

        readOnly = true;
        this.useTemplates = useTemplates;
        this.shouldCreate = create;

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(new SafeConstructor(), new NullRepresenter(), options);
        
        String line;
        StringBuilder data = new StringBuilder();
        BufferedReader bf;
        try
        {
            InputStream in = file.openStream();
            bf = new BufferedReader(new InputStreamReader(in));
        }
        catch (IOException ex)
        {
            logMsg(Level.SEVERE, "Failed to Open File "+file.getFile());
            ex.printStackTrace();
            return;
        }

        try
        {
            while ((line = bf.readLine()) != null)
            {
                data.append(line).append("\n");
            }
            this.read(yaml.load(data.toString()));
        }
        catch (IOException ex)
        {
            logMsg(Level.SEVERE, "Failed to Read File "+file.getFile());
            ex.printStackTrace();
        }
        catch (ConfigurationException ex)
        {
            logMsg(Level.SEVERE, "Failed to Read File "+file.getFile());
            ex.printStackTrace();
        }
    }

    @Override
    public void load()
    {
        FileInputStream stream = null;

        try
        {
            stream = new FileInputStream(file);
            read(yaml.load(new UnicodeReader(stream)));
        }
        catch (IOException e)
        {
            root = new HashMap<String, Object>();
        }
        catch (ConfigurationException e)
        {
            root = new HashMap<String, Object>();
        }
        finally
        {
            try
            {
                if (stream != null)
                {
                    stream.close();
                }
            }
            catch (IOException e)
            {}
        }
    }

    @Override
    public void setHeader(String... headerLines) {
        StringBuilder headerBuilder = new StringBuilder();

        for (String line : headerLines) {
            if (headerBuilder.length() > 0) {
                headerBuilder.append("\r\n");
            }
            headerBuilder.append(line);
        }

        setHeader(headerBuilder.toString());
    }

    @Override
    public void setHeader(String header)
    {
        this.header = header;
    }

    @Override
    public String getHeader()
    {
        return header;
    }

    @Override
    public boolean save()
    {
        if (readOnly)
            return false;
        
        FileOutputStream stream = null;

        File parent = file.getParentFile();

        if (parent != null)
        {
            parent.mkdirs();
        }

        try
        {
            stream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
            if (header != null)
            {
                writer.append(header);
                writer.append("\r\n");
            }
            yaml.dump(root, writer);
            return true;
        }
        catch (IOException e)
        {}
        finally
        {
            try
            {
                if (stream != null)
                {
                    stream.close();
                }
            }
            catch (IOException e)
            {}
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private void read(Object input) throws ConfigurationException {
        try {
            if (null == input) {
                root = new HashMap<String, Object>();
            } else {
                root = (Map<String, Object>) input;
            }
        } catch (ClassCastException e) {
            throw new ConfigurationException("Root document must be an key-value structure");
        }
    }

    public String getDirectory()
    {
        return file.getParent();
    }

    public String getFileName()
    {
        return this.file.getName();
    }

    public void setFile(File file)
    {
        this.file = file;
        this.load();
    }
    
    public void setFile(String file)
    {
        this.setFile(file, false);
    }

    public void setFile(String file, boolean create)
    {
        this.file = new File(this.file.getParent(), file);
        if (create)
        {
            this.createFile(this.useTemplates);
        }
        this.load();
    }

    public void setDirectory(String directory)
    {
        this.setDirectory(directory, false);
    }

    public void setDirectory(String directory, boolean create)
    {
        this.file = new File(directory, this.file.getName());
        if (create)
        {
            this.createDirectory();
        }
        this.load();
    }

    private void log(Level level, Object message)
    {
        Logger.getLogger("Minecraft").log(level, null, message);
    }

    private void logMsg(Level level, String message)
    {
        Logger.getLogger("Minecraft").log(level, message);
    }

    public boolean exists()
    {
        return this.file.exists();
    }

    public boolean exists(String file)
    {
        return this.exists(this.file.getParent(), file);
    }

    public boolean exists(String directory, String file)
    {
        return (new File(directory, file)).exists();
    }

    public void createFile(boolean useTemplate)
    {
        this.createFile(file.getParent(), this.file.getName(), useTemplate);
    }

    public boolean createFile(String directory, String file, boolean useTemplate)
    {
        // create the directorys
        if(!((new File(directory).exists())))
        {
            if (this.createDirectory(directory)) return false;
        }

        // now create the file if it doesn't exist
        if(!((new File(directory, file)).exists()))
        {
            try
            {
                if (useTemplate)
                    this.writeTemplate();
                new File(directory, file).createNewFile();
            }
            catch (IOException ex)
            {
                this.log(Level.SEVERE, ex);
                return false;
            }
        }
        return true;
    }

    public boolean createDirectory()
    {
        return this.createDirectory(file.getParent());
    }

    public boolean createDirectory(String directory)
    {
        if ((new File(directory)).mkdirs())
            return true;

        return false;
    }

    public String loadTemplateHeader(String templateFileName)
    {
        YMLFile headerLoader = new YMLFile(this.getClass().getResource(templateFile), false, false);

        StringBuilder headerBuilder = new StringBuilder();
        //List<String> list = this.WorldConfiguration.get(world).getStringList("groups." + group + ".permissions", new LinkedList<String>());
        List<String> temp = headerLoader.getStringList(templateFileName.replace(".", "~")+"|HEADER", null);
        for ( String s : temp )
            headerBuilder.append(s).append("\r\n");

        // remove the last two newline chars.
        if (headerBuilder.length() > 2)
            headerBuilder.setLength(headerBuilder.length()-2);

        // NOTE: we are only replacing \n \r and \\ at the moment as i see no reason for any other chars. (Prove me wrong =P)
        return headerBuilder.toString().replace("\\n", "\n").replace("\\r", "\r").replace("\\\\", "\\");
    }

    public boolean writeTemplate()
    {
        // if this is the default file, then also load the header. force the user to set the header manually otherwise.
        header = loadTemplateHeader(this.file.getName());
        if (header.equals(""))
            header = null;
        
        return this.writeTemplate(this.file);
    }
    
    // basicaly clears the file completely and overrides it with a fresh template
    // WARNING: only use this if the file doesn't exist.
    @SuppressWarnings("CallToThreadDumpStack")
    public boolean writeTemplate(File newFile)
    {
        // yes this is hardcoded. if you can think of a better way let me know. =P
        URL url = this.getClass().getResource(templateFile);
        String data = "";
        try
        {
            InputStream in = url.openStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            StringBuilder strBuff = new StringBuilder();
            String line;
            boolean foundStart = false;
            StringBuilder lineBuilder = new StringBuilder();
            while((line = bf.readLine()) != null)
            {
                if (foundStart || line.trim().length() == 0 || line.trim().charAt(0) != '#')
                {
                    if (line.length() == line.trim().length() && line.trim().length() > 0)
                    {
                        // if we've already found the start then this is the end of the template.
                        if (foundStart)
                            break;

                        if (line.trim().equalsIgnoreCase(newFile.getName().replace(".", "~")+":"))
                        {
                            // we've already found the template. this is probably a duplicate.
                            if (foundStart)
                                break;
                            
                            foundStart = true;
                        }
                        continue;
                    }
                    boolean leadSpaces = line.length() >= 4;
                    lineBuilder.setLength(0);
                    for (int i = 0; i < line.length() && i < 4; ++i)
                        leadSpaces &= line.charAt(i) == ' ';
                    
                    if (leadSpaces)
                    {
                        for (int i = 4; i < line.length(); ++i)
                        {
                            lineBuilder.append(line.charAt(i));
                        }
                    }
                    if (foundStart)
                        strBuff.append(lineBuilder.toString()).append("\r\n");
                }
            }
            // set the data to our new 'file'
            data = strBuff.toString();
            
            // create the directorys if they don't exist.
            this.createDirectory(newFile.getParent());
            
            // now write out the file
            newFile.setWritable(true);
            FileOutputStream fileWriter = new FileOutputStream(newFile);

            // add our header if we have one.
            if (header != null)
                data = new StringBuilder(header).append("\r\n").append(data).toString();
            
            fileWriter.write(data.getBytes(), 0, data.getBytes().length);
            fileWriter.flush();
            fileWriter.close();

            // now that we've written out the file, we want to load it in so we can use it.
            // but only if we are the main file and not just using this to create templates, etc.
            if (newFile == file)
                this.load();

            return true;
        }
        catch (IOException ex)
        {
            logMsg(Level.INFO, "ERROR:");
        }
        
        return false;
    }

    // should we call save() here as well? or force it to be a seperate call so we don't override settings if we screw up?
    public boolean upgrade(String currentVersion)
    {
        return upgrade(currentVersion, file);
    }

    public boolean upgrade(String currentVersion, File newFile)
    {
        YMLFile template = new YMLFile(this.getClass().getResource(templateFile), false, false);
        YMLFile upgraded = new YMLFile(new File(newFile.getPath()), false, false);//file, false, false);
        
        String templateKey = (newFile.getName()+"|"+currentVersion).replace(".", "~");
        String newHeader = loadTemplateHeader(newFile.getName());
        upgraded.setHeader(newHeader);
        // now we have our initial vars, save this file as the BU
        file = new File(file.getPath()+".bak");
        save();

        // TODO: finish off from here.
        StringBuilder headerBuilder = new StringBuilder();
        //List<String> list = this.WorldConfiguration.get(world).getStringList("groups." + group + ".permissions", new LinkedList<String>());

        if (template.getNode(templateKey) != null)
        {
            Map<String, Object> templateValues = template.getNode(templateKey).getAll();

            for (String item : templateValues.keySet())
            {
                // TODO: have logic loops within here.
                if (templateValues.get(item) instanceof String)
                {
                    String value = (String)templateValues.get(item);
                    // im a string, check if this is a value
                    // so split me first
                    String[] values = value.split("\\|", 2);
                    // and if there is a | in there, check to see if we're the corerct split length.
                    
                    if (values.length == 2)
                    {
                        // is this a value from the old config?
                        if (values[0].equals("VALUE"))
                        {
                            // if it is, then try to grab the value
                            if (getProperty(values[1]) != null)
                            {
                                // if we have the value, then set it.
                                upgraded.setProperty(item, this.getProperty(values[1]));
                                continue;
                            }
                            else
                            {
                                // if we can't throw an error and cancel the upgrade.
                                // TODO: keep going or error out like we do currently?
                                logMsg(Level.INFO, "FAILED: Property '"+values[1]+"' does not exist in "+file.getName());
                                return false;
                            }
                        }
                    }
                    upgraded.setProperty(item, value);
                }
                else if (templateValues.get(item) instanceof List)
                {
                    // this is a list save it straight out? or check for values?
                }

                 upgraded.setProperty(item, templateValues.get(item));
            }

            // if we've gotten here then save out the new config and then
            // load it in as our main settings.
            upgraded.save();
            this.file = upgraded.file;
            this.load();
            // let the caller know it was successful
            return true;
        }
        // if the template for that version doesn't exist chuck an error msg.
        logMsg(Level.INFO, "FAILED: '" + file.getName()+"' doesn't Have an upgrade template for v"+currentVersion);
        return false;
    }
}

class NullRepresenter extends Representer
{

    public NullRepresenter()
    {
        super();
        this.nullRepresenter = new EmptyRepresentNull();
    }

    protected class EmptyRepresentNull implements Represent {
        public Node representData(Object data) {
            return representScalar(Tag.NULL, ""); //Changed "null" to "" so as to avoid writing nulls
        }
    }


    //Code borrowed from snakeyaml (http://code.google.com/p/snakeyaml/source/browse/src/test/java/org/yaml/snakeyaml/issues/issue60/SkipBeanTest.java)
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
            Object propertyValue, Tag customTag) {
        NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue,
                customTag);
        Node valueNode = tuple.getValueNode();
        if (valueNode instanceof CollectionNode) {
            //Removed null check
            if (Tag.SEQ.equals(valueNode.getTag())) {
                SequenceNode seq = (SequenceNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty lists
                }
            }
            if (Tag.MAP.equals(valueNode.getTag())) {
                MappingNode seq = (MappingNode) valueNode;
                if (seq.getValue().isEmpty()) {
                    return null;// skip empty maps
                }
            }
        }
        return tuple;
    }
    //End of borrowed code
}

// OLD/REFERENCE code

//class Template {
//
//    private Configuration tpl = null;
//
//    public Template(String directory, String filename) {
//        this.tpl = new Configuration(new File(directory, filename));
//        this.tpl.load();
//
//        this.upgrade();
//    }
//
//    public void upgrade() {
//        LinkedHashMap<String, String> nodes = new LinkedHashMap<String, String>();
//
//        if(this.tpl.getString("error.bank.exists") == null) {
//            nodes.put("tag.money", "<green>[<white>Money<green>] ");
//        }
//    }
//}

// writeTemplate()
// NOTE: the following code also works but i disabled it as i couldn't have comments within the yml template
// except in the header. seems a tad arse to me.

//            try
//            {
//                this.read(yaml.load(data));
//            }
//            catch (ConfigurationException ex)
//            {
//                logMsg(Level.SEVERE, "Failed to write "+file.getName());
//                ex.printStackTrace();
//            }
//
//            this.save();