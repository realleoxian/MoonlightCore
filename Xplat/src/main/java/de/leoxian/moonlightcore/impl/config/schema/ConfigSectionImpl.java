package de.leoxian.moonlightcore.impl.config.schema;

import de.leoxian.moonlightcore.api.config.schema.ConfigKey;
import de.leoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.leoxian.moonlightcore.api.config.schema.ConfigSection;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ConfigSectionImpl implements ConfigSection {
    private final ConfigKey key;

    private final Map<String, ConfigSection> sections = new HashMap<>();
    private final Map<String, ConfigProperty<?>> properties = new HashMap<>();

    private final @Nullable Iterable<String> comments;
    private final @Nullable String translationKey;

    ConfigSectionImpl(ConfigKey key, Map<ConfigKey, ConfigSection> sections, Map<ConfigKey, ConfigProperty<?>> properties, @Nullable Iterable<String> comments, @Nullable String translationKey) {
        this.key = key;
        this.comments = comments;
        this.translationKey = translationKey;

        for (var sectionEntry : sections.entrySet()) this.sections.put(sectionEntry.getKey().getLastComponent(), sectionEntry.getValue());
        for (var propEntry : properties.entrySet()) this.properties.put(propEntry.getKey().getLastComponent(), propEntry.getValue());
    }

    @Override
    public ConfigProperty<?> getProperty(String key) {
        if (!this.properties.containsKey(key)) {
            throw new IllegalArgumentException("Unknown property: " + this.key.child(key).toString());
        }

        return this.properties.get(key);
    }

    @Override
    public ConfigSection getSection(String key) {
        if (!this.sections.containsKey(key)) {
            throw new IllegalArgumentException("Unknown sub-section: " + this.key.child(key).toString());
        }

        return this.sections.get(key);
    }

    @Override
    public Collection<ConfigProperty<?>> getProperties() {
        return this.properties.values();
    }

    @Override
    public Collection<ConfigSection> getSubSections() {
        return this.sections.values();
    }

    @Override
    public @Nullable Iterable<String> getComments() {
        return this.comments;
    }

    @Override
    public @Nullable String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public ConfigKey getKey() {
        return this.key;
    }
}
