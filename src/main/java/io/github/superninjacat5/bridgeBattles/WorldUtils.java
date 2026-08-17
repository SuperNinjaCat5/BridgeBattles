package io.github.superninjacat5.bridgeBattles;
// VIBE CODED!!!!!!!!!!

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public final class WorldUtils {
    public static void copyWorldFolder(Path source, Path target) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            for (Path src : (Iterable<Path>) stream::iterator) {
                String name = src.getFileName().toString();
                // Skip files that must be regenerated per-copy
                if (name.equals("uid.dat") || name.equals("session.lock")) continue;

                Path dest = target.resolve(source.relativize(src));
                if (Files.isDirectory(src)) {
                    Files.createDirectories(dest);
                } else {
                    Files.createDirectories(dest.getParent());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}