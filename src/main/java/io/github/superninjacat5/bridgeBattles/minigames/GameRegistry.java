package io.github.superninjacat5.bridgeBattles.minigames;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRegistry {
    private Map<UUID, Minigame> games = new HashMap<>();

    public Map<UUID, Minigame> getGames() {
        return games;
    }

    public void registerGame(Minigame minigame) {
        games.put(minigame.getUuid(), minigame);
        minigame.configMinigame();
    }

    public void unRegisterGame(Minigame minigame) {
        games.remove(minigame.getUuid());
    }
}
