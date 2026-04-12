package de.realleoxian.moonlightcore.api.attachment;

import com.mojang.serialization.Codec;
import de.realleoxian.moonlightcore.api.network.PacketDecoder;
import de.realleoxian.moonlightcore.api.network.PacketEncoder;
import de.realleoxian.moonlightcore.impl.attachment.AttachmentTypeImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AttachmentType<T> {

    static <T> AttachmentType<T> create(ResourceLocation name, Consumer<Builder<T>> modifier) {
        return AttachmentTypeImpl.create(name, modifier);
    }

    ResourceLocation name();

    @Nullable
    Codec<T> codec();

    @Nullable
    AttachmentSyncHandler<T> syncHandler();

    @Nullable
    Supplier<T> initializer();

    boolean copyOnDeath();

    default boolean isPersistent() {
        return codec() != null;
    }

    default boolean isSync() {
        return syncHandler() != null;
    }

    interface Builder<T> {

        Builder<T> codec(Codec<T> codec);

        Builder<T> syncHandler(AttachmentSyncHandler<T> syncHandler);

        Builder<T> initializer(Supplier<T> initializer);

        Builder<T> copyOnDeath(boolean copyOnDeath);

        default Builder<T> syncHandler(BiPredicate<AttachmentHolder, ServerPlayer> predicate, PacketEncoder<FriendlyByteBuf, T> encoder, PacketDecoder<FriendlyByteBuf, T> decoder) {
            return syncHandler(new AttachmentSyncHandler<T>() {
                @Override
                public void encode(FriendlyByteBuf byteBuf, AttachmentType<T> type, T data) {
                    encoder.write(byteBuf, data);
                }

                @Override
                public T decode(FriendlyByteBuf byteBuf, AttachmentType<T> type) {
                    return decoder.read(byteBuf);
                }

                @Override
                public boolean canSync(AttachmentHolder holder, ServerPlayer player) {
                    return predicate.test(holder, player);
                }
            });
        }

        default Builder<T> syncHandler(PacketEncoder<FriendlyByteBuf, T> encoder, PacketDecoder<FriendlyByteBuf, T> decoder) {
            return syncHandler((h, s) -> true, encoder, decoder);
        }

    }
}
