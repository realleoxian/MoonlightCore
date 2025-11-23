package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolder;
import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentInternals;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentChange;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.util.PlayerTrackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends AttachmentHoldersMixin implements AttachmentHolderImpl {
    @Shadow
    public abstract Map<BlockPos, BlockEntity> getBlockEntities();

    @Shadow @Final
    Level level;

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At("TAIL"))
    private void mlcore_transferProtoChunkAttachments(ServerLevel level, ProtoChunk chunk, LevelChunk.PostLoadProcessor postLoad, CallbackInfo ci) {
        AttachmentInternals.transfer((AttachmentHolder) chunk, this, false);
    }

    @Override
    public void mlcore_computeInitialAttachmentChanges(ServerPlayer player, Consumer<AttachmentChange> changeOutput) {
        super.mlcore_computeInitialAttachmentChanges(player, changeOutput);

        for(BlockEntity be : this.getBlockEntities().values()) {
            ((AttachmentHolderImpl) be).mlcore_computeInitialAttachmentChanges(player, changeOutput);
        }
    }

    @Override
    public void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {
        if(this.level instanceof ServerLevel serverLevel) {
            PlayerTrackUtils.tracking(serverLevel, ((ChunkAccess) (Object) this).getPos()).forEach(player -> {
                if(type.syncPredicate().test(this, player)) {
                    MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, packet);
                }
            });
        }
    }

    @Override
    public boolean mlcore_shouldSync() {
        return !this.level.isClientSide();
    }
}
