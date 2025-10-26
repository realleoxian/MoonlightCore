package de.leoxian.moonlightcore.attachment.sync;

import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface AttachmentHolderInfo<T> {
    int MAX_SIZE_IN_BYTES = Byte.BYTES + Long.BYTES;
    StreamCodec<ByteBuf, AttachmentHolderInfo<?>> STREAM_CODEC = ByteBufCodecs.BYTE.dispatch(AttachmentHolderInfo::getTypeId, Type::codecById);

    AttachmentHolder getHolder(Level level);

    Type<T> getType();

    default byte getTypeId() {
        return getType().id();
    }

    record Type<T>(byte id, StreamCodec<ByteBuf, ? extends AttachmentHolderInfo<T>> streamCodec) {
        private static final Byte2ObjectMap<Type<?>> TYPES = new Byte2ObjectArrayMap<>();

        public static final Type<BlockEntity> BLOCK_ENTITY = new Type<>((byte) 0, BlockEntityInfo.STREAM_CODEC);
        public static final Type<Entity> ENTITY = new Type<>((byte) 1, EntityInfo.STREAM_CODEC);
        public static final Type<ChunkAccess> CHUNK_ACCESS = new Type<>((byte) 2, ChunkAccessInfo.STREAM_CODEC);
        public static final Type<Level> LEVEL = new Type<>((byte) 2, LevelInfo.STREAM_CODEC);

        public Type {
            TYPES.put(this.id(), this);
        }

        private static StreamCodec<ByteBuf, ? extends AttachmentHolderInfo<?>> codecById(byte id) {
            return TYPES.get(id).streamCodec;
        }
    }

    record BlockEntityInfo(BlockPos pos) implements AttachmentHolderInfo<BlockEntity> {
        public static final StreamCodec<ByteBuf, BlockEntityInfo> STREAM_CODEC = ByteBufCodecs.BLOCK_POS.xmap(BlockEntityInfo::new, BlockEntityInfo::pos);

        @Override
        public AttachmentHolder getHolder(Level level) {
            return (AttachmentHolder) level.getBlockEntity(this.pos());
        }

        @Override
        public Type<BlockEntity> getType() {
            return Type.BLOCK_ENTITY;
        }
    }

    record EntityInfo(int id) implements AttachmentHolderInfo<Entity> {
        public static final StreamCodec<ByteBuf, EntityInfo> STREAM_CODEC = ByteBufCodecs.INTEGER.xmap(EntityInfo::new, EntityInfo::id);

        @Override
        public AttachmentHolder getHolder(Level level) {
            return (AttachmentHolder) level.getEntity(this.id);
        }

        @Override
        public Type<Entity> getType() {
            return Type.ENTITY;
        }
    }

    record ChunkAccessInfo(ChunkPos chunkPos) implements AttachmentHolderInfo<ChunkAccess> {
        public static final StreamCodec<ByteBuf, ChunkAccessInfo> STREAM_CODEC = ByteBufCodecs.LONG.xmap(ChunkPos::new, ChunkPos::toLong).xmap(ChunkAccessInfo::new, ChunkAccessInfo::chunkPos);

        @Override
        public AttachmentHolder getHolder(Level level) {
            return (AttachmentHolder) level.getChunk(this.chunkPos().x, this.chunkPos().z);
        }

        @Override
        public Type<ChunkAccess> getType() {
            return Type.CHUNK_ACCESS;
        }
    }

    enum LevelInfo implements AttachmentHolderInfo<Level> {
        INSTANCE
        ;
        public static final StreamCodec<ByteBuf, LevelInfo> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public AttachmentHolder getHolder(Level level) {
            return (AttachmentHolder) level;
        }

        @Override
        public Type<Level> getType() {
            return Type.LEVEL;
        }
    }

}
