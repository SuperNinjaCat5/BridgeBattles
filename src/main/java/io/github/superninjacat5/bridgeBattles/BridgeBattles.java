package io.github.superninjacat5.bridgeBattles;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.lobby.Lobby;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class BridgeBattles extends JavaPlugin implements Listener {

    public InstanceManager instanceManager = new InstanceManager();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        World spawnWorld = Bukkit.getWorlds().getFirst();
        instanceManager.createLobby(spawnWorld);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Instance instance = instanceManager.getInstanceByWorld(player.getWorld());

        if (instance == null) return;

        if (!(instance instanceof Lobby)) return;

        player.sendMessage(Component.text("Welcome, " + event.getPlayer().getName() + "!"));
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
