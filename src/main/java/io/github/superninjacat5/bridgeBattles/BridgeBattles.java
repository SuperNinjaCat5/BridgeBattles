package io.github.superninjacat5.bridgeBattles;

import io.github.superninjacat5.bridgeBattles.basicCommands.CreateInstanceOfMap;
import io.github.superninjacat5.bridgeBattles.basicCommands.CreateRandomInstance;
import io.github.superninjacat5.bridgeBattles.basicCommands.GetCurrentInstance;
import io.github.superninjacat5.bridgeBattles.basicCommands.ListInstances;
import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.lobby.Lobby;
import io.papermc.paper.command.brigadier.BasicCommand;
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

        MapManager mapManager = new MapManager(getDataFolder().toPath());

        BasicCommand currentInstance = new GetCurrentInstance(instanceManager);
        registerCommand("current_instance", currentInstance);

        BasicCommand listInstances = new ListInstances(instanceManager);
        registerCommand("list_all_instances", listInstances);

        BasicCommand createRandomInstance = new CreateRandomInstance(instanceManager);
        registerCommand("create_random_instance", createRandomInstance);

        BasicCommand createInstanceOfMap = new CreateInstanceOfMap(this, instanceManager, mapManager);
        registerCommand("create_instance_of_map", createInstanceOfMap);

        World spawnWorld = Bukkit.getWorlds().getFirst();
        instanceManager.createLobby(spawnWorld);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Instance instance = instanceManager.getInstanceByWorld(player.getWorld());

        if (instance == null) return;

        instance.addPlayer(player);

        if (!(instance instanceof Lobby)) return;

        getServer().sendMessage(Component.text("Welcome, " + event.getPlayer().getName() + "!"));
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
