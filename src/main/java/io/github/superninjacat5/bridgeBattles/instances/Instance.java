package io.github.superninjacat5.bridgeBattles.instances;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Instance {
    public Instance(UUID instance_id, World world) {
        this.INSTANCE_ID = instance_id;
        this.world = world;
        this.players = new HashMap<>();
    }

    private UUID INSTANCE_ID = null;

    private World world = null;
    private Map<String, Player> players = new HashMap<>();

    public UUID getInstanceId() {
        return INSTANCE_ID;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getName(), player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getName());
    }

}
