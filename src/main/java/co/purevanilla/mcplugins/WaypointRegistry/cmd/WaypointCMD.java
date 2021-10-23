package co.purevanilla.mcplugins.WaypointRegistry.cmd;

import co.purevanilla.mcplugins.WaypointRegistry.API;
import co.purevanilla.mcplugins.WaypointRegistry.data.Author;
import co.purevanilla.mcplugins.WaypointRegistry.data.Entry;
import co.purevanilla.mcplugins.WaypointRegistry.ex.*;
import co.purevanilla.mcplugins.WaypointRegistry.util.BookGeneration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.upperlevel.spigot.book.BookUtil;

import java.io.IOException;
import java.util.List;

public class WaypointCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        } else {
            boolean list = false;
            Author author;
            API api = API.getInstance();
            author = api.getAuthor(((Player) sender).getUniqueId());
            Player player = (Player) sender;
            if(args.length>0){
                if(args[0].toLowerCase().trim().equals("add")){
                    if(args.length==2){
                        short dim;
                        Location location = player.getLocation();
                        switch (location.getWorld().getEnvironment()){
                            case NORMAL -> dim=0;
                            case NETHER -> dim=1;
                            case THE_END -> dim=2;
                            default -> dim=-1;
                        }
                        try {
                            author.addEntry((long) location.getX(),(long) location.getY(),(long) location.getZ(),args[1], dim);
                            player.sendMessage("added");
                        } catch (InvalidEntryNameException e) {
                            e.printStackTrace();
                            player.sendMessage(e.getMessage());
                        } catch (InvalidEntryException | IOException e) {
                            e.printStackTrace();
                            player.sendMessage("internal error");
                        } catch (DuplicatedEntryException e) {
                            player.sendMessage(e.getMessage());
                        }
                    } else {
                        player.sendMessage("usage: \"/waypoints add <name>\"");
                    }
                } else if (args[0].toLowerCase().trim().equals("rename")) {
                    if(args.length==3){
                        try {
                            author.getEntry(args[1]).rename(args[2]);
                            player.sendMessage("renamed");
                        } catch (InvalidEntryNameException e) {
                            player.sendMessage("invalid name provided");
                        } catch (UnknownAuthorException e) {
                            player.sendMessage("you have no entries");
                        } catch (InvalidEntryException | IOException | InvalidConfigurationException e) {
                            e.printStackTrace();
                            player.sendMessage("internal error");
                        } catch (UnknownEntryException e) {
                            player.sendMessage("no entries with that name");
                        } catch (DuplicatedEntryException e) {
                            player.sendMessage("you have an entry with the same name already");
                        }
                    } else {
                        player.sendMessage("usage: \"/waypoints rename <name> <new-name>\"");
                    }
                } else if (args[0].toLowerCase().trim().equals("delete")) {
                    if (args.length == 2) {
                        try {
                            author.getEntry(args[1]).delete();
                            player.sendMessage("deleted");
                        } catch (InvalidEntryNameException e) {
                            player.sendMessage("invalid name provided");
                        } catch (UnknownAuthorException e) {
                            player.sendMessage("you have no entries");
                        } catch (InvalidEntryException | IOException | InvalidConfigurationException e) {
                            e.printStackTrace();
                            player.sendMessage("internal error");
                        } catch (UnknownEntryException e) {
                            player.sendMessage("no entries with that name");
                        }
                    } else {
                        player.sendMessage("usage: \"/waypoints delete <name>\"");
                    }
                } else if(args[0].toLowerCase().trim().equals("list")){
                    list=true;
                } else {
                    player.sendMessage("unknown command");
                }
            } else {
                list=true;
            }
            if(list){
                try {
                    List<Entry> entryList = author.getEntries();
                    BookUtil.openPlayer(player, BookGeneration.getWaypointBook(player,entryList));
                } catch (UnknownAuthorException e) {
                    player.sendMessage("you have no entries");
                } catch (InvalidEntryException | InvalidConfigurationException | IOException e) {
                    e.printStackTrace();
                    player.sendMessage("internal error");
                }
            }
        }
        return true;
    }
}
