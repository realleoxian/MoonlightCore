package de.realleoxian.moonlightcore.api.config.metadata;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;

public final class WarningMetadata {
    public static final ConfigMetadataType<WarningMetadata, WarningMetadata.Builder> TYPE = new ConfigMetadataType<>(WarningMetadata.Builder::new, WarningMetadata::new, true);

    public final Type type;
    public final Component message;

    private WarningMetadata(WarningMetadata.Builder builder) {
        Preconditions.checkArgument(builder.type != Type.NONE, "Warning metadata may not be 'none' type");
        this.type = builder.type;
        this.message = builder.message;
    }

    public static final class Builder {
        private Type type = Type.NONE;
        private Component message = Component.empty();

        public void custom(Component message) {
            this.set(Type.CUSTOM, message);
        }

        public void experimental() {
            this.set(Type.EXPERIMENTAL, Component.translatable("moonlightcore.config.warning.experimental"));
        }

        public void unstable() {
            this.set(Type.UNSTABLE, Component.translatable("moonlightcore.config.warning.unstable"));
        }

        private void set(Type type, Component message) {
            this.type = type;
            this.message = message;
        }

        private Builder() {}
    }

    public enum Type {
        NONE,
        CUSTOM,
        EXPERIMENTAL,
        UNSTABLE
    }
}
