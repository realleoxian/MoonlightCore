package de.realleoxian.moonlightcore.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;

public interface LevelRenderEvents {
    EventBus<LevelRenderEvents> AFTER_SKY = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
       for (LevelRenderEvents listener : listeners) {
           listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
       }
    });
    EventBus<LevelRenderEvents> AFTER_ENTITIES = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });
    EventBus<LevelRenderEvents> AFTER_BLOCK_ENTITIES = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });
    EventBus<LevelRenderEvents> AFTER_TRANSLUCENT_BLOCKS = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });
    EventBus<LevelRenderEvents> AFTER_PARTICLES = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });
    EventBus<LevelRenderEvents> AFTER_WEATHER = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });
    EventBus<LevelRenderEvents> AFTER_LEVEL = EventBus.create(LevelRenderEvents.class, (listeners) -> (renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick) -> {
        for (LevelRenderEvents listener : listeners) {
            listener.onLevelRender(renderer, camera, frustum, poseStack, projectionMatrix, renderTick, partialTick);
        }
    });


    void onLevelRender(LevelRenderer renderer, Camera camera, Frustum frustum, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTick);
}
