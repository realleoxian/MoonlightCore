package de.realleoxian.moonlightcore.impl.config.schema;

import com.google.common.base.Splitter;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.*;
import de.realleoxian.moonlightcore.impl.config.DefaultedLoadedConfig;
import de.realleoxian.moonlightcore.impl.config.ModConfigImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class ConfigSchemaImpl implements ConfigSchema {
    private static final Splitter DOT_SPLITTER = Splitter.on('.');
    private static final Splitter LINE_SPLITTER = Splitter.on('\n');

    private final Map<ConfigKey, ConfigSection> sections;
    private final Map<ConfigKey,ConfigProperty<?>> rootProperties;

    ModConfigImpl parent = null;
    LoadedConfig config;

    public ConfigSchemaImpl(BuilderImpl builder) {
        this.sections = Map.copyOf(builder.rootSections);
        this.rootProperties = Map.copyOf(builder.rootProperties);
        this.config = DefaultedLoadedConfig.INSTANCE;
    }

    @Override
    public void accept(LoadedConfig config) {
        Objects.requireNonNull(parent, "ModConfig parent cannot be 'null'");
        this.config = config;
        update();
    }

    @Override
    public Collection<ConfigProperty<?>> getRootProperties() {
        return Collections.unmodifiableCollection(this.rootProperties.values());
    }

    @Override
    public Collection<ConfigSection> getSections() {
        return Collections.unmodifiableCollection(this.sections.values());
    }

    @ApiStatus.Internal
    public void setParent(ModConfigImpl parent) {
        Objects.requireNonNull(parent, "Cannot set a 'null' parent");
        this.parent = parent;
        update();
    }

    public void save() {
        Objects.requireNonNull(this.parent, "ModConfig parent cannot be 'null'");
        this.config.save(this.parent);
    }

    @ApiStatus.Internal
    private void update() {
        this.rootProperties.values().forEach(property -> ((ConfigPropertyImpl<?>) property).setup(this));
        this.sections.values().forEach(this::updateSection);
    }

    @ApiStatus.Internal
    private void updateSection(ConfigSection section) {
        section.getProperties().forEach(property -> ((ConfigPropertyImpl<?>) property).setup(this));
        section.getSubSections().forEach(this::updateSection);
    }

    public static final class BuilderImpl implements Builder {
        private final Map<ConfigKey, ConfigProperty<?>> rootProperties = new LinkedHashMap<>();
        private final Map<ConfigKey, ConfigSection> rootSections = new LinkedHashMap<>();

        private final Map<ConfigKey, List<String>> sectionComments = new HashMap<>();
        private final Map<ConfigKey, String> sectionTranslationKeys = new HashMap<>();

        private final Stack<String> sectionsPath = new Stack<>();
        private final Deque<Map<ConfigKey, ConfigSection>> sectionsDeque = new ArrayDeque<>();
        private final Deque<Map<ConfigKey, ConfigProperty<?>>> propertiesDeque = new ArrayDeque<>();

        private ConfigKey currentSectionKey = null;

        private final List<String> comments = new ArrayList<>();
        private @Nullable String translationKey = null;
        private RestartType restartType = RestartType.NONE;

        private boolean built = false;

        @Override
        public Builder push(String path) {
            checkNotBuilt();
            if (path.isEmpty()) throw new IllegalArgumentException("Cannot push empty section's path");

            ConfigKey currentKey = this.currentSectionKey;
            for (String part : DOT_SPLITTER.split(path)) {
                this.sectionsPath.push(part);

                currentKey = currentKey == null ? new ConfigKeyImpl(part) : currentKey.child(part);
                this.sectionsDeque.push(new LinkedHashMap<>());
                this.propertiesDeque.push(new LinkedHashMap<>());
            }
            this.currentSectionKey = currentKey;
            if (this.translationKey != null) {
                this.sectionTranslationKeys.put(currentKey, this.translationKey);
                this.translationKey = null;
            }
            if (!this.comments.isEmpty()) {
                this.sectionComments.put(currentKey, List.copyOf(this.comments));
                this.comments.clear();
            }

            return this;
        }

        @Override
        public Builder pop(int count) {
            checkNotBuilt();
            if (this.sectionsPath.isEmpty()) throw new IllegalStateException("There are no sections available to be popped");
            if (count <= 0) throw new IllegalArgumentException("Cannot pop 0 or less sections");
            if (count > this.sectionsPath.size()) throw new IllegalArgumentException("Cannot pop more than %d sections from the path".formatted(this.sectionsPath.size()));

            ConfigKey currentKey = this.currentSectionKey;
            for (int i = 0; i < count; i++) {
                Map<ConfigKey, ConfigSection> currentSections = getCurrentSectionsMap();
                Map<ConfigKey, ConfigProperty<?>> currentProperties = getCurrentPropertiesMap();

                List<String> sectionComments = this.sectionComments.remove(currentKey);
                String translationKey = this.sectionTranslationKeys.remove(currentKey);
                ConfigSection section = new ConfigSectionImpl(currentKey, currentSections, currentProperties, sectionComments, translationKey);

                this.propertiesDeque.removeLast();
                this.sectionsDeque.removeLast();

                currentKey = currentKey.pop();
                this.sectionsPath.pop();

                getCurrentSectionsMap().put(section.getKey(), section);
            }
            this.currentSectionKey = this.sectionsPath.isEmpty() ? null : currentKey;

            return this;
        }

        @Override
        public Builder comment(String comment) {
            checkNotBuilt();
            for (String line : LINE_SPLITTER.split(comment)) {
                this.comments.add(line);
            }

            return this;
        }

        @Override
        public Builder translationKey(String key) {
            checkNotBuilt();this.translationKey = key;
            return this;
        }

        @Override
        public Builder worldRestart() {
            checkNotBuilt();
            this.restartType = RestartType.WORLD;
            return this;
        }

        @Override
        public Builder gameRestart() {
            checkNotBuilt();
            this.restartType = RestartType.GAME;
            return this;
        }

        @Override
        public <T> ConfigProperty<T> define(String key, ConfigPropertyType<T> type, ConfigPropertyValidator<T> validator, Supplier<T> defValue) {
            checkNotBuilt();
            ConfigKey propertyKey = this.currentSectionKey == null ? new ConfigKeyImpl(key) : this.currentSectionKey.child(key);
            ConfigProperty<T> property = new ConfigPropertyImpl<>(propertyKey, type, validator, this.restartType, this.comments, this.translationKey, defValue);
            getCurrentPropertiesMap().put(propertyKey, property);
            resetState();

            return property;
        }

        @Override
        public void build() {
            if (built) throw new IllegalStateException("ConfigSchema was already built");
            if (!this.sectionsPath.isEmpty()) throw new IllegalStateException("Expected all sections/categories to be closed at build time. Found %d not being closed".formatted(this.sectionsPath.size()));
            if (this.translationKey != null) throw new IllegalStateException("Found an unattached translation key at build time");
            if (this.restartType != RestartType.NONE) throw new IllegalStateException("Expected restart type to be 'NONE' at build time");
            if (!this.comments.isEmpty()) throw new IllegalStateException("Expected comments to be attached to a section or property");

            this.built = true;
        }

        public boolean isBuilt() {
            return built;
        }

        private Map<ConfigKey, ConfigSection> getCurrentSectionsMap() {
            return this.sectionsPath.isEmpty() ? this.rootSections : this.sectionsDeque.peekLast();
        }

        private Map<ConfigKey, ConfigProperty<?>> getCurrentPropertiesMap() {
            return this.sectionsPath.isEmpty() ? this.rootProperties : this.propertiesDeque.peekLast();
        }

        private void checkNotBuilt() {
            if (built) {
                throw new IllegalStateException("Cannot longer modify this config schema, it was already built");
            }
        }

        private void resetState() {
            this.restartType = RestartType.NONE;
            this.comments.clear();
            this.translationKey = null;
        }
    }
}
