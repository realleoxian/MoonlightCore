package de.leoxian.moonlightcore.config;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.util.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigSerializer {
    private static final Splitter LINE_SPLITTER = Splitter.on('\n');
    private static final Splitter COLON_SPLITTER = Splitter.on(':');

    private static final Pattern COMMENT_REGEX = Pattern.compile("^#.*$");
    private static final Pattern CATEGORY_REGEX = Pattern.compile("^\\s*\\[(?<category>.*)]$");
    private static final Pattern KEY_REGEX = Pattern.compile("^\\s*(?<key>[^\\s=]+)\\s*=\\s*(?<value>.*?)\\s*$");

    public static void readFromFile(ModConfigSpec spec) {
        if(!Files.exists(spec.getFilePath())) {
            writeToFile(spec);
        }

        try {
            List<String> content = Files.readAllLines(spec.getFilePath());

            ModConfigSpec.Category currentCategory = null;
            for (String line : content) {
                if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                    continue;
                }

                Matcher categoryMatcher = CATEGORY_REGEX.matcher(line);
                if (categoryMatcher.matches()) {
                    List<String> path = COLON_SPLITTER.splitToList(categoryMatcher.group("category"));

                    currentCategory = spec.getCategory(path);
                }

                if (currentCategory == null) {
                    MoonlightCore.LOGGER.warn(ModConfigSpec.MARKER, "uhh...?");
                    continue;
                }

                Matcher keyMatcher = KEY_REGEX.matcher(line);
                if (keyMatcher.matches()) {
                    String key = keyMatcher.group("key");
                    String value = keyMatcher.group("value");

                    currentCategory.getValueKey(key).cacheValue(value);
                }
            }
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public static void readFromBytes(byte[] newData, ModConfigSpec spec) {
        List<String> content = LINE_SPLITTER.splitToList(new String(newData, StandardCharsets.UTF_8));

        ModConfigSpec.Category currentCategory = null;
        for (String line : content) {
            if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                continue;
            }

            Matcher categoryMatcher = CATEGORY_REGEX.matcher(line);
            if (categoryMatcher.matches()) {
                List<String> path = COLON_SPLITTER.splitToList(categoryMatcher.group("category"));

                currentCategory = spec.getCategory(path);
            }

            if (currentCategory == null) {
                MoonlightCore.LOGGER.warn(ModConfigSpec.MARKER, "uhh..?");
                continue;
            }

            Matcher keyMatcher = KEY_REGEX.matcher(line);
            if (keyMatcher.matches()) {
                String key = keyMatcher.group("key");
                String value = keyMatcher.group("value");

                currentCategory.getValueKey(key).cacheValue(value);
            }
        }
    }

    public static byte[] readToBytes(ModConfigSpec spec) {
        if(!Files.exists(spec.getFilePath())) {
            writeToFile(spec);
        }

        try {
            List<String> content = Files.readAllLines(spec.getFilePath());
            return Joiner.on('\n').join(content).getBytes(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void writeToFile(ModConfigSpec spec) {
        if(Files.exists(spec.getFilePath())) {
            return;
        }

        List<String> serializedContent = new ArrayList<>();
        for(String categoryKey : spec.categories()) {
            writeCategory(serializedContent, spec.getCategory(categoryKey), null);
        }

        FileUtils.writeUsingTempFile(spec.getFilePath(), serializedContent);
    }

    private static void writeCategory(List<String> serializedContent, ModConfigSpec.Category category, String parent) {
        String absolutePath = (parent == null || parent.isBlank()) ? category.getKey() : parent + ":" + category.getKey();

        if(!category.getComment().isBlank()) {
            List<String> lines = LINE_SPLITTER.splitToList(category.getComment());

            for(String line : lines) {
                serializedContent.add("# " + line);
            }
        }

        serializedContent.add("[" + absolutePath + "]");
        serializedContent.add("");

        for(String valueKey : category.valueKeys()) {
            writeKey(serializedContent, category.getValueKey(valueKey));
            serializedContent.add("");
        }

        for(String categoryKey : category.subCategories()) {
            writeCategory(serializedContent, category.getSubCategory(categoryKey), absolutePath);
        }

        serializedContent.add("");
    }

    private static void writeKey(List<String> serializedContent, ModConfigSpec.ValueKey<?> key) {
        String value = key.valueToStr();

        if(!key.getComment().isBlank()) {
            List<String> lines = LINE_SPLITTER.splitToList(key.getComment());

            for (String line : lines) {
                serializedContent.add("# " + line);
            }
        }

        serializedContent.add("# Default value: " + value);
        serializedContent.add("%s = %s".formatted(key.getKey(), value));
    }

    private ConfigSerializer() {}
}
