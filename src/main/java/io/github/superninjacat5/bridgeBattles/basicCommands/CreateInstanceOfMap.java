package io.github.superninjacat5.bridgeBattles.basicCommands;

import io.github.superninjacat5.bridgeBattles.MapManager;
import io.github.superninjacat5.bridgeBattles.WorldUtils;
import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;

@NullMarked
public class CreateInstanceOfMap implements BasicCommand {
    private final JavaPlugin plugin;
    private final InstanceManager instanceManager;
    private final MapManager mapManager;
    public CreateInstanceOfMap(JavaPlugin plugin, InstanceManager instanceManager, MapManager mapManager) {
        this.plugin = plugin;
        this.instanceManager = instanceManager;
        this.mapManager = mapManager;
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage("Players only.");
            return;
        }

        if (args.length < 1) {
            player.sendMessage(MM.deserialize("<red>Usage: /create_instance_of_map <template_name>"));
            return;
        }

        Path templateWorldPath = mapManager.getArenaTemplate(args[0]);
        if (templateWorldPath == null) {
            player.sendMessage(MM.deserialize(
                    "<red>No arena template named '<white><name></white>' exists.",
                    net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("name", args[0])
            ));
            return;
        }

        String instanceName = "arena_" + UUID.randomUUID();
        Path targetPath = Bukkit.getWorldContainer().toPath().resolve(instanceName);

        player.sendMessage(MM.deserialize("<yellow>Creating instance, please wait..."));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                WorldUtils.copyWorldFolder(templateWorldPath, targetPath);
            } catch (IOException e) {
                Bukkit.getScheduler().runTask(plugin, () ->
                        player.sendMessage(MM.deserialize(
                                "<red>Failed to copy map template: <white><error>",
                                net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("error", String.valueOf(e.getMessage()))
                        ))
                );
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                World world = new WorldCreator(instanceName).createWorld();
                if (world == null) {
                    player.sendMessage(MM.deserialize("<red>Failed to load the copied world."));
                    return;
                }

                Instance newInstance = instanceManager.createArena(world);

                Instance originalInstance = instanceManager.getInstanceByWorld(player.getWorld());
                if (originalInstance != null) {
                    originalInstance.removePlayer(player);
                }

                player.teleport(newInstance.getWorld().getSpawnLocation());
                newInstance.addPlayer(player);

                player.sendMessage(MM.deserialize("<green>Instance created!"));
            });
        });
    }
}
