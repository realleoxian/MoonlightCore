package de.realleoxian.moonlightcore.mixin;

import de.realleoxian.moonlightcore.impl.attachment.AttachmentMapImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(ClientboundBlockEntityDataPacket.class)
public class ClientboundBlockEntityDataPacketMixin {

    @ModifyArg(method = "create(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;create(Lnet/minecraft/world/level/block/entity/BlockEntity;Ljava/util/function/Function;)Lnet/minecraft/network/protocol/game/ClientboundBlockEntityDataPacket;"))
    private static Function<BlockEntity, CompoundTag> mlcore_stripPersistentAttachmentData(Function<BlockEntity, CompoundTag> getter) {
        return be -> {
            CompoundTag nbt = getter.apply(be);
            nbt.remove(AttachmentMapImpl.NBT_TAG);

            return nbt;
        };
    }

}
