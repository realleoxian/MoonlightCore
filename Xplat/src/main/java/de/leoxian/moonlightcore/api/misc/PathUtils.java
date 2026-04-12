package de.leoxian.moonlightcore.api.misc;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PathUtils {
    private static boolean atomicMoveSupported = true;

    public static void writeUsingTempFile(Path path, Iterable<String> lines) throws IOException {
        Files.createDirectories(path.getParent());
        Path tempFile = Files.createTempFile(path.getParent(), null, null);

        try {
            Files.write(tempFile, lines);

            if (atomicMoveSupported) {
                try {
                    Files.move(tempFile, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    return;
                } catch (AtomicMoveNotSupportedException e) {
                    atomicMoveSupported = false;
                }
            }
            Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private PathUtils() {}
}
