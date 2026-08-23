package de.leoxian.moonlightcore.neoforge.client.event;

import de.leoxian.moonlightcore.client.event.*;
import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onBlockEntityLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            event.getChunk().getBlockEntities().values().forEach(blockEntity ->
                    ClientBlockEntityEvents.LOAD.doFire().onBlockEntityLoad(level, blockEntity));
        }
    }

    @SubscribeEvent
    public static void onBlockEntityUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            event.getChunk().getBlockEntities().values().forEach(blockEntity ->
                    ClientBlockEntityEvents.UNLOAD.doFire().onBlockEntityUnload(level, blockEntity));
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientChunkEvents.LOAD.doFire().onChunkLoad(level, event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientChunkEvents.UNLOAD.doFire().onChunkUnload(level, event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onEntityLoad(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientEntityEvents.LOAD.doFire().onEntityLoad(level, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityUnload(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientEntityEvents.UNLOAD.doFire().onEntityUnload(level, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientLevelEvents.LOAD.doFire().onLevelLoad(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientLevelEvents.UNLOAD.doFire().onLevelUnload(level);
        }
    }

    @SubscribeEvent
    public static void onPreLevelTick(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientLevelTickEvents.START.doFire().onLevelTickStart(level);
        }
    }

    @SubscribeEvent
    public static void onPostLevelTick(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ClientLevel level) {
            ClientLevelTickEvents.END.doFire().onLevelTickEnd(level);
        }
    }

    @SubscribeEvent
    public static void onClientStarted(ClientStartedEvent event) {
        ClientLifecycleEvents.STARTED.doFire().onClientStarted(event.getClient());
    }

    @SubscribeEvent
    public static void onClientStopped(ClientStoppingEvent event) {
        ClientLifecycleEvents.STOPPING.doFire().onClientStopping(event.getClient());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPlayConnectionEvents.JOIN.doFire().onPlayJoin(event.getPlayer().connection, Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPlayConnectionEvents.DISCONNECT.doFire().onPlayDisconnect(Minecraft.getInstance().getConnection(), Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPreClientTick(ClientTickEvent.Pre event) {
        ClientTickEvents.START.doFire().onClientTickStart(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPostClientTick(ClientTickEvent.Post event) {
        ClientTickEvents.END.doFire().onClientTickEnd(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        int key = event.getKey();
        int scancode = event.getScanCode();
        int modifiers = event.getModifiers();
        int action = event.getAction();

        if (InputEvents.PRE_KEY_PRESS.doFire().onPreMouseInput(key, scancode, modifiers, action).isSuccess()) {
            InputEvents.POST_KEY_PRESS.doFire().onPostKeyInput(key, scancode, modifiers, action);
        }
    }

    @SubscribeEvent
    public static void onPreMouseInput(InputEvent.MouseButton.Pre event) {
        int button = event.getButton();
        int modifiers = event.getModifiers();
        int action = event.getAction();

        if (InputEvents.PRE_MOUSE_INPUT.doFire().onPreMouseInput(button, modifiers, action).isDeny()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostMouseInput(InputEvent.MouseButton.Post event) {
        int button = event.getButton();
        int modifiers = event.getModifiers();
        int action = event.getAction();

        InputEvents.POST_MOUSE_INPUT.doFire().onPostMouseInput(button, modifiers, action);
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        double deltaScrollX = event.getScrollDeltaX();
        double deltaScrollY = event.getScrollDeltaY();
        boolean isLeftDown = event.isLeftDown();
        boolean isMiddleDown = event.isMiddleDown();
        boolean isRightDown = event.isRightDown();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        if (InputEvents.MOUSE_SCROLL.doFire().onMouseScroll(deltaScrollX, deltaScrollY, isLeftDown, isMiddleDown, isRightDown, mouseX, mouseY).isDeny()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        FogData originalData = event.getFogData();

        CompoundEventResult<FogData> result = ViewportEvents.RENDER_FOG.doFire().onRenderFog(event.getRenderer(), event.getCamera(), (float) event.getPartialTick(), event.getEnvironment(), originalData);

        if (result.result().isSuccess() && result.isValuePresent() && result.value() != originalData) {
            FogData data = result.value();

            originalData.environmentalStart = data.environmentalStart;
            originalData.environmentalEnd = data.environmentalEnd;
            originalData.skyEnd = data.skyEnd;
            originalData.cloudEnd = data.cloudEnd;
            originalData.renderDistanceStart = data.renderDistanceStart;
            originalData.renderDistanceEnd = data.renderDistanceEnd;
            originalData.color = data.color;

            event.setNearPlaneDistance(data.renderDistanceStart);
            event.setFarPlaneDistance(data.renderDistanceEnd);
        }
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        ViewportEvents.ComputeFogColor.Context context = new ViewportEvents.ComputeFogColor.Context() {
            float red = event.getRed();
            float green = event.getGreen();
            float blue = event.getBlue();

            @Override
            public void red(float red) {
                this.red = red;
            }

            @Override
            public float red() {
                return red;
            }

            @Override
            public void green(float green) {
                this.green = green;
            }

            @Override
            public float green() {
                return green;
            }

            @Override
            public void blue(float blue) {
                this.blue = blue;
            }

            @Override
            public float blue() {
                return blue;
            }
        };

        if (ViewportEvents.COMPUTE_FOG_COLOR.doFire().onComputeFogColor(event.getRenderer(), event.getCamera(), (float) event.getPartialTick(), context).isSuccess()) {
            event.setRed(context.red());
            event.setGreen(context.green());
            event.setBlue(context.blue());
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        ViewportEvents.ComputeCameraAngles.Context context = new ViewportEvents.ComputeCameraAngles.Context() {
            float yaw = event.getYaw();
            float pitch = event.getPitch();
            float roll = event.getRoll();

            @Override
            public void yaw(float yaw) {
                this.yaw = yaw;
            }

            @Override
            public float yaw() {
                return this.yaw;
            }

            @Override
            public void pitch(float pitch) {
                this.pitch = pitch;
            }

            @Override
            public float pitch() {
                return this.pitch;
            }

            @Override
            public void roll(float roll) {
                this.roll = roll;
            }

            @Override
            public float roll() {
                return this.roll;
            }
        };

        if (ViewportEvents.COMPUTE_CAMERA_ANGLES.doFire().onComputeCameraAngles(event.getRenderer(), event.getCamera(), (float) event.getPartialTick(), context).isSuccess()) {
            event.setYaw(context.yaw());
            event.setPitch(context.pitch());
            event.setRoll(context.roll());
        }
    }
}