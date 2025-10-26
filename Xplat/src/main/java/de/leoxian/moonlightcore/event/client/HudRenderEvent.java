package de.leoxian.moonlightcore.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

public interface HudRenderEvent {
     /**
      * @see Pre#onPreHudRendering(GuiGraphics, PoseStack, Context, float) 
      */
     Event<Pre> PRE = EventFactory.createWithResult(Pre.class);
     /**
      * @see Post#onPostHudRendering(GuiGraphics, PoseStack, Context, float) 
      */
     Event<Post> POST = EventFactory.create(Post.class);

     interface Pre {
          /**
           * Invoked before rendering the HUD, can be used to cancel all the hud rendering (or to render custom elements before others)
           * @param guiGraphics The gui graphics to used draw something
           * @param poseStack The pose stack of the GUI
           * @param context A context that can given some util things
           * @param partialTick The partial tick value used to render
           */
          Event.Result onPreHudRendering(GuiGraphics guiGraphics, PoseStack poseStack, Context context, float partialTick);
     }

     interface Post {
          /**
           * Invoked after all the hud rendering, can be used to render custom elements
           * @param guiGraphics The gui graphics to used draw something
           * @param poseStack The pose stack of the GUI
           * @param context A context that can given some util things
           * @param partialTick The partial tick value used to render
           */
          void onPostHudRendering(GuiGraphics guiGraphics, PoseStack poseStack, Context context, float partialTick);
     }

     interface Context {
          default Minecraft minecraft() {
               return Minecraft.getInstance();
          }

          default LocalPlayer player() {
               return this.minecraft().player;
          }

          default Gui gui() {
               return this.minecraft().gui;
          }

          default int screenWidth() {
               return this.minecraft().getWindow().getScreenWidth();
          }

          default int screenHeight() {
               return this.minecraft().getWindow().getScreenHeight();
          }
     }
}
