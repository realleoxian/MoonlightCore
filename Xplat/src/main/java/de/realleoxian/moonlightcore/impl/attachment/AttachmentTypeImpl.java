package de.realleoxian.moonlightcore.impl.attachment;

import com.mojang.serialization.Codec;
import de.realleoxian.moonlightcore.api.attachment.AttachmentSyncHandler;
import de.realleoxian.moonlightcore.api.attachment.AttachmentType;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AttachmentTypeImpl<T> implements AttachmentType<T> {
    public static final int MAX_SYNCED_NAME_SIZE = 255;

    public static <T> AttachmentType<T> create(ResourceLocation name, Consumer<AttachmentType.Builder<T>> modifier) {
        BuilderImpl<T> builder = new BuilderImpl<>();
        modifier.accept(builder);

        return new AttachmentTypeImpl<>(name, builder);
    }

    private final ResourceLocation name;
    private final Codec<T> codec;
    private final AttachmentSyncHandler<T> syncHandler;
    private final Supplier<T> initializer;
    private final boolean copyOnDeath;

    private AttachmentTypeImpl(ResourceLocation name, BuilderImpl<T> builder) {
        if(builder.syncHandler != null) {
            int nameLength = name.getPath().length();
            if(nameLength > MAX_SYNCED_NAME_SIZE) {
                throw new IllegalArgumentException("Max name length for synced attachment types it's 255");
            }
        }

        this.name = name;
        this.codec = builder.codec;
        this.syncHandler = builder.syncHandler;
        this.initializer = builder.initializer;
        this.copyOnDeath = builder.copyOnDeath;
    }

    @Override
    public ResourceLocation name() {
        return name;
    }

    @Override
    public @Nullable Codec<T> codec() {
        return codec;
    }

    @Override
    public @Nullable AttachmentSyncHandler<T> syncHandler() {
        return syncHandler;
    }

    @Override
    public @Nullable Supplier<T> initializer() {
        return initializer;
    }

    @Override
    public boolean copyOnDeath() {
        return copyOnDeath;
    }

    public static final class BuilderImpl<T> implements AttachmentType.Builder<T> {
        private @Nullable Codec<T> codec = null;
        private @Nullable AttachmentSyncHandler<T> syncHandler = null;
        private @Nullable Supplier<T> initializer = null;
        private boolean copyOnDeath = false;

        private BuilderImpl() {}

        @Override
        public AttachmentType.Builder<T> codec(Codec<T> codec) {
            Objects.requireNonNull(codec, "Cannot set an 'null' codec for attachment type");
            this.codec = codec;

            return this;
        }

        @Override
        public AttachmentType.Builder<T> syncHandler(AttachmentSyncHandler<T> syncHandler) {
            Objects.requireNonNull(syncHandler, "Cannot set an 'null' sync handler for attachment type");
            this.syncHandler = syncHandler;

            return this;
        }

        @Override
        public AttachmentType.Builder<T> initializer(Supplier<T> initializer) {
            Objects.requireNonNull(initializer, "Cannot set an 'null' initializer for attachment type");
            this.initializer = initializer;

            return this;
        }

        @Override
        public AttachmentType.Builder<T> copyOnDeath(boolean copyOnDeath) {
            this.copyOnDeath = copyOnDeath;
            return this;
        }
    }
}
