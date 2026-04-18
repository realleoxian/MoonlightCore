package de.realleoxian.moonlightcore.forge.client.event;

import com.mojang.blaze3d.shaders.FogShape;
import de.realleoxian.moonlightcore.api.client.event.*;
import de.realleoxian.moonlightcore.api.client.model.BlockStateModelModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeClientEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        ModelEvents.RegisterModelsLocation.Context ctx = event::register;
        ModelEvents.REGISTER_MODELS_LOCATION.invoker().onRegisterModelsLocation(ctx);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ModelEvents.AfterBaking.Context ctx = new ModelEvents.AfterBaking.Context() {
            @Override
            public void setModel(ResourceLocation location, BakedModel model) {
                event.getModels().put(location, model);
            }

            @Override
            public void modifyBlockStateModels(Block block, BlockStateModelModifier modelModifier) {
                modelModifier.applyBlockStateModels(new BlockStateModelModifier.Context() {
                    @Override
                    public void replace(BlockState state, BakedModel model) {
                        if (!getBlock().getStateDefinition().getPossibleStates().contains(state)) {
                            throw new IllegalArgumentException("BlockState %s isn't present on block %s".formatted(state, BuiltInRegistries.BLOCK.getKey(getBlock())));
                        }

                        ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
                        setModel(location, model);
                    }

                    @Override
                    public Block getBlock() {
                        return block;
                    }
                });
            }

            @Override
            public ModelBakery getModelBakery() {
                return event.getModelBakery();
            }

            @Override
            public @UnmodifiableView Map<ResourceLocation, BakedModel> getModels() {
                return Map.copyOf(event.getModels());
            }
        };

        ModelEvents.AFTER_BAKING.invoker().onAfterBaking(ctx);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START -> ClientTickEvents.TICK_START.invoker().onStartClientTick(Minecraft.getInstance());
            case END -> ClientTickEvents.TICK_END.invoker().onEndClientTick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKeyInput(InputEvent.Key event) {
        InputEvents.KEY_INPUT.invoker().onKeyInput(event.getKey(), event.getAction(), event.getModifiers(), event.getScanCode());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseInput(InputEvent.MouseButton event) {
        InputEvents.MOUSE_INPUT.invoker().onMouseInput(event.getButton(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onStartGuiRender(RenderGuiEvent.Pre event) {
        if (GuiRenderEvents.GUI_RENDER_START.invoker().onGuiRenderStart(event.getGuiGraphics(), event.getPartialTick()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEndGuiRender(RenderGuiEvent.Post event) {
        GuiRenderEvents.GUI_RENDER_END.invoker().onGuiRenderEnd(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFogRender(ViewportEvent.RenderFog event) {
        ViewportEvents.RenderFog.Context context = new ViewportEvents.RenderFog.Context() {
            float start = event.getNearPlaneDistance();
            float end = event.getFarPlaneDistance();
            FogShape shape = event.getFogShape();

            @Override
            public void setStartDistance(float distance) {
                this.start = distance;
            }

            @Override
            public void setEndDistance(float distance) {
                this.end = distance;
            }

            @Override
            public void setShape(FogShape shape) {
                this.shape = shape;
            }

            @Override
            public float getStartDistance() {
                return this.start;
            }

            @Override
            public float getEndDistance() {
                return this.end;
            }

            @Override
            public FogShape getShape() {
                return this.shape;
            }

            @Override
            public FogRenderer.FogMode getMode() {
                return event.getMode();
            }
        };

        if (ViewportEvents.RENDER_FOG.invoker().onRenderFog(event.getRenderer(), event.getCamera(), context, (float) event.getPartialTick()).isTrue()) {
            event.setFarPlaneDistance(context.getEndDistance());
            event.setNearPlaneDistance(context.getStartDistance());
            event.setFogShape(context.getShape());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        ViewportEvents.ComputeFogColor.Context context = new ViewportEvents.ComputeFogColor.Context() {
            float red = event.getRed();
            float green = event.getGreen();
            float blue = event.getBlue();

            @Override
            public void setRed(float red) {
                this.red = red;
            }

            @Override
            public void setGreen(float green) {
                this.green = green;
            }

            @Override
            public void setBlue(float blue) {
                this.blue = blue;
            }

            @Override
            public float getRed() {
                return this.red;
            }

            @Override
            public float getGreen() {
                return this.green;
            }

            @Override
            public float getBlue() {
                return this.blue;
            }
        };

        if (ViewportEvents.COMPUTE_FOG_COLOR.invoker().onComputeFogColor(event.getRenderer(), event.getCamera(), context, (float) event.getPartialTick()).isTrue()) {
            event.setRed(context.getRed());
            event.setGreen(context.getGreen());
            event.setBlue(context.getBlue());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onComputeCameraAngle(ViewportEvent.ComputeCameraAngles event) {
        ViewportEvents.ComputeCameraAngle.Context context = new ViewportEvents.ComputeCameraAngle.Context() {
            float yaw = event.getYaw();
            float pitch = event.getPitch();
            float roll = event.getRoll();

            @Override
            public void setYaw(float yaw) {
                this.yaw = yaw;
            }

            @Override
            public void setPitch(float pitch) {
                this.pitch = pitch;
            }

            @Override
            public void setRoll(float roll) {
                this.roll = roll;
            }

            @Override
            public float getYaw() {
                return this.yaw;
            }

            @Override
            public float getPitch() {
                return this.pitch;
            }

            @Override
            public float getRoll() {
                return this.roll;
            }
        };

        if (ViewportEvents.COMPUTE_CAMERA_ANGLE.invoker().onComputeCameraAngle(event.getRenderer(), event.getCamera(), context, (float) event.getPartialTick()).isTrue()) {
            event.setYaw(context.getYaw());
            event.setPitch(context.getPitch());
            event.setRoll(context.getRoll());
        }
    }
}
