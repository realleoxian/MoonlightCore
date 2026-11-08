package de.leoxian.moonlightcore.mixin.client;

import de.leoxian.moonlightcore.common.playerskin.MlPlayerSkin;
import de.leoxian.moonlightcore.common.playerskin.PlayerSkinHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(
            method = "getSkin",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private void moonlightcore$getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        MlPlayerSkin skin = PlayerSkinHandler.getSkin(self);

        if (!PlayerSkinHandler.isEmptySkin(skin)) {
            PlayerSkin currentSkin = cir.getReturnValue();
            cir.setReturnValue(currentSkin.with(PlayerSkin.Patch.create(
                    Optional.ofNullable(skin.skinLocation()).map(ClientAsset.ResourceTexture::new),
                    Optional.ofNullable(skin.capeLocation()).map(ClientAsset.ResourceTexture::new),
                    Optional.ofNullable(skin.elytraLocation()).map(ClientAsset.ResourceTexture::new),
                    Optional.of(skin.slim() ? PlayerModelType.SLIM : PlayerModelType.WIDE)
            )));
        }
    }
}
