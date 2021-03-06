package co.purevanilla.mcplugins.WaypointRegistry;

import co.aikar.commands.PaperCommandManager;
import co.purevanilla.mcplugins.WaypointRegistry.cmd.WaypointCMD;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
        try {
            API.setPlugin(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new WaypointCMD(API.getInstance()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
