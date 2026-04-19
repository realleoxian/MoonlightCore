package de.realleoxian.moonlightcore.api.attachment;

import de.realleoxian.moonlightcore.api.network.NetworkHelper;
import de.realleoxian.moonlightcore.api.network.PacketDecoder;
import de.realleoxian.moonlightcore.api.network.PacketEncoder;
import de.realleoxian.moonlightcore.api.network.PlayerTrackUtils;
import de.realleoxian.moonlightcore.impl.attachment.sync.AttachmentSyncChange;
import de.realleoxian.moonlightcore.core.network.s2c.S2CAttachmentSyncPacket;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public interface AttachmentsHolderInfo<T, S extends AttachmentsHolderInfo<T, S>> {
    int MAX_SIZE_IN_BYTES = Byte.BYTES + Long.BYTES;

    void syncAttachment(ServerLevel level, AttachmentSyncChange syncChange);

    @Nullable T get(Level level);

    Type<T, S> type();

    @SuppressWarnings("unchecked")
    default void writeToBuffer(FriendlyByteBuf byteBuf) {
        type().encoder().write((S) this, byteBuf);
    }

    default byte typeId() {
        return type().id();
    }

    record BlockEntityHolderInfo(BlockPos blockPos) implements AttachmentsHolderInfo<BlockEntity, BlockEntityHolderInfo> {
        public static final Type<BlockEntity, BlockEntityHolderInfo> TYPE = new Type<>((byte) 0, BlockEntityHolderInfo::writeToBuffer, BlockEntityHolderInfo::new);

        private static void writeToBuffer(BlockEntityHolderInfo holderInfo, ByteBuf byteBuf) {
            byteBuf.writeLong(holderInfo.blockPos().asLong());
        }

        public BlockEntityHolderInfo(ByteBuf byteBuf) {
            this(BlockPos.of(byteBuf.readLong()));
        }

        @Override
        public void syncAttachment(ServerLevel level, AttachmentSyncChange syncChange) {
            PlayerTrackUtils.tracking(get(level)).forEach(player -> NetworkHelper.get().sendToPlayer(player, new S2CAttachmentSyncPacket(syncChange)));
        }

        @Override
        public @Nullable BlockEntity get(Level level) {
            BlockState blockState = level.getBlockState(blockPos);
            return blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
        }

        @Override
        public Type<BlockEntity, BlockEntityHolderInfo> type() {
            return TYPE;
        }
    }

    record EntityHolderInfo(int id) implements AttachmentsHolderInfo<Entity, EntityHolderInfo> {
        public static final Type<Entity, EntityHolderInfo> TYPE = new Type<>((byte) 1, EntityHolderInfo::writeToBuffer, EntityHolderInfo::new);

        private static void writeToBuffer(EntityHolderInfo holderInfo, ByteBuf byteBuf) {
            byteBuf.writeInt(holderInfo.id());
        }

        public EntityHolderInfo(ByteBuf byteBuf) {
            this(byteBuf.readInt());
        }

        @Override
        public void syncAttachment(ServerLevel level, AttachmentSyncChange syncChange) {
            PlayerTrackUtils.tracking(get(level)).forEach(player -> NetworkHelper.get().sendToPlayer(player, new S2CAttachmentSyncPacket(syncChange)));
        }

        @Override
        public @Nullable Entity get(Level level) {
            return level.getEntity(id);
        }

        @Override
        public Type<Entity, EntityHolderInfo> type() {
            return TYPE;
        }
    }

    enum LevelHolderInfo implements AttachmentsHolderInfo<Level, LevelHolderInfo> {
        INSTANCE
        ;
        public static final Type<Level, LevelHolderInfo> TYPE = new Type<>((byte) 2, LevelHolderInfo::encodeToBuffer, LevelHolderInfo::decodeFromBuffer);

        private static void encodeToBuffer(LevelHolderInfo holderInfo, ByteBuf byteBuf) {
        }

        private static LevelHolderInfo decodeFromBuffer(ByteBuf byteBuf) {
            return INSTANCE;
        }

        @Override
        public void syncAttachment(ServerLevel level, AttachmentSyncChange syncChange) {
            PlayerTrackUtils.level(level).forEach(player -> NetworkHelper.get().sendToPlayer(player, new S2CAttachmentSyncPacket(syncChange)));
        }

        @Override
        public Level get(Level level) {
            return level;
        }

        @Override
        public Type<Level, LevelHolderInfo> type() {
            return TYPE;
        }
    }

    record Type<T, H extends AttachmentsHolderInfo<T, H>>(byte id, PacketEncoder<? super ByteBuf, H> encoder, PacketDecoder<? super ByteBuf, H> decoder) {
        private static final Byte2ObjectOpenHashMap<Type<?, ?>> TYPES = new Byte2ObjectOpenHashMap<>();

        public static <T, H extends AttachmentsHolderInfo<T, H>> PacketEncoder<? super ByteBuf, H> getEncoder(byte typeID) {
            return Type.<T, H>getType(typeID).encoder;
        }

        public static <T, H extends AttachmentsHolderInfo<T, H>> PacketDecoder<? super ByteBuf, H> getDecoder(byte typeID) {
            return Type.<T, H>getType(typeID).decoder;
        }

        @Nullable @SuppressWarnings("unchecked")
        private static <T, H extends AttachmentsHolderInfo<T, H>> Type<T, H> getType(byte id) {
            return (Type<T, H>) Objects.requireNonNull(TYPES.get(id), "Unknown AttachmentHolderInfo type: " + id);
        }

        public Type {
            TYPES.put(id(), this);
        }
    }
}
