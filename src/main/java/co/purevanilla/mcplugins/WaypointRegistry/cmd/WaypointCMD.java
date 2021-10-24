package co.purevanilla.mcplugins.WaypointRegistry.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.purevanilla.mcplugins.WaypointRegistry.API;
import co.purevanilla.mcplugins.WaypointRegistry.data.Author;
import co.purevanilla.mcplugins.WaypointRegistry.data.Entry;
import co.purevanilla.mcplugins.WaypointRegistry.ex.*;
import co.purevanilla.mcplugins.WaypointRegistry.util.BookGeneration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.BookUtil;

import java.io.IOException;
import java.util.List;

@CommandAlias("waypoint|waypoints|wp")
public class WaypointCMD extends BaseCommand {

    API api;

    public WaypointCMD(API api){
        this.api=api;
    }

    @CatchUnknown
    public void unknown(Player player, String[] args){
        api.sendMessage(player, API.Message.UNKNOWN_COMMAND);
    }

    @Subcommand("list")
    @Default
    @CommandPermission("waypoint.use")
    public void onList(Player player, String[] args) {
        try {
            List<Entry> entryList = this.api.getAuthor(player.getUniqueId()).getEntries();
            if(api.useSounds()){
                api.playOpenSound(player);
            }
            BookUtil.openPlayer(player, BookGeneration.getWaypointBook(player,entryList));
        } catch (UnknownAuthorException e) {
            api.sendMessage(player, API.Message.NO_ENTRIES);
        } catch (InvalidEntryException | InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            api.sendMessage(player, API.Message.INTERNAL_ERROR);
        }
    }

    @Subcommand("add")
    @CommandPermission("waypoint.use")
    public void onAdd(Player player, String[] args){
        Author author = this.api.getAuthor(player.getUniqueId());
        if(args.length==1||args.length==4){
            short dim;
            Location location = player.getLocation();
            switch (location.getWorld().getEnvironment()){
                case NORMAL -> dim=0;
                case NETHER -> dim=1;
                case THE_END -> dim=2;
                default -> dim=-1;
            }
            long x = (long) location.getX();
            long y = (long) location.getY();
            long z = (long) location.getZ();
            if(args.length==4){
                x=Long.parseLong(args[1]);
                y=Long.parseLong(args[2]);
                z=Long.parseLong(args[3]);
            }
            try {
                author.addEntry(x,y,z,args[0], dim);
                api.sendMessage(player, API.Message.ADDED);
            } catch (InvalidEntryNameException e) {
                api.sendMessage(player, API.Message.INVALID_NAME);
            } catch (InvalidEntryException | IOException e) {
                e.printStackTrace();
                api.sendMessage(player, API.Message.INTERNAL_ERROR);
            } catch (DuplicatedEntryException e) {
                api.sendMessage(player, API.Message.ALREADY_ADDED);
            }
        } else {
            api.sendMessage(player, API.Message.ADD_USAGE);
        }
    }

    @Subcommand("rename")
    @CommandPermission("waypoint.use")
    public void onRename(Player player, String[] args){
        if(args.length==2){
            try {
                Author author = this.api.getAuthor(player.getUniqueId());
                author.getEntry(args[0]).rename(args[1]);
                api.sendMessage(player, API.Message.RENAMED);
            } catch (InvalidEntryNameException e) {
                api.sendMessage(player, API.Message.INVALID_NAME);
            } catch (UnknownAuthorException e) {
                api.sendMessage(player, API.Message.NO_ENTRIES);
            } catch (InvalidEntryException | IOException | InvalidConfigurationException e) {
                e.printStackTrace();
                api.sendMessage(player, API.Message.INTERNAL_ERROR);
            } catch (UnknownEntryException e) {
                api.sendMessage(player, API.Message.UNKNOWN_ITEM);
            } catch (DuplicatedEntryException e) {
                api.sendMessage(player, API.Message.ALREADY_ADDED);
            }
        } else {
            api.sendMessage(player, API.Message.RENAME_USAGE);
        }
    }

    @Subcommand("delete")
    @CommandPermission("waypoint.use")
    public void onDelete(Player player, String[] args) {
        if (args.length == 1) {
            try {
                Author author = this.api.getAuthor(player.getUniqueId());
                author.getEntry(args[0]).delete();
                api.sendMessage(player, API.Message.REMOVED);
            } catch (InvalidEntryNameException e) {
                api.sendMessage(player, API.Message.INVALID_NAME);
            } catch (UnknownAuthorException e) {
                api.sendMessage(player, API.Message.NO_ENTRIES);
            } catch (InvalidEntryException | IOException | InvalidConfigurationException e) {
                e.printStackTrace();
                api.sendMessage(player, API.Message.INTERNAL_ERROR);
            } catch (UnknownEntryException e) {
                api.sendMessage(player, API.Message.UNKNOWN_ITEM);
            }
        } else {
            api.sendMessage(player, API.Message.DELETE_USAGE);
        }
    }

    @Subcommand("export")
    @CommandPermission("waypoint.export")
    public void onExport(Player player, String[] args) {
        try {
            Author author = this.api.getAuthor(player.getUniqueId());
            if(player.getInventory().getItemInMainHand().getType() == Material.WRITABLE_BOOK){
                List<Entry> entryList = author.getEntries();
                ItemStack book = BookGeneration.getWaypointBook(player,entryList,false);
                player.getInventory().setItemInMainHand(book);
                api.sendMessage(player, API.Message.EXPORTED);
            } else {
                api.sendMessage(player, API.Message.HOLD_BOOK_AND_QUILL);
            }
        } catch (UnknownAuthorException e) {
            api.sendMessage(player, API.Message.NO_ENTRIES);
        } catch (InvalidEntryException | InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            api.sendMessage(player, API.Message.INTERNAL_ERROR);
        }
    }
}
