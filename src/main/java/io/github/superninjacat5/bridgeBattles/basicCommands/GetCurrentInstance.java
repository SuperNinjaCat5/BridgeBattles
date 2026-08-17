package io.github.superninjacat5.bridgeBattles.basicCommands;

import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GetCurrentInstance implements BasicCommand {
    private final InstanceManager instanceManager;
    public GetCurrentInstance(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (source.getExecutor() instanceof Player player) {
            Instance instance = instanceManager.getInstanceByWorld(player.getWorld());

            if (instance == null) {
                player.sendMessage(MM.deserialize(
                        "<red>You're not in a managed instance."
                ));
                return;
            }

            String playerList = instance.getPlayers().isEmpty()
                    ? "none"
                    : String.join(", ", instance.getPlayers().keySet());

            player.sendMessage(MM.deserialize(
                    "<light_purple>Current Instance ---</light_purple>\n<aqua>Instance Type: <white><type>\n<aqua>Instance UUID: <white><uuid>\n<aqua>Player List: <white><players>",
                    net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("type", instance.getClass().getSimpleName()),
                    net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("uuid", instance.getInstanceId().toString()),
                    net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("players", playerList)
            ));
        } else {
            source.getSender().sendMessage("Can't run getInstance from the Console!!!!!!!!!");
        }
    }
}
