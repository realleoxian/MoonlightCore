package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.attachment.AttachmentHolderImpl;
import de.leoxian.moonlightcore.attachment.AttachmentSavedState;
import de.leoxian.moonlightcore.attachment.AttachmentType;
import de.leoxian.moonlightcore.attachment.sync.AttachmentHolderInfo;
import de.leoxian.moonlightcore.core.MoonlightCore;
import de.leoxian.moonlightcore.core.network.clientbound.S2CAttachmentSyncPacket;
import de.leoxian.moonlightcore.util.PlayerTrackUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements AttachmentHolderImpl {

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mlcore_createAttachmentsSavedData(CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;

        self.getDataStorage().computeIfAbsent(
                (t) -> AttachmentSavedState.read(self, t),
                () -> new AttachmentSavedState(self), AttachmentSavedState.ID);
    }

    @Override
    public void mlcore_sendChangePacket(AttachmentType<?> type, S2CAttachmentSyncPacket packet) {
        if((Object) this instanceof ServerLevel serverLevel) {
            PlayerTrackUtils.level(serverLevel).forEach(player -> {
                if(type.syncPredicate().test(this, player)) {
                    MoonlightCore.PACKET_DISPATCHER.sendToPlayer(player, packet);
                }
            });
        }
    }

    @Override
    public AttachmentHolderInfo<?> mlcore_getHolderInfo() {
        return AttachmentHolderInfo.LevelInfo.INSTANCE;
    }
}
