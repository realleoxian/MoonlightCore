package de.realleoxian.moonlightcore.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

public final class LevelRenderEvents extends EventBase {
    public static final Event<LevelRenderEvents> AFTER_SKY = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_ENTITIES = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_BLOCK_ENTITIES = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_TRANSLUCENT_BLOCKS = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_PARTICLES = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_WEATHER = Event.create(LevelRenderEvents.class);
    public static final Event<LevelRenderEvents> AFTER_LEVEL = Event.create(LevelRenderEvents.class);

    public final LevelRenderer renderer;
    public final Camera camera;
    public final Frustum frustum;
    public final PoseStack poseStack;
    public final Matrix4f projectionMatrix;
    public final int renderTick;
    public final DeltaTracker partialTick;

    public LevelRenderEvents(LevelRenderer renderer, Camera camera, Frustum frustum, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, DeltaTracker partialTick) {
        this.renderer = renderer;
        this.camera = camera;
        this.frustum = frustum;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
    }
}
