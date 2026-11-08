package de.leoxian.moonlightcore.common.playerskin;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.core.MoonlightCore;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class MlPlayerSkin {
    public static final MlPlayerSkin EMPTY = new MlPlayerSkin(MoonlightCore.id("empty"));

    private final Identifier id;

    private @Nullable Identifier skinLocation = null;
    private @Nullable Identifier capeLocation = null;
    private @Nullable Identifier elytraLocation = null;
    private boolean slim = false;

    public MlPlayerSkin(Identifier id) {
        this.id = id;
    }

    @ApiStatus.OverrideOnly
    public void submitExtra(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
    }

    @ApiStatus.OverrideOnly
    public void tick(Player player) {
    }

    public MlPlayerSkin slim(boolean slim) {
        this.slim = slim;
        return this;
    }

    public boolean slim() {
        return this.slim;
    }

    public MlPlayerSkin skinLocation(@Nullable Identifier skinLocation) {
        this.skinLocation = skinLocation;
        return this;
    }

    public @Nullable Identifier skinLocation() {
        return this.skinLocation;
    }

    public MlPlayerSkin capeLocation(@Nullable Identifier capeLocation) {
        this.capeLocation = capeLocation;
        return this;
    }

    public @Nullable Identifier capeLocation() {
        return this.capeLocation;
    }

    public MlPlayerSkin elytraLocation(@Nullable Identifier elytraLocation) {
        this.elytraLocation = elytraLocation;
        return this;
    }

    public @Nullable Identifier elytraLocation() {
        return this.elytraLocation;
    }

    public Identifier id() {
        return this.id;
    }
}
