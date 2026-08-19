package io.github.superninjacat5.bridgeBattles.parties;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PartyRegistry {
    private static final Map<Player, Party> partyOf = new HashMap<>();

    public static void register(Player player, Party party) {
        partyOf.put(player, party);
    }

    public static void unregister(Player player) {
        partyOf.remove(player);
    }

    public static Party getParty(Player player) {
        return partyOf.get(player);
    }
}
