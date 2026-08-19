package io.github.superninjacat5.bridgeBattles.minigames.games;

import io.github.superninjacat5.bridgeBattles.MapManager;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.minigames.minigame;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public class FourPlayerBridge extends minigame {
    FourPlayerBridge(JavaPlugin plugin, InstanceManager instanceManager, MapManager mapManager) {
        super(plugin, instanceManager, mapManager);
    }

    @Override
    public void configMinigame() {
        globalSpawnLocation = new Location(arena.getWorld(), 0, 0, 0);
        World world = arena.getWorld();

        if (Objects.equals(map_name, "4P_BRIDGE_FFA_SPACE")) {
            world.setTime(18000);
        }
        world.setGameRule(GameRules.ADVANCE_TIME, false);

        world.setGameRule(GameRules.SPAWN_MOBS, false);

        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRules.KEEP_INVENTORY, true);
        world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
    }



}
