package de.leoxian.moonlightcore.neoforge.common.event;

import de.leoxian.moonlightcore.common.event.*;
import de.leoxian.moonlightcore.common.event.base.CompoundEventResult;
import de.leoxian.moonlightcore.common.network.PacketSender;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkDataEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@EventBusSubscriber
public final class CommonEventHandler {
    @SubscribeEvent
    public static void onArmorHurtEvent(net.neoforged.neoforge.event.entity.living.ArmorHurtEvent event) {
        ArmorHurtEvent.Context context = new ArmorHurtEvent.Context() {
            @Override
            public ItemStack getArmorItemStack(EquipmentSlot slot) {
                return event.getArmorItemStack(slot);
            }

            @Override
            public float getOriginalDamage(EquipmentSlot slot) {
                return event.getOriginalDamage(slot);
            }

            @Override
            public float getDamage(EquipmentSlot slot) {
                return event.getNewDamage(slot);
            }

            @Override
            public void setDamage(EquipmentSlot slot, float damage) {
                event.setNewDamage(slot, damage);
            }
        };

        if (ArmorHurtEvent.EVENT.doFire().onAmorHurt(event.getEntity(), event.getDamageSource(), context).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            ChunkDataEvents.LOAD.doFire().onChunkDataLoad(serverLevel, event.getChunk(), event.getData());
        }
    }

    @SubscribeEvent
    public static void onChunkDataSave(ChunkDataEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            ChunkDataEvents.SAVE.doFire().onChunkDataSave(serverLevel, event.getChunk(), event.getData());
        }
    }

    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent event) {
        if (EntityInvulnerabilityCheckEvent.EVENT.doFire().onEntityInvulnerabilityCheck(event.getEntity(), event.getSource(), event.getOriginalInvulnerability()).isTrue()) {
            event.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        if (ItemEntityPickupEvents.PRE.doFire().onPreItemEntityPickup(event.getPlayer(), event.getItemEntity()).isDeny()) {
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onPostItemPickup(ItemEntityPickupEvent.Post event) {
        ItemEntityPickupEvents.POST.doFire().onPostItemEntityPickup(event.getPlayer(), event.getItemEntity());
    }

    @SubscribeEvent
    public static void onItemToss(net.neoforged.neoforge.event.entity.item.ItemTossEvent event) {
        if (ItemTossEvent.EVENT.doFire().onItemToss(event.getPlayer(), event.getEntity()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPreLivingDamage(LivingDamageEvent.Pre event) {
        LivingDamageEvents.PRE.doFire().onPreLivingDamage(event.getEntity(), event.getSource(), event.getOriginalDamage());
    }

    @SubscribeEvent
    public static void onPostLivingDamage(LivingDamageEvent.Post event) {
        LivingDamageEvents.POST.doFire().onPostLivingDamage(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void onLivingDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (LivingDeathEvent.EVENT.doFire().onLivingDeath(event.getEntity(), event.getSource()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (LivingIncomingDamageEvent.EVENT.doFire().onLivingIncomingDamage(event.getEntity(), event.getSource(), event.getAmount()).isDeny()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onStartLivingItemUse(LivingEntityUseItemEvent.Start event) {
        if (LivingUseItemEvents.START.doFire().onItemUseStart(event.getEntity(), event.getItem(), event.getHand(), event.getDuration()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onTickLivingItemUse(LivingEntityUseItemEvent.Tick event) {
        if (LivingUseItemEvents.TICK.doFire().onItemUseTick(event.getEntity(), event.getItem(), event.getHand(), event.getDuration()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onStopLivingItemUse(LivingEntityUseItemEvent.Stop event) {
        if (LivingUseItemEvents.STOP.doFire().onItemUseStop(event.getEntity(), event.getItem(), event.getHand(), event.getDuration()).isFalse()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFinishLivingItemUse(LivingEntityUseItemEvent.Finish event) {
        CompoundEventResult<ItemStack> result = LivingUseItemEvents.FINISH.doFire().onItemUseFinish(event.getEntity(), event.getItem(), event.getDuration());
        if (result.result().isSuccess()) {
            event.setResultStack(result.value());
        }
    }

    @SubscribeEvent
    public static void onNewRegistry(net.neoforged.neoforge.registries.NewRegistryEvent event) {
        NewRegistryEvent.EVENT.doFire().onNewRegistryEvent(event::register);
    }

    @SubscribeEvent
    public static void onDatapackSync(net.neoforged.neoforge.event.OnDatapackSyncEvent event) {
        OnDatapackSyncEvent.EVENT.doFire().onDatapackSync(event.getPlayerList(), event.getPlayer(), event.getRelevantPlayers());
    }

    @SubscribeEvent
    public static void onRegisterEvent(net.neoforged.neoforge.registries.RegisterEvent event) {
        RegisterEvent.EVENT.doFire().onRegister(event.getRegistryKey(), new RegisterEvent.Output() {
            @Override
            public <T> void register(Identifier id, Supplier<T> value) {
                event.register((ResourceKey<? extends Registry<T>>) event.getRegistryKey(), id, value);
            }
        });
    }

    @SubscribeEvent
    public static void onServerBlockEntityLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            event.getChunk().getBlockEntities().forEach((blockPos, blockEntity) ->
                    ServerBlockEntityEvents.LOAD.doFire().onBlockEntityLoad(level, blockEntity));
        }
    }

    @SubscribeEvent
    public static void onServerBlockEntitySAve(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            event.getChunk().getBlockEntities().forEach((blockPos, blockEntity) ->
                    ServerBlockEntityEvents.UNLOAD.doFire().onBlockEntityUnload(level, blockEntity));
        }
    }

    @SubscribeEvent
    public static void onServerChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerChunkEvents.LOAD.doFire().onChunkLoad(level, event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onServerChunkSave(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerChunkEvents.UNLOAD.doFire().onChunkUnload(level, event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onEntityLoadEvent(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerEntityEvents.LOAD.doFire().onEntityLoad(level, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityUnloadEvent(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerEntityEvents.UNLOAD.doFire().onEntityUnload(level, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevelEvents.LOAD.doFire().onLevelLoad(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevelEvents.UNLOAD.doFire().onLevelUnload(level);
        }
    }

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevelEvents.SAVE.doFire().onLevelSave(level);
        }
    }

    @SubscribeEvent
    public static void onTickEvent(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevelTickEvents.START.doFire().onServerLevelTickStart(level);
        }
    }

    @SubscribeEvent
    public static void onTickEvent(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ServerLevelTickEvents.END.doFire().onServerLevelTickEnd(level);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLifecycleEvents.STARTING.doFire().onServerStarting(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.doFire().onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.doFire().onServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.doFire().onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerPlayConnectionEvents.JOIN.doFire().onPlayReady(serverPlayer.connection, new PacketSender() {
                @Override
                public Packet<?> createPacket(CustomPacketPayload payload) {
                    return new ClientboundCustomPayloadPacket(payload);
                }

                @Override
                public void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback) {
                    serverPlayer.connection.send(packet, callback);
                }

                @Override
                public void disconnect(Component reason) {
                    serverPlayer.connection.disconnect(reason);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerPlayConnectionEvents.DISCONNECT.doFire().onPlayDisconnect(serverPlayer.connection);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer original && event.getEntity() instanceof ServerPlayer player) {
            ServerPlayerEvents.CLONE.doFire().onPlayerClone(original, player, event.isWasDeath());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayerEvents.AFTER_RESPAWN.doFire().onPlayerRespawn(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        if  (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerPlayerEvents.CHANGE_DIMENSION.doFire().onChangeDimension(serverPlayer, event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void onPrePlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayerTickEvents.START.doFire().onServerPlayerTickStart(player);
        }
    }

    @SubscribeEvent
    public static void onPostPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayerTickEvents.END.doFire().onServerPlayerTickEnd(player);
        }
    }

    @SubscribeEvent
    public static void onPreServerTick(ServerTickEvent.Pre event) {
        ServerTickEvents.START.doFire().onServerTickStart(event.getServer());
    }

    @SubscribeEvent
    public static void onPostServerTick(ServerTickEvent.Post event) {
        ServerTickEvents.END.doFire().onServerTickEnd(event.getServer());
    }

    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent.ServerDataLoad event) {
        TagsUpdatedEvents.SERVER_DATA_LOAD.doFire().onServerDataLoad(event.getRegistries(), event.getServerResources());
    }

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (VanillaGameEventCallback.EVENT.doFire().onVanillaGameEvent(level, event.getVanillaEvent(), event.getContext(), event.getEventPosition()).isFalse()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterConfigurationTasks(net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent event) {
        RegisterConfigurationTasksEvent.EVENT.doFire().onConfigure(event.getListener(), new RegisterConfigurationTasksEvent.Context() {
            @Override
            public void addTask(ConfigurationTask task) {
                event.register(task);
            }
        });
    }

    private CommonEventHandler() {}
}
