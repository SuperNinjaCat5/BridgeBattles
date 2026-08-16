package io.github.superninjacat5.bridgeBattles.instances.arena;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Arena extends Instance {
    public Arena(UUID instance_id, World world) {
        super(instance_id, world);
        ARENA_STATE = ArenaState.STARTING;
    }

    private ArenaState ARENA_STATE;

    public ArenaState getARENA_STATE() {
        return ARENA_STATE;
    }

    public void setARENA_STATE(ArenaState arenaState) {
        ARENA_STATE = arenaState;
    }

}
