package de.leoxian.moonlightcore.forge.handler;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.brigadier.CommandDispatcher;
import de.leoxian.moonlightcore.event.client.ClientTickEvent;
import de.leoxian.moonlightcore.event.client.HudRenderEvent;
import de.leoxian.moonlightcore.event.client.RegisterClientCommandEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onClientTick(TickEvent.ClientTickEvent event) {
          de.leoxian.moonlightcore.event.common.TickEvent.Phase phase = event.phase == TickEvent.Phase.START ? de.leoxian.moonlightcore.event.common.TickEvent.Phase.START : de.leoxian.moonlightcore.event.common.TickEvent.Phase.END;

          ClientTickEvent.CLIENT_TICK.invoker().onClientTick(phase);
     }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onClientCommandRegistration(RegisterClientCommandsEvent event) {
        RegisterClientCommandEvent.EVENT.invoker().onClientCommandRegistration((CommandDispatcher<RegisterClientCommandEvent.ClientCommandSourceStack>) (CommandDispatcher<?>) event.getDispatcher(), event.getBuildContext());
    }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPreHudRendering(RenderGuiEvent.Pre event) {
          if(HudRenderEvent.PRE.invoker().onPreHudRendering(event.getGuiGraphics(), event.getGuiGraphics().pose(), new HudRenderEvent.Context() {}, event.getPartialTick()).isFalse()) {
               event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPostHudRendering(RenderGuiEvent.Post event) {
          HudRenderEvent.POST.invoker().onPostHudRendering(event.getGuiGraphics(), event.getGuiGraphics().pose(), new HudRenderEvent.Context() {}, event.getPartialTick());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onViewportComputeFogColor(ViewportEvent.ComputeFogColor event) {
          de.leoxian.moonlightcore.event.client.ViewportEvent.FogColorCompute.Context context = new de.leoxian.moonlightcore.event.client.ViewportEvent.FogColorCompute.Context() {
               @Override
               public float getRed() {
                    return event.getRed();
               }

               @Override
               public float getGreen() {
                    return event.getGreen();
               }

               @Override
               public float getBlue() {
                    return event.getBlue();
               }

               @Override
               public void setRed(float red) {
                    event.setRed(red);
               }

               @Override
               public void setGreen(float green) {
                    event.setGreen(green);
               }

               @Override
               public void setBlue(float blue) {
                    event.setBlue(blue);
               }
          };

          de.leoxian.moonlightcore.event.client.ViewportEvent.FOG_COLOR_COMPUTE.invoker().onColorCompute(event.getRenderer(), context, (float) event.getPartialTick());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onViewportFogRender(ViewportEvent.RenderFog event) {
          de.leoxian.moonlightcore.event.client.ViewportEvent.RenderFog.Context context = new de.leoxian.moonlightcore.event.client.ViewportEvent.RenderFog.Context() {
               @Override
               public void setFarPlaneDistance(float distance) {
                    event.setFarPlaneDistance(distance);
               }

               @Override
               public void setNearPlaneDistance(float distance) {
                    event.setNearPlaneDistance(distance);
               }

               @Override
               public void setFogShape(FogShape shape) {
                    event.setFogShape(shape);
               }

               @Override
               public float getFarPlaneDistance() {
                    return event.getFarPlaneDistance();
               }

               @Override
               public float getNearPlaneDistance() {
                    return event.getNearPlaneDistance();
               }

               @Override
               public Camera getCamera() {
                    return event.getCamera();
               }

               @Override
               public FogShape getFogShape() {
                    return event.getFogShape();
               }

               @Override
               public FogRenderer.FogMode getMode() {
                    return event.getMode();
               }

               @Override
               public FogType getType() {
                    return event.getType();
               }
          };

          if(de.leoxian.moonlightcore.event.client.ViewportEvent.RENDER_FOG.invoker().onFogRendering(event.getRenderer(), context, (float) event.getPartialTick()).isFalse()) {
               event.setCanceled(true);
          }
     }
}
