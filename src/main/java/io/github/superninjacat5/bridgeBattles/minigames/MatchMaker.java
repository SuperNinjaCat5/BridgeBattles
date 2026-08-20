package io.github.superninjacat5.bridgeBattles.minigames;

import io.github.superninjacat5.bridgeBattles.MapManager;
import io.github.superninjacat5.bridgeBattles.instances.Instance;
import io.github.superninjacat5.bridgeBattles.instances.InstanceManager;
import io.github.superninjacat5.bridgeBattles.instances.arena.Arena;
import io.github.superninjacat5.bridgeBattles.parties.Party;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchMaker {
    private Map<Player, Class<? extends Minigame>> playerQueue = new HashMap<>();
    private Map<Party, Class<? extends Minigame>> partyQueue = new HashMap<>();

    private final JavaPlugin plugin;
    private final MapManager mapManager;
    private final InstanceManager instanceManager;
    private final GameRegistry gameRegistry;

    public MatchMaker(JavaPlugin plugin, MapManager mapManager, InstanceManager instanceManager, GameRegistry gameRegistry) {
        this.plugin = plugin;
        this.mapManager = mapManager;
        this.instanceManager = instanceManager;
        this.gameRegistry = gameRegistry;
    }

    public void addPlayerToQueue(Player player, Class<? extends Minigame> minigameType) {
        if (playerInQueue(player)) return;
        playerQueue.put(player, minigameType);
    }

    public void removePlayerFromQueue(Player player) {
        playerQueue.remove(player);
    }

    public boolean playerInQueue(Player player) {
        return playerQueue.get(player) != null;
    }

    public Class<? extends Minigame> getMinigameTypeQueuedFor(Player player) {
        if (!playerInQueue(player)) return null;

        return playerQueue.get(player);
    }

    public Class<? extends Minigame> getMinigameTypeQueuedFor(Party party) {
        if (!partyInQueue(party)) return null;

        return partyQueue.get(party);
    }

    public void addPartyToQueue(Party party, Class<? extends Minigame> minigameType) {
        if (partyInQueue(party)) return;
        partyQueue.put(party, minigameType);
    }

    public void removePartyFromQueue(Party party, Class<? extends Minigame> minigameType) {
        partyQueue.remove(party);
    }

    public boolean partyInQueue(Party party) {
        return partyQueue.get(party) != null;
    }



    public void matchMake() {
        Map<Class<? extends Minigame>, List<Player>> grouped = new HashMap<>();
        Map<Class<? extends Minigame>, List<List<Player>>> partyUnits = new HashMap<>();

        for (Map.Entry<Player, Class<? extends Minigame>> entry : playerQueue.entrySet()) {
            grouped.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        for (Map.Entry<Party, Class<? extends Minigame>> entry : partyQueue.entrySet()) {
            partyUnits.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                    .add(new ArrayList<>(entry.getKey().getMembers()));
        }

        for (Class<? extends Minigame> minigameType : grouped.keySet()) {
            Minigame minigame;
            try {
                minigame = minigameType.getDeclaredConstructor(JavaPlugin.class, InstanceManager.class, MapManager.class)
                        .newInstance(plugin, instanceManager, mapManager);
            } catch (Exception e) {
                System.out.println("[BridgeBattles] Failed to construct minigame " + minigameType.getSimpleName());
                continue;
            }

            List<Player> selectedPlayers = new ArrayList<>();
            int maxPlayers = minigame.getMaxPlayers();

            for (List<Player> party : partyUnits.getOrDefault(minigameType, List.of())) {
                if (selectedPlayers.size() + party.size() > maxPlayers) continue;
                selectedPlayers.addAll(party);
            }

            for (Player player : grouped.get(minigameType)) {
                if (selectedPlayers.size() >= maxPlayers) break;
                selectedPlayers.add(player);
            }

            if (selectedPlayers.size() < maxPlayers) continue;

            for (Player player : selectedPlayers) {
                Instance originalInstance = instanceManager.getInstanceByWorld(player.getWorld());
                originalInstance.removePlayer(player);
                minigame.arena.addPlayer(player);
                player.teleport(minigame.getGlobalSpawnLocation());
                playerQueue.remove(player);
            }

            gameRegistry.registerGame(minigame);
        }
    }


}
