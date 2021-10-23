package co.purevanilla.mcplugins.WaypointRegistry;

import co.purevanilla.mcplugins.WaypointRegistry.data.Author;
import co.purevanilla.mcplugins.WaypointRegistry.data.Entry;
import co.purevanilla.mcplugins.WaypointRegistry.ex.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class API {

    private static Plugin plugin;
    private static FileConfiguration data;
    private static File dataFile;

    static void setPlugin(Plugin plugin) throws IOException, InvalidConfigurationException {
        API.plugin=plugin;
        API.data=plugin.getConfig();

        API.dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!API.dataFile.exists()) {
            API.dataFile.getParentFile().mkdirs();
            API.plugin.saveResource("data.yml", false);
        }

        API.data= new YamlConfiguration();
        API.data.load(API.dataFile);
    }

    public static API getInstance(){
        return new API();
    }

    public void save() throws IOException {
        API.data.save(API.dataFile);
    }

    public Author getAuthor(UUID uuid) {
        return new Author(uuid);
    }

    public String getServerName(){
        return API.plugin.getConfig().getString("config.serverName");
    }

    public String getPrimaryColor(){
        return API.plugin.getConfig().getString("config.primaryColor");
    }

    public String getTitle(){
        return API.plugin.getConfig().getString("config.title");
    }

    public String getMenuFormat(){
        return API.plugin.getConfig().getString("config.menuFormat");
    }

    public Author createAuthor(UUID uuid) throws IOException {
        String path = "data."+uuid.toString();
        List<String> entries = new ArrayList<>();
        API.data.set(path, entries.toArray());
        this.save();
        return new Author(uuid);
    }

    public List<Entry> getAuthorEntries(Author author) throws InvalidEntryException, IOException, InvalidConfigurationException {
        API.data.load(API.dataFile);
        List<Entry> entries = new ArrayList<>();
        String path = author.getAuthorID().toString().replaceAll("-+","");
        List<String> entriesRawData = API.data.getStringList(path);
        for (String entry:entriesRawData) {
            entries.add(new Entry(author, entry));
        }
        return entries;
    }

    public void addEntry(Author author, Entry entry) throws InvalidEntryException, DuplicatedEntryException, IOException {
        List<Entry> entries = new ArrayList<>();
        try {
            entries = author.getEntries();
        } catch (UnknownAuthorException | InvalidConfigurationException e) {
            // ignore
        }
        for (Entry savedEntry:entries) {
            if(entry.name.toLowerCase().trim().equals(savedEntry.name.toLowerCase())){
                throw new DuplicatedEntryException("you have an entry with the same name already, please, use a different name");
            }
        }
        entries.add(entry);
        updateAuthorEntries(author,entries);
    }

    public void renameEntry(Entry entry, String name) throws UnknownAuthorException, InvalidEntryException, InvalidEntryNameException, UnknownEntryException, DuplicatedEntryException, IOException, InvalidConfigurationException {
        List<Entry> entries = entry.getAuthor().getEntries();

        for (Entry savedEntry:entries) {
            if(savedEntry.name.toLowerCase().trim().equals(name.toLowerCase())){
                throw new DuplicatedEntryException("you have an entry with the same name already, please, use a different name");
            }
        }

        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getEntryId().equals(entry.uuid)){
                entries.get(i).setName(name);
                this.updateAuthorEntries(entry.getAuthor(),entries);
                return;
            }
        }
        throw new UnknownEntryException("unknown entry with uuid " + entry.getEntryId().toString());
    }

    public void deleteEntry(Entry entry) throws UnknownAuthorException, InvalidEntryException, InvalidEntryNameException, UnknownEntryException, IOException, InvalidConfigurationException {
        List<Entry> entries = entry.getAuthor().getEntries();
        List<Entry> finalEntries = new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < entries.size(); i++) {
            if(!entries.get(i).getEntryId().equals(entry.uuid)){
                finalEntries.add(entries.get(i));
            } else {
                found = true;
            }
        }
        if(!found){
            throw new UnknownEntryException("unknown entry with uuid " + entry.getEntryId().toString());
        } else {
            this.updateAuthorEntries(entry.getAuthor(),finalEntries);
        }
    }

    public void updateAuthorEntries(Author author, List<Entry> entries) throws IOException {
        List<String> rawData = new ArrayList<>();
        String path = author.getAuthorID().toString().replaceAll("-+","");
        for (Entry entry:entries) {
            rawData.add(entry.asRawData());
        }
        API.data.set(path,rawData.toArray());
        this.save();
    }


}
