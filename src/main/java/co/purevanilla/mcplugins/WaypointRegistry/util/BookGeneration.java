package co.purevanilla.mcplugins.WaypointRegistry.util;

import co.purevanilla.mcplugins.WaypointRegistry.API;
import co.purevanilla.mcplugins.WaypointRegistry.data.Entry;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;

public class BookGeneration {

    public static ItemStack getWaypointBook(Player player, List<Entry> waypoints) {
        API api = API.getInstance();
        List<BaseComponent[]> pages = new ArrayList<>();
        BookUtil.PageBuilder currentPage = new BookUtil.PageBuilder();

        // top menu

        if(api.getServerName().length()<25){
            for (int i = 0; i < (25-api.getServerName().length())/2; i++) {
                currentPage.add(" ");
            }
        }

        currentPage
                .add(net.md_5.bungee.api.ChatColor.GREEN + api.getServerName())
                .newLine()
                .add("  ")
                .add(BookUtil.TextBuilder.of("[how to add entry]").color(ChatColor.DARK_GRAY).onHover(BookUtil.HoverAction.showText("use /waypoint add <name> [x] [y] [z]")).build())
                .newLine()
                .newLine();

        // content build

        if(waypoints.size()<=0){
            currentPage.add(BookUtil.TextBuilder.of("You've got no waypoints!").color(ChatColor.RED).build())
                    .newLine()
                    .add("Start creating waypoints by executing /waypoint add <name>. A waypoint will be added saving your exact position.")
                    .newLine()
                    .add("You can also use /waypoint add <name> [x] [y] [z]");
            pages.add(currentPage.build());
        } else {
            for (int i = 0; i < waypoints.size(); i++) {
                Entry entry = waypoints.get(i);
                currentPage
                        .add(BookUtil.TextBuilder.of(entry.name+" ").color(ChatColor.DARK_GRAY).style(ChatColor.BOLD).style(ChatColor.ITALIC).build())
                        .add(BookUtil.TextBuilder.of("[del]").color(ChatColor.GRAY).onHover(BookUtil.HoverAction.showText("delete entry")).onClick(BookUtil.ClickAction.runCommand("/waypointregistry:waypoint delete " + entry.name)).build())
                        .add(BookUtil.TextBuilder.of(" ").build())
                        .add(BookUtil.TextBuilder.of("[...]").color(ChatColor.GRAY).onHover(BookUtil.HoverAction.showText("show info and more options")).onClick(BookUtil.ClickAction.runCommand("/waypointregistry:waypoint info " + entry.name)).build())
                        .newLine()
                        .add(BookUtil.TextBuilder.of("X ").color(ChatColor.DARK_GRAY).style(ChatColor.BOLD).build())
                        .add(String.valueOf(entry.x))
                        .add(BookUtil.TextBuilder.of(" Y ").color(ChatColor.DARK_GRAY).style(ChatColor.BOLD).build())
                        .add(String.valueOf(entry.y))
                        .add(BookUtil.TextBuilder.of(" Z ").color(ChatColor.DARK_GRAY).style(ChatColor.BOLD).build())
                        .add(String.valueOf(entry.z))
                        .newLine()
                        .newLine();

                if((i <=2 && i % 2 == 0 && i>0) || (i>3 && (i-3) % 3 == 0) || i == waypoints.size()-1){
                    pages.add(currentPage.build());
                    currentPage = new BookUtil.PageBuilder();
                }
            }
        }
        return BookUtil.writtenBook()
                .author(api.getServerName())
                .title(player.getName()+"'s Waypoints")
                .pages(pages)
                .build();
    }

}
