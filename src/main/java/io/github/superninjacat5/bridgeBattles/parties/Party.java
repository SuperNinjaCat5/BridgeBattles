package io.github.superninjacat5.bridgeBattles.parties;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Party {
    private Player partyLeader;
    private final JavaPlugin plugin;

    private final List<Player> members = new ArrayList<>();
    private final List<Player> invitees = new ArrayList<>();

    private static final MiniMessage MM = MiniMessage.miniMessage();

    public Party(Player party_leader, JavaPlugin plugin) {
        this.partyLeader = party_leader;
        this.members.add(party_leader);
        PartyRegistry.register(party_leader, this);

        this.plugin = plugin;
    }

    public void inviteToParty(Player invitee) {
        invitees.add(invitee);

        invitee.sendMessage(MM.deserialize(
                "<aqua>" + partyLeader.getName() + " invited you to a party! <gray>Click to join:</gray> " +
                        "<click:run_command:'/party accept " + partyLeader.getName() + "'><white><u>[Accept]</u></white></click>"
        ));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            invitees.remove(invitee);
            if (invitee.isOnline()) invitee.sendMessage("§cYour party invite from " + partyLeader.getName() + " has expired.");
            if (partyLeader.isOnline()) partyLeader.sendMessage("§cYour Party invite to " + partyLeader.getName() + " has expired.");
        }, 1200L);
    }

    public void acceptInvite(Player acceptee) {
        if (invitees.contains(acceptee)) {

            members.add(acceptee);
            PartyRegistry.register(acceptee, this);

            for (Player member : members) {
                member.sendMessage(MM.deserialize("<blue><acceptee_name> joined the party!",
                        Placeholder.unparsed("acceptee_name", acceptee.getName())
                ));
            }
        } else {
            acceptee.sendMessage(MM.deserialize("<red>You have not been invited to this party!"));
        }
    }

    public void transferOwnership(Player ownershipee) {
        partyLeader = ownershipee;
    }

    public void disbandParty() {
        if (!members.isEmpty()) {
            for (Player member : members) {
                member.sendMessage(MM.deserialize("<gold>The party has been disbanded!"));
                PartyRegistry.unregister(member);
            }
        }

        members.clear();
        invitees.clear();
    }

    public void leaveParty(Player leavee) {

        if(leavee == partyLeader) {
            members.remove(leavee);
            PartyRegistry.unregister(leavee);

            if(!members.isEmpty()) {
                Player oldPartyLeader = partyLeader;
                partyLeader = members.getFirst();

                for (Player member : members) {
                    member.sendMessage(MM.deserialize("<aqua><bold><old_leader_name></bold> left the party, <bold><party_leader_name></bold> is the new leader!", Placeholder.unparsed("old_leader_name", oldPartyLeader.getName()), Placeholder.unparsed("party_leader_name",partyLeader.getName())));
                }

                return;

            } else disbandParty();
        }

        members.remove(leavee);
        PartyRegistry.unregister(leavee);

        for (Player member : members) {
            member.sendMessage(MM.deserialize("<gray><bold><leavee></bold> left the party!", Placeholder.unparsed("leavee", leavee.getName())));
        }
    }

    public Player getPartyLeader() {
        return partyLeader;
    }

    public boolean isInvitedToParty(Player player) {
        return invitees.contains(player);
    }

    public List<Player> getMembers() {
        return members;
    }

}
