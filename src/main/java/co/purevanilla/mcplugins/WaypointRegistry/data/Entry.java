package co.purevanilla.mcplugins.WaypointRegistry.data;

import co.purevanilla.mcplugins.WaypointRegistry.API;
import co.purevanilla.mcplugins.WaypointRegistry.ex.*;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.UUID;

public class Entry extends Author {

    public UUID uuid;
    public long x;
    public long y;
    public long z;
    public String name;
    public short dimension;

    public Entry(Author author, String string) throws InvalidEntryException {
        super(author.uuid);

        // uuid;x;y;z;name
        String[] entryParts = string.split(";");
        if(entryParts.length!=6){
            throw new InvalidEntryException("invalid entry, the part count was different than expected");
        } else {
            this.uuid=UUID.fromString(entryParts[0]);
            this.x=Long.parseLong(entryParts[1]);
            this.y=Long.parseLong(entryParts[2]);
            this.z=Long.parseLong(entryParts[3]);
            this.name=Entry.nameSanitize(entryParts[4]);
            this.dimension=Short.parseShort(entryParts[5]);
        }

    }

    public void setName(String name) throws InvalidEntryNameException {
        if(Entry.isValidName(name)){
            throw new InvalidEntryNameException("checkpoint names should be 1-32 characters long");
        } else {
            this.name=name;
        }
    }

    public static boolean isValidName(String name){
        return name.length() <= 14 && name.length() >= 1 && !name.contains(";");
    }

    public static String nameSanitize(String name){
        return name.replaceAll(";+", "");
    }

    public String asRawData(){
        return this.uuid.toString()+";"+ String.valueOf(this.x) +";"+String.valueOf(this.y)+";"+String.valueOf(this.z)+";"+this.name+";"+String.valueOf(this.dimension);
    }

    public UUID getEntryId(){
        return this.uuid;
    }

    public void delete() throws InvalidEntryNameException, UnknownAuthorException, InvalidEntryException, UnknownEntryException, IOException, InvalidConfigurationException {
        API.getInstance().deleteEntry(this);
    }

    public void rename(String name) throws InvalidEntryNameException, UnknownAuthorException, InvalidEntryException, UnknownEntryException, DuplicatedEntryException, IOException, InvalidConfigurationException {
        name=Entry.nameSanitize(name);
        this.setName(name);
        API.getInstance().renameEntry(this,name);
    }

    public Entry(Author author, UUID uuid, long x, long y, long z, String name, short dimension) throws InvalidEntryNameException {
        super(author.uuid);

        name=Entry.nameSanitize(name);

        this.uuid=uuid;
        this.x=x;
        this.y=y;
        this.z=z;
        if(!Entry.isValidName(name)){
            throw new InvalidEntryNameException("checkpoint names must be between 1-32 and can't contain \";\"");
        } else {
            this.name=name;
        }
        this.dimension=dimension;
    }

}
