package de.realleoxian.moonlightcore.api.config.metadata;

import net.minecraft.network.chat.Component;

import java.util.Objects;

public class TranslationKeyMetadata {
    public static final ConfigMetadataType<TranslationKeyMetadata, TranslationKeyMetadata.Builder> TYPE = new ConfigMetadataType<>(TranslationKeyMetadata.Builder::new, TranslationKeyMetadata::new, false);

    private final String translationKey;

    private TranslationKeyMetadata(TranslationKeyMetadata.Builder builder) {
        this.translationKey = Objects.requireNonNull(builder.translationKey, "Translation key metadata may not be 'null'");
    }

    public Component displayName() {
        return Component.translatable(this.translationKey);
    }

    public static final class Builder {
        private String translationKey = null;

        public void set(String translationKey) {
            if (translationKey.isEmpty()) {
                throw new IllegalArgumentException("Translation key may not be 'empty'");
            }
            this.translationKey = translationKey;
        }

        private Builder() {}
    }
}
