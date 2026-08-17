package io.github.superninjacat5.bridgeBattles.basicCommands;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.UUID;

@NullMarked
public class GotoLobby implements BasicCommand {
    private final InstanceManager instanceManager;
    public GotoLobby(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (source.getExecutor() instanceof Player player) {
            Instance originalInstance = instanceManager.getInstanceByWorld(player.getWorld());
            Instance lobbyInstance = instanceManager.getFirstLobby(); // CHANGE THIS if you add more complex lobby stuff

            originalInstance.removePlayer(player);

            if (originalInstance instanceof Arena arena) instanceManager.destroyArenaIfEmpty(arena);

            player.teleport(lobbyInstance.getWorld().getSpawnLocation());
            lobbyInstance.addPlayer(player);
        }
    }
}
