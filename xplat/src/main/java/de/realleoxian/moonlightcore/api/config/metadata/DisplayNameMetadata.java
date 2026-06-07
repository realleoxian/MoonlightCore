package de.realleoxian.moonlightcore.api.config.metadata;

import java.util.Objects;

public record DisplayNameMetadata(String translationKey) {
    public static final ConfigMetadataType<DisplayNameMetadata, DisplayNameMetadata.Builder> TYPE = new ConfigMetadataType<DisplayNameMetadata, Builder>(Builder::new, DisplayNameMetadata::new);

    public DisplayNameMetadata(Builder builder) {
        this(Objects.requireNonNull(builder.translationKey));
    }

    public static final class Builder {
        private String translationKey = null;

        public Builder set(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }
    }
}
