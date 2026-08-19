package io.github.superninjacat5.bridgeBattles.parties;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Bed;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.cert.CertPath;

public class PartyCommand {

    private final JavaPlugin plugin;

    public PartyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();


    @FunctionalInterface
    public interface Command<S> {
        int SINGLE_SUCCESS = 1;

        int run(CommandContext<S> ctx) throws CommandSyntaxException;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            LiteralArgumentBuilder<CommandSourceStack> partyCommand = Commands.literal("party")
                    .then(Commands.literal("create").executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        if (!(executor instanceof Player creator)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        if (PartyRegistry.getParty(creator) != null) {
                            creator.sendMessage(MM.deserialize("<red>You are already in a party!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        creator.sendMessage(MM.deserialize("<gold><bold>Party Created!</bold></gold>"));
                        creator.playSound(creator.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0F, 1.0F);

                        Party creatorParty = new Party(creator, plugin);
                        return Command.SINGLE_SUCCESS;
                    }))
                    .then(Commands.literal("invite").then(Commands.argument("invitee", ArgumentTypes.players()).executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            Entity executor = ctx.getSource().getExecutor();
                            Player invitee = ctx.getArgument("invitee", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

                            if (!(executor instanceof Player inviter)) {
                                sender.sendPlainMessage("Parties are for kids robot scum!");
                                return Command.SINGLE_SUCCESS;
                            }

                            if (inviter == invitee) {
                                inviter.sendMessage(MM.deserialize("<red>You can't invite yourself to a party stupid!"));
                                return Command.SINGLE_SUCCESS;
                            }

                            Party inviterParty = PartyRegistry.getParty(inviter);

                            if (inviterParty == null) {
                                inviter.sendMessage(MM.deserialize("<red>You are not in a party!<dark_grey> Run [ /party create ]"));
                                return Command.SINGLE_SUCCESS;
                            }

                            if (inviterParty.getPartyLeader() != inviter) {
                                inviter.sendMessage(MM.deserialize("<red>You aren't a party leader!"));
                                return Command.SINGLE_SUCCESS;
                            }

                            inviterParty.inviteToParty(invitee);

                            inviter.sendMessage(MM.deserialize("<aqua>Invited <bold><invitee_name></bold> to the party!",
                                    Placeholder.unparsed("invitee_name", invitee.getName())));

                            return Command.SINGLE_SUCCESS;
                        })))
                    .then(Commands.literal("accept").then(Commands.argument("inviter", ArgumentTypes.player()).executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();
                        Player inviter = ctx.getArgument("inviter", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

                        if (!(executor instanceof Player accepter)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        Party inviterParty = PartyRegistry.getParty(inviter);

                        if (PartyRegistry.getParty(accepter) != null) {
                            accepter.sendMessage(MM.deserialize("<red>You are already in a party! Leave it to join a different one!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        if (inviterParty == null) {
                            accepter.sendMessage(MM.deserialize("<red><inviter_name>'s party no longer exists!",
                                    net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("inviter_name", inviter.getName())
                            ));
                            return Command.SINGLE_SUCCESS;
                        }

                        inviterParty.acceptInvite(accepter);
                        return Command.SINGLE_SUCCESS;
                    })))
                    .then(Commands.literal("kick").then(Commands.argument("kickee", ArgumentTypes.player()).executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();
                        Player kickee = ctx.getArgument("kickee", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

                        if (!(executor instanceof Player kicker)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        Party kickerParty = PartyRegistry.getParty(kicker);

                        if (kickerParty == null) {
                            kicker.sendMessage(MM.deserialize("<red>You are not in a party!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        if (kickerParty.getPartyLeader() != kicker) {
                            kicker.sendMessage(MM.deserialize("<red>You are not the party leader!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        if (PartyRegistry.getParty(kickee) != kickerParty) {
                            kicker.sendMessage(MM.deserialize("<red><kickee_name> is not in your party!",
                                    Placeholder.unparsed("kickee_name", kickee.getName())));
                            return Command.SINGLE_SUCCESS;
                        }

                        if (kickee == kicker) {
                            kicker.sendMessage(MM.deserialize("<red>You can't kick yourself! Use /party leave."));
                            return Command.SINGLE_SUCCESS;
                        }

                        kickerParty.leaveParty(kickee);
                        kickee.sendMessage(MM.deserialize("<red>You were kicked from the party."));
                        kicker.sendMessage(MM.deserialize("You kicked<dark_gray><bold><kickee_name></bold>.",
                                Placeholder.unparsed("kickee_name", kickee.getName())));

                        return Command.SINGLE_SUCCESS;

                    })))
                    .then(Commands.literal("leave").executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        if (!(executor instanceof Player leaver)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        Party leaverParty = PartyRegistry.getParty(leaver);

                        if(leaverParty == null) {
                            leaver.sendMessage(MM.deserialize("<red>You are not in a party!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        leaverParty.leaveParty(leaver);
                        leaver.sendMessage(MM.deserialize("<gray>You left the party."));
                        return Command.SINGLE_SUCCESS;
                    }))
                    .then(Commands.literal("disband").executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        if (!(executor instanceof Player disbander)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        Party disbanderParty = PartyRegistry.getParty(disbander);

                        if (disbanderParty == null) {
                            disbander.sendMessage(MM.deserialize("<red>You are not in a party!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        if (disbanderParty.getPartyLeader() != disbander) {
                            disbander.sendMessage(MM.deserialize("<red>You are not the party leader!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        disbanderParty.disbandParty();
                        return Command.SINGLE_SUCCESS;
                    }))
                    .then(Commands.literal("transfer")
                    .then(Commands.argument("newLeader", ArgumentTypes.player())
                            .executes(ctx -> {
                                CommandSender sender = ctx.getSource().getSender();
                                Entity executor = ctx.getSource().getExecutor();
                                Player newLeader = ctx.getArgument("newLeader", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();


                                if (!(executor instanceof Player transferer)) {
                                    sender.sendPlainMessage("Parties are for kids robot scum!");
                                    return Command.SINGLE_SUCCESS;
                                }

                                Party transfererParty = PartyRegistry.getParty(transferer);

                                if (transferer != transfererParty.getPartyLeader()) {
                                    transferer.sendMessage(MM.deserialize("<red>You are not the party leader!"));
                                    return Command.SINGLE_SUCCESS;
                                }

                                if (PartyRegistry.getParty(newLeader) == null) {
                                    transferer.sendMessage(MM.deserialize("<red><transferee_name> is not a party!",
                                            net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("transferee_name", newLeader.getName())));
                                    return Command.SINGLE_SUCCESS;
                                }

                                if (PartyRegistry.getParty(newLeader) != transfererParty) {
                                    transferer.sendMessage(MM.deserialize("<red><transferee_name> is not in your party!",
                                            net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("transferee_name", newLeader.getName())));
                                    return Command.SINGLE_SUCCESS;
                                }

                                transfererParty.transferOwnership(newLeader);

                                for (Player member : transfererParty.getMembers()) {
                                    member.sendMessage(MM.deserialize(
                                            "<aqua><bold><new_leader_name></bold> is now the party leader!",
                                            Placeholder.unparsed("new_leader_name", newLeader.getName())
                                    ));
                                }

                                return Command.SINGLE_SUCCESS;
                    })))
                    .then(Commands.literal("list").executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();

                        if (!(executor instanceof Player lister)) {
                            sender.sendPlainMessage("Parties are for kids robot scum!");
                            return Command.SINGLE_SUCCESS;
                        }

                        Party listerParty = PartyRegistry.getParty(lister);

                        if (listerParty == null) {
                            lister.sendMessage(MM.deserialize("<red>You are not in a party!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        StringBuilder stringBuilder = new StringBuilder();

                        stringBuilder.append("<gold>--- <bold>Party List</bold> ---\n");
                        stringBuilder.append("<gray>Leader: <white><leader_name>\n");
                        stringBuilder.append("<gray>Members: \n");

                        for (Player member : listerParty.getMembers()) {

                            if (listerParty.getMembers().size() == 1) {
                                stringBuilder.append("<gray>No members!");
                                break;
                            }

                            if (member.equals(listerParty.getPartyLeader())) continue;

                            stringBuilder.append("<gray>- <white>").append(member.getName()).append("\n");
                        }

                        lister.sendMessage(MM.deserialize(stringBuilder.toString(), Placeholder.unparsed("leader_name", listerParty.getPartyLeader().getName())));

                        return Command.SINGLE_SUCCESS;
                    }));
            commands.register(partyCommand.build(), "Party Commands", java.util.List.of("p"));
        });
    }

}
