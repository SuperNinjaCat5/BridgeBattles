package io.github.superninjacat5.bridgeBattles.basicCommands;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.UUID;

@NullMarked
public class CreateRandomInstance implements BasicCommand {
    private final InstanceManager instanceManager;
    public CreateRandomInstance(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        WorldCreator worldCreator = new WorldCreator("randomInstanceCreator_" + UUID.randomUUID());

        Random random = new Random();
        worldCreator.seed(random.nextLong());
        World world = worldCreator.createWorld();

        Instance newInstance = instanceManager.createArena(world);

        if (source.getExecutor() instanceof Player player) {
            Instance originalInstance = instanceManager.getInstanceByWorld(player.getWorld());

            originalInstance.removePlayer(player);
            player.teleport(newInstance.getWorld().getSpawnLocation());
            newInstance.addPlayer(player);
        }
    }
}
