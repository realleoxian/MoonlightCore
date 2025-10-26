package de.leoxian.moonlightcore.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public interface LevelRenderEvent {
     /**
      * @see #onLevelRendering(Stage, LevelRenderer, Camera, Frustum, PoseStack, Matrix4f, int, float)
      */
     Event<LevelRenderEvent> EVENT = EventFactory.create(LevelRenderEvent.class);

     /**
      * Invoked on each {@link Stage} of the level rendering
      * @param stage The current stage of the level rendering
      * @param renderer The instance of the level renderer
      * @param camera The camera
      * @param frustum The frustum
      * @param poseStack The pose stack used for rendering
      * @param projectionMatrix The projection matrix
      * @param renderTick The current "render ticks" value in the level renderer
      * @param partialTick The current partial tick value used for rendering
      */
     void onLevelRendering(Stage stage, LevelRenderer renderer, Camera camera, Frustum frustum, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTick);

     enum Stage {
          AFTER_SKY("after_sky", null),
          AFTER_SOLID_BLOCKS("after_solid_blocks", RenderType.solid()),
          AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS("after_cutout_mipped_blocks_blocks", RenderType.cutoutMipped()),
          AFTER_CUTOUT_BLOCKS("after_cutout_blocks", RenderType.cutoutMipped()),
          AFTER_ENTITIES("after_entities", null),
          AFTER_BLOCK_ENTITIES("after_block_entities", null),
          AFTER_TRANSLUCENT_BLOCKS("after_translucent_blocks", RenderType.translucent()),
          AFTER_PARTICLES("after_particles", null),
          AFTER_WEATHER("after_weather", null),
          AFTER_LEVEL("after_level", null)
          ;
          private static final Map<RenderType, Stage> STAGES_BY_RENDER_TYPE = new HashMap<>();

          public static Stage byRenderType(RenderType renderType) {
               return STAGES_BY_RENDER_TYPE.get(renderType);
          }

          static {
               for(Stage stage : Stage.values()) {
                    if(stage.renderType !=  null) {
                         STAGES_BY_RENDER_TYPE.put(stage.renderType, stage);
                    }
               }
          }

          public final String name;
          @Nullable
          private final RenderType renderType;

          Stage(String name, @Nullable RenderType renderType) {
               this.name = "mlcore:" + name;
               this.renderType = renderType;
          }
     }
}
