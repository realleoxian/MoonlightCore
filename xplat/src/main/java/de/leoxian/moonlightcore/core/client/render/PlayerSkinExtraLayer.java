package de.leoxian.moonlightcore.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.common.playerskin.MlPlayerSkin;
import de.leoxian.moonlightcore.common.playerskin.PlayerSkinHandler;
import de.leoxian.moonlightcore.core.client.render.state.PlayerSkinRenderState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class PlayerSkinExtraLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public PlayerSkinExtraLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, AvatarRenderState avatarRenderState, float v, float v1) {
        PlayerSkinRenderState renderState = PlayerSkinRenderState.from(avatarRenderState);
        MlPlayerSkin skin = renderState.mlPlayerSkin;
        if (PlayerSkinHandler.isEmptySkin(skin)) {
            return;
        }

        skin.submitExtra(poseStack, submitNodeCollector, i, avatarRenderState, v, v1);
    }
}
