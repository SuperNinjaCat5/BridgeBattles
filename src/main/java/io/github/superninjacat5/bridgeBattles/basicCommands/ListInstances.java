package io.github.superninjacat5.bridgeBattles.basicCommands;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.stream.Collectors;

@NullMarked
public class ListInstances implements BasicCommand {
    private final InstanceManager instanceManager;
    public ListInstances(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (source.getExecutor() instanceof Player player) {
            String list = instanceManager.getInstancesByUID().values().stream()
                    .map(i -> "<gray>- <white>" + i.getClass().getSimpleName() + " <gray>(<white>" + i.getInstanceId() + "<gray>)")
                    .collect(Collectors.joining("\n"));

            player.sendMessage(MM.deserialize(
                    "<yellow>Active Instances:\n" + list
            ));
        } else {
            String list = instanceManager.getInstancesByUID().values().stream()
                    .map(i -> i.getClass().getSimpleName() + i.getInstanceId())
                    .collect(Collectors.joining("\n"));
            source.getSender().sendMessage("Instances By World UID: " + list);
        }
    }
}