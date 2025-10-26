package de.leoxian.moonlightcore.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    private static boolean atomicMoveSupported = true;

    public static void writeUsingTempFile(Path path, Iterable<? extends CharSequence> lines) {
        try {
            Files.createDirectories(path.getParent());
            Path tempFile = Files.createTempFile(path.getParent(), null, null);

            try {
                Files.write(tempFile, lines);
                moveAtomicReplace(tempFile, path);
            } finally {
                if(Files.exists(tempFile)) {
                    Files.delete(tempFile);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void moveAtomicReplace(Path source, Path target) throws IOException {
        if(atomicMoveSupported) {
            try {
                Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (AtomicMoveNotSupportedException ignored) {
                atomicMoveSupported = true;
            }
        }

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
