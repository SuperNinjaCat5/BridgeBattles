package io.github.superninjacat5.bridgeBattles.instances;

import io.github.superninjacat5.bridgeBattles.WorldUtils;
import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.github.superninjacat5.bridgeBattles.instances.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InstanceManager {
    private final JavaPlugin plugin;

    private final Map<UUID, Arena> arenas = new HashMap<>();
    private final Map<UUID, Lobby> lobbies = new HashMap<>();

    private final Map<UUID, Instance> instancesByUID = new HashMap<>();

    public InstanceManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Instance getInstanceByWorld(World world) {
        if (world == null) return null;
        return instancesByUID.get(world.getUID());
    }

    public Map<UUID, Instance> getInstancesByUID() {
        return instancesByUID;
    }

    // Arenas

    public Map<UUID, Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(UUID uuid) {
        return arenas.get(uuid);
    }

    public Arena createArena(World world) {
        UUID uuid = UUID.randomUUID();
        Arena newArena = new Arena(uuid,world);

        arenas.put(uuid, newArena);
        instancesByUID.put(world.getUID(), newArena);
        return newArena;
    }

    public void addArena(UUID uuid, Arena arena) {
        this.arenas.put(uuid, arena);
        this.instancesByUID.put(arena.getWorld().getUID(), arena);
    }

    public void removeArena(UUID uuid) {
        this.instancesByUID.remove(arenas.get(uuid).getWorld().getUID());
        this.arenas.remove(uuid);
    }

    public void destroyArenaIfEmpty(Arena arena) {
        if (!arena.getPlayers().isEmpty()) return;

        World world = arena.getWorld();
        UUID arenaId = arena.getInstanceId();

        arenas.remove(arenaId);
        instancesByUID.remove(world.getUID());

        Bukkit.unloadWorld(world, false);

        Path worldPath = world.getWorldFolder().toPath();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (java.util.stream.Stream<Path> walk = java.nio.file.Files.walk(worldPath)) {
                walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                    try { java.nio.file.Files.delete(p); } catch (java.io.IOException ignored) {}
                });
            } catch (java.io.IOException e) {
                plugin.getLogger().warning("Failed to delete arena folder: " + worldPath + " — " + e.getMessage());
            }
        });
     }

    // Lobbies

    public Map<UUID, Lobby> getLobbies() {
        return lobbies;
    }

    public Lobby getFirstLobby() {
        return lobbies.values().stream().findFirst().orElse(null);
    }

    public Lobby getLobby(UUID uuid) {
        return lobbies.get(uuid);
    }

    public Lobby createLobby(World world) {
        UUID uuid = UUID.randomUUID();
        Lobby newLobby = new Lobby(uuid,world);

        lobbies.put(uuid, newLobby);
        instancesByUID.put(world.getUID(), newLobby);
        return newLobby;
    }

    public void addLobby(UUID uuid, Lobby lobby) {
        this.lobbies.put(uuid, lobby);
        this.instancesByUID.put(lobby.getWorld().getUID(), lobby);
    }

    public void removeLobby(UUID uuid) {
        this.instancesByUID.remove(lobbies.get(uuid).getWorld().getUID());
        this.lobbies.remove(uuid);
    }

}
