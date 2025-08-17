package de.leowgc.moonlightcore.config;

import com.google.common.base.Joiner;
import com.mojang.logging.LogUtils;
import de.leowgc.moonlightcore.api.config.ModConfigSpec;
import de.leowgc.moonlightcore.util.FileUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EzcConfigParser {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Pattern COMMENT_REGEX = Pattern.compile("^#.*$*");
    private static final Pattern CATEGORY_REGEX = Pattern.compile("^\\s*\\[(?<category>.*)]$*");
    private static final Pattern KEY_REGEX = Pattern.compile("^\\s*(?<key>[^\\s=]+)\\s*=\\s*(?<value>.*?)\\s*$");

    static void tryReadFromFile(Path filePath, ModConfigSpec spec) {
        if(!Files.exists(filePath)) {
            tryWriteToFile(filePath, spec);
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            ModConfigSpec.ConfigCategory currentCategory = null;
            for(int i = 0; i < lines.size(); i++) {
                int lineNumber = i + 1;
                String line = lines.get(i);

                if(line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                    continue;
                }

                Matcher categoryMatcher = CATEGORY_REGEX.matcher(line);
                if(categoryMatcher.matches()) {
                    String fullKey = categoryMatcher.group("category");
                    String[] keyParts = fullKey.split(":");

                    Optional<ModConfigSpec.ConfigCategory> optCategory = spec.getCategory(keyParts[0]);
                    if(optCategory.isEmpty()) {
                        LOGGER.error(formatError(filePath, lineNumber, line, """
                                Unknown config category: %s. Skipping
                                - Valid categories: %s
                                """.formatted(keyParts[0], String.join(", ", spec.categoryEntries()))));
                        continue;
                    }

                    currentCategory = optCategory.get();
                    for(int j = 1; j < keyParts.length; j++) {
                        if(currentCategory == null) break;
                        currentCategory = currentCategory.getChild(keyParts[j]).orElse(null);
                    }

                    if(currentCategory == null) {
                        LOGGER.error(formatError(filePath, lineNumber, line, "Unknown child category: %s".formatted(fullKey)));
                        continue;
                    }
                }

                if(currentCategory == null) {
                    LOGGER.error(formatError(filePath, lineNumber, line, "Expected a valid category, config value keys should be parsed after a category"));
                    continue;
                }

                Matcher keyMatcher = KEY_REGEX.matcher(line);
                if(keyMatcher.matches()) {
                    String key = keyMatcher.group("key");
                    String val = keyMatcher.group("value");

                    ModConfigSpec.ConfigCategory finalCurrentCategory = currentCategory;
                    currentCategory.getKey(key).ifPresentOrElse(
                            (valKey) -> ((ModConfigSpecImpl.ValueKeyImpl<?>) valKey).cacheValue(val),
                            () -> LOGGER.error(formatError(filePath, lineNumber, line, "Key '%s' not found in category '%s' Available keys: %s".formatted(key, finalCurrentCategory.id(), String.join(", ", finalCurrentCategory.keyEntries())))));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readFromBytes(byte[] newData, ModConfigSpecImpl spec) {
        String str = new String(newData, StandardCharsets.UTF_8);
        List<String> lines = Arrays.asList(str.split("\\r?\\n"));

        ModConfigSpec.ConfigCategory currentCategory = null;
        for(int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);

            if(line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                continue;
            }

            Matcher categoryMatcher = CATEGORY_REGEX.matcher(line);
            if(categoryMatcher.matches()) {
                String fullKey = categoryMatcher.group("category");
                String[] keyParts = fullKey.split(":");

                Optional<ModConfigSpec.ConfigCategory> optCategory = spec.getCategory(keyParts[0]);
                if(optCategory.isEmpty()) {
                    LOGGER.error(formatError(spec.filePath(), lineNumber, line, """
                                Unknown config category: %s. Skipping
                                - Valid categories: %s
                                """.formatted(keyParts[0], String.join(", ", spec.categoryEntries()))));
                    continue;
                }

                currentCategory = optCategory.get();
                for(int j = 1; j < keyParts.length; j++) {
                    if(currentCategory == null) break;
                    currentCategory = currentCategory.getChild(keyParts[j]).orElse(null);
                }

                if(currentCategory == null) {
                    LOGGER.error(formatError(spec.filePath(), lineNumber, line, "Unknown child category: %s".formatted(fullKey)));
                    continue;
                }
            }

            if(currentCategory == null) {
                LOGGER.error(formatError(spec.filePath(), lineNumber, line, "Expected a valid category, config value keys should be parsed after a category"));
                continue;
            }

            Matcher keyMatcher = KEY_REGEX.matcher(line);
            if(keyMatcher.matches()) {
                String key = keyMatcher.group("key");
                String val = keyMatcher.group("value");

                ModConfigSpec.ConfigCategory finalCurrentCategory = currentCategory;
                currentCategory.getKey(key).ifPresentOrElse(
                        (valKey) -> ((ModConfigSpecImpl.ValueKeyImpl<?>) valKey).cacheValue(val),
                        () -> LOGGER.error(formatError(spec.filePath(), lineNumber, line, "Key '%s' not found in category '%s' Available keys: %s".formatted(key, finalCurrentCategory.id(), String.join(", ", finalCurrentCategory.keyEntries())))));
            }
        }
    }

    private static String formatError(Path filePath, int lineNumber, String line, String errorDescription) {
        return """
                %s
                
                    - File: %s
                    - Line#%s : '%s'
                """.formatted(errorDescription, filePath, lineNumber, line);
    }

    public static byte[] writeToBytes(ModConfigSpec spec) {
        if(!Files.exists(spec.filePath())) {
            tryWriteToFile(spec.filePath(), spec);
        }

        try {
            List<String> content = Files.readAllLines(spec.filePath());

            return Joiner.on('\n').join(content).getBytes(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void tryWriteToFile(Path filePath, ModConfigSpec spec) {
        if(Files.exists(filePath)) {
            return;
        }

        List<String> content = new ArrayList<>();
        for(String categoryKey : spec.categoryEntries()) {
            spec.getCategory(categoryKey).ifPresent(category -> writeCategory(content, category, ""));
            content.add("");
        }

        try {
            FileUtils.writeUsingTempFile(filePath, content);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void writeCategory(List<String> content, ModConfigSpec.ConfigCategory category, String parentPath) {
        String newPath = parentPath.isBlank() ? category.id() : parentPath + ":" + category.id();

        if(!category.description().isEmpty()) {
            String[] descLines = category.description().split("\n");

            for(String descLine : descLines) {
                content.add("# %s".formatted(descLine));
            }
        }

        content.add("[%s]".formatted(newPath));
        content.add("");

        for(String valueKey : category.keyEntries()) {
            category.getKey(valueKey).ifPresent(key -> writeValueKey(content, key));
        }

        for(String categoryKey : category.childEntries()) {
            category.getChild(categoryKey).ifPresent(child -> writeCategory(content, child, newPath));
        }
        content.add("");
    }

    private static void writeValueKey(List<String> content, ModConfigSpec.ValueKey<?> valueKey) {
        String strValue = valueKey.writeValue();

        if(!valueKey.description().isEmpty()) {
            String[] descLines = valueKey.description().split("\n");

            for(String descLine : descLines) {
                content.add("# %s".formatted(descLine));
            }
        }

        content.add("# Default value: %s".formatted(strValue));
        content.add("%s = %s".formatted(valueKey.id(), strValue));
        content.add("");
    }

    private EzcConfigParser() {}
}
