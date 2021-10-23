package co.purevanilla.mcplugins.WaypointRegistry.data;

import co.purevanilla.mcplugins.WaypointRegistry.API;
import co.purevanilla.mcplugins.WaypointRegistry.ex.*;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Author {

    UUID uuid;

    public Author(UUID uuid){
        this.uuid=uuid;
    }

    public UUID getAuthorID() {
        return uuid;
    }

    public Author getAuthor() {
        return this;
    }

    public void addEntry(long x, long y, long z, String name, short dimension) throws InvalidEntryNameException, InvalidEntryException, DuplicatedEntryException, IOException {
        Entry entry = new Entry(this, UUID.randomUUID(),x,y,z,name, dimension);
        API.getInstance().addEntry(this,entry);
    }

    public Entry getEntry(UUID uuid) throws InvalidEntryException, UnknownEntryException {
        List<Entry> entries = new ArrayList<>();
        try {
            entries = this.getEntries();
        }  catch (UnknownAuthorException | IOException | InvalidConfigurationException e){
            // ignore
        }
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).getEntryId()==uuid){
                return entries.get(i);
            }
        }
        throw new UnknownEntryException("unknown entry");
    }

    public Entry getEntry(String name) throws InvalidEntryException, UnknownEntryException {
        List<Entry> entries = new ArrayList<>();
        try {
            entries = this.getEntries();
        }  catch (UnknownAuthorException | IOException | InvalidConfigurationException ex){
            // ignore
        }
        for (int i = 0; i < entries.size(); i++) {
            if(entries.get(i).name.toLowerCase().trim().equals(name.toLowerCase().trim())){
                return entries.get(i);
            }
        }
        throw new UnknownEntryException("unknown entry");
    }

    public List<Entry> getEntries() throws UnknownAuthorException, InvalidEntryException, IOException, InvalidConfigurationException {
        return API.getInstance().getAuthorEntries(this);
    }

}
