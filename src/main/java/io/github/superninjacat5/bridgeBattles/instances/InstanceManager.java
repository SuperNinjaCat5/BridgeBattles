package io.github.superninjacat5.bridgeBattles.instances;

import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.github.superninjacat5.bridgeBattles.instances.lobby.Lobby;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InstanceManager {
    private final Map<UUID, Arena> arenas = new HashMap<>();
    private final Map<UUID, Lobby> lobbies = new HashMap<>();

    private final Map<UUID, Instance> instancesByUID = new HashMap<>();

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

    // Lobbies

    public Map<UUID, Lobby> getLobbies() {
        return lobbies;
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
