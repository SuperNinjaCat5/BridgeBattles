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
        return newArena;
    }

    public void addArena(UUID uuid, Arena arena) {
        this.arenas.put(uuid, arena);
    }

    public void removeArena(UUID uuid) {
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
        return newLobby;
    }

    public void addLobby(UUID uuid, Lobby lobby) {
        this.lobbies.put(uuid, lobby);
    }

    public void removeLobby(UUID uuid) {
        this.lobbies.remove(uuid);
    }

}
