package io.github.superninjacat5.bridgeBattles;
// "Anybody have a map" - The Mom from Dear Evan Hanson

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapManager {
    private final Path mapsFolder;
    private final FileConfiguration mapConfig;
    private final Map<String, Path> arenaTemplates = new HashMap<>();
    private final Map<String, Path> lobbyTemplates = new HashMap<>();

    public MapManager(Path pluginDataFolder) {
        this.mapsFolder = pluginDataFolder.resolve("maps");

        File file = new File(mapsFolder.toFile(), "maps.yml");
        if (!file.exists()) {
            System.err.println("[BridgeBattles] maps.yml not found at: " + file.getAbsolutePath());
        }

        this.mapConfig = YamlConfiguration.loadConfiguration(file);

        loadTemplates("arenaTemplates", arenaTemplates);
        loadTemplates("lobbyTemplates", lobbyTemplates);

        System.out.println("[BridgeBattles] Loaded arena templates: " + arenaTemplates.keySet());
        System.out.println("[BridgeBattles] Loaded lobby templates: " + lobbyTemplates.keySet());

    }

    private void loadTemplates(String sectionName, Map<String, Path> target) { // light vc
        ConfigurationSection section = mapConfig.getConfigurationSection(sectionName);
        if (section == null) return;

        for (String name : section.getKeys(false)) {
            String rawPath = section.getString(name);
            if (rawPath == null) continue;
            target.put(name, mapsFolder.resolve(rawPath));
        }
    }

    public Path getArenaTemplate(String name) {
        return arenaTemplates.get(name);
    }

    public Path getLobbyTemplate(String name) {
        return lobbyTemplates.get(name);
    }
}
