package io.github.superninjacat5.bridgeBattles;

import io.github.superninjacat5.bridgeBattles.basicCommands.*;
import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.github.superninjacat5.bridgeBattles.instances.lobby.Lobby;
import io.github.superninjacat5.bridgeBattles.parties.PartyCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class BridgeBattles extends JavaPlugin implements Listener {

    public InstanceManager instanceManager = new InstanceManager(this);

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

        BasicCommand gotoLobby = new GotoLobby(instanceManager);
        registerCommand("lobby", gotoLobby);

        new PartyCommand(this).register();

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
