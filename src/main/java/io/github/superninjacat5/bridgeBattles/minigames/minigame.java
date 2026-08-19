package io.github.superninjacat5.bridgeBattles.minigames;

import io.github.superninjacat5.bridgeBattles.MapManager;
import io.github.superninjacat5.bridgeBattles.WorldUtils;
import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.github.superninjacat5.bridgeBattles.instances.arena.ArenaState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class minigame {
    public minigame(JavaPlugin plugin, InstanceManager instanceManager, MapManager mapManager) {

        this.plugin = plugin;
        this.instanceManager = instanceManager;
        this.mapManager = mapManager;
    }

    private final JavaPlugin plugin;

    private final InstanceManager instanceManager;
    private final MapManager mapManager;

    protected Arena arena;

    protected Location globalSpawnLocation = null;
    protected String map_name;
    protected List<Player> players = new ArrayList<>();

    private void createArena(String map_name) {
        Path mapPath = mapManager.getArenaTemplate(map_name);


        if (mapPath == null) return;

        String worldName = map_name + "_" + UUID.randomUUID();
        Path targetPath = Bukkit.getWorldContainer().toPath().resolve(worldName);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                WorldUtils.copyWorldFolder(mapPath, targetPath);
            } catch (IOException e) {
                System.err.println("[BridgeBattles] Failed to copy mapPathWorld to target path!");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                World world = new WorldCreator(worldName).createWorld();
                if (world == null) {
                    System.err.println("[BridgeBattles] Failed to create world from template!");
                    return;
                }

                arena = instanceManager.createArena(world);

            });

        });
    }

    public void setupMinigame(String map_name) {
        createArena(map_name);
        this.map_name = map_name;
        arena.setARENA_STATE(ArenaState.STARTING);

        globalSpawnLocation = new Location(arena.getWorld(), 0, 0, 0);

        configMinigame();

        arena.setARENA_STATE(ArenaState.WAITING);
    }

    public void configMinigame() {
        return;
    }

    public void startMinigame() {
        arena.setARENA_STATE(ArenaState.RUNNING);
    }

    public void endMinigame() {
        arena.setARENA_STATE(ArenaState.ENDING);
    }

    public void addPlayerToMinigameInstance(Player player) {

        players.add(player);
        arena.addPlayer(player);

        Instance originalInstance = instanceManager.getInstanceByWorld(player.getWorld());
        if (originalInstance != null) {
            originalInstance.removePlayer(player);
            if (originalInstance instanceof Arena oldArena) instanceManager.destroyArenaIfEmpty(oldArena);
        }

        player.teleport(globalSpawnLocation);
        arena.addPlayer(player);
    }
}
