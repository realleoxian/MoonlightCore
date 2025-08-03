package de.leowgc.moonlightcore.api.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.util.SidedEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;

@FunctionalInterface
@SidedEnvironment(SidedEnvironment.Environment.CLIENT)
public interface HudRenderEvent {
    Event<HudRenderEvent> HUD_RENDER = Event.create();

    void bootstrap(Context context, PoseStack poseStack, float partialTick);

    interface Context {
        default Minecraft minecraft() {
            return Minecraft.getInstance();
        }

        default LocalPlayer player() {
            return minecraft().player;
        }

        default Gui gui() {
            return minecraft().gui;
        }

        default int screenWidth() {
            return minecraft().getWindow().getScreenWidth();
        }

        default int screenHeight() {
            return minecraft().getWindow().getScreenHeight();
        }
    }
}
