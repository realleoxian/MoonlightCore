package de.leoxian.moonlightcore.event.client;

import com.mojang.blaze3d.shaders.FogShape;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;

public interface ViewportEvent {
     /**
      * @see RenderFog#onFogRendering(GameRenderer, RenderFog.Context, float)
      */
     Event<RenderFog> RENDER_FOG = EventFactory.createWithResult(RenderFog.class);
     /**
      * @see FogColorCompute#onColorCompute(GameRenderer, FogColorCompute.Context, float)
      */
     Event<FogColorCompute> FOG_COLOR_COMPUTE = EventFactory.createWithResult(FogColorCompute.class);

     interface RenderFog {
          /**
           * Invoked for rendering custom fog
           * @param renderer The game renderer
           * @param context the context of the fog renderer
           * @param partialTick The current partial tick value of the fog renderer
           */
          Event.Result onFogRendering(GameRenderer renderer, Context context, float partialTick);

          interface Context {
              void setFarPlaneDistance(float distance);

              void setNearPlaneDistance(float distance);

              void setFogShape(FogShape shape);

              float getFarPlaneDistance();

              float getNearPlaneDistance();

              Camera getCamera();

              FogShape getFogShape();

              FogRenderer.FogMode getMode();

              FogType getType();
          }
     }

     interface FogColorCompute {
          /**
           * Invoked when computing the current color of the fog. Use this event to customize the fog color
           * @param renderer The game renderer
           * @param context The context of the fog color
           * @param partialTick The current partial tick value of the fog renderer
           */
          Event.Result onColorCompute(GameRenderer renderer, Context context, float partialTick);

          interface Context {
               float getRed();

               float getGreen();

               float getBlue();

               void setRed(float red);

               void setGreen(float green);

               void setBlue(float blue);
          }
     }

}
