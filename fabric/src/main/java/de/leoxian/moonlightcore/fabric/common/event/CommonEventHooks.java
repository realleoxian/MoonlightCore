package de.leoxian.moonlightcore.fabric.common.event;

import de.leoxian.moonlightcore.common.event.ArmorHurtEvent;
import de.leoxian.moonlightcore.common.event.RegisterConfigurationTasksEvent;
import de.leoxian.moonlightcore.common.event.ServerLevelTickEvents;
import de.leoxian.moonlightcore.common.network.PacketSender;
import io.netty.channel.ChannelFutureListener;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public final class CommonEventHooks {
    public static void bindFabricApiEvents() {
        ServerConfigurationConnectionEvents.CONFIGURE.register((listener, server) -> {
            RegisterConfigurationTasksEvent.Context context = listener::addTask;
            RegisterConfigurationTasksEvent.EVENT.doFire().onConfigure(listener, context);
        });

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, level) ->
                de.leoxian.moonlightcore.common.event.ServerBlockEntityEvents.LOAD.doFire().onBlockEntityLoad(level, be));
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, level) ->
                de.leoxian.moonlightcore.common.event.ServerBlockEntityEvents.UNLOAD.doFire().onBlockEntityUnload(level, be));

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, generated) ->
                de.leoxian.moonlightcore.common.event.ServerChunkEvents.LOAD.doFire().onChunkLoad(level, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
                de.leoxian.moonlightcore.common.event.ServerChunkEvents.UNLOAD.doFire().onChunkUnload(level, chunk));

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) ->
                de.leoxian.moonlightcore.common.event.ServerEntityEvents.LOAD.doFire().onEntityLoad(level, entity));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
                de.leoxian.moonlightcore.common.event.ServerEntityEvents.UNLOAD.doFire().onEntityUnload(level, entity));

        ServerLevelEvents.LOAD.register((server, level) ->
                de.leoxian.moonlightcore.common.event.ServerLevelEvents.LOAD.doFire().onLevelLoad(level));
        ServerLevelEvents.UNLOAD.register((server, level) ->
                de.leoxian.moonlightcore.common.event.ServerLevelEvents.UNLOAD.doFire().onLevelUnload(level));

        ServerTickEvents.START_LEVEL_TICK.register((level) -> ServerLevelTickEvents.START.doFire().onServerLevelTickStart(level));
        ServerTickEvents.END_LEVEL_TICK.register((level) -> ServerLevelTickEvents.END.doFire().onServerLevelTickEnd(level));
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> de.leoxian.moonlightcore.common.event.ServerLifecycleEvents.STARTING.doFire().onServerStarting(server));
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> de.leoxian.moonlightcore.common.event.ServerLifecycleEvents.STARTED.doFire().onServerStarted(server));
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> de.leoxian.moonlightcore.common.event.ServerLifecycleEvents.STOPPING.doFire().onServerStopping(server));
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> de.leoxian.moonlightcore.common.event.ServerLifecycleEvents.STOPPED.doFire().onServerStopped(server));

        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) ->
                de.leoxian.moonlightcore.common.event.ServerPlayConnectionEvents.JOIN.doFire().onPlayReady(listener, new PacketSender() {
                    @Override
                    public Packet<?> createPacket(CustomPacketPayload payload) {
                        return new ClientboundCustomPayloadPacket(payload);
                    }

                    @Override
                    public void sendPacket(Packet<?> packet, @Nullable ChannelFutureListener callback) {
                        listener.send(packet, callback);
                    }

                    @Override
                    public void disconnect(Component reason) {
                        listener.disconnect(reason);
                    }
                })
        );
        ServerPlayConnectionEvents.DISCONNECT.register((listener, server) ->
                de.leoxian.moonlightcore.common.event.ServerPlayConnectionEvents.DISCONNECT.doFire().onPlayDisconnect(listener));

        ServerPlayerEvents.COPY_FROM.register((old, newPlayer, alive) ->
                de.leoxian.moonlightcore.common.event.ServerPlayerEvents.CLONE.doFire().onPlayerClone(old, newPlayer, !alive));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                de.leoxian.moonlightcore.common.event.ServerPlayerEvents.AFTER_RESPAWN.doFire().onPlayerRespawn(newPlayer));
    }

    public static void onArmorHurt(DamageSource damageSource, EquipmentSlot[] slots, float damage, LivingEntity entity) {
        EnumMap<EquipmentSlot, ArmorEntry> entries = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : slots) {
            ItemStack armorItemStack = entity.getItemBySlot(slot);
            if (armorItemStack.isEmpty()) {
                continue;
            }

            float finalDamage = armorItemStack.canBeHurtBy(damageSource) ? damage : 0.0F;
            entries.put(slot, new ArmorEntry(armorItemStack, finalDamage));
        }

        ArmorHurtEvent.Context context = new ArmorHurtEvent.Context() {
            @Override
            public ItemStack getArmorItemStack(EquipmentSlot slot) {
                ArmorEntry entry = entries.get(slot);
                return entry != null ? entry.armorItemStack : entity.getItemBySlot(slot);
            }

            @Override
            public float getOriginalDamage(EquipmentSlot slot) {
                ArmorEntry entry = entries.get(slot);
                return entry != null ? entry.originalDamage : 0.0F;
            }

            @Override
            public float getDamage(EquipmentSlot slot) {
                ArmorEntry entry = entries.get(slot);
                return entry != null ? entry.newDamage : 0.0F;
            }

            @Override
            public void setDamage(EquipmentSlot slot, float damage) {
                ArmorEntry entry = entries.get(slot);
                if (entry != null) {
                    entry.newDamage = damage;
                }
            }
        };

        if (ArmorHurtEvent.EVENT.doFire().onAmorHurt(entity, damageSource, context).isFalse()) {
            return;
        }

        entries.forEach((slot, entry) -> {
            if (entry.newDamage > 0.0F) {
                entry.armorItemStack.hurtAndBreak((int) entry.newDamage, entity, slot);
            }
        });
    }

    private CommonEventHooks() {}

    public static class ArmorEntry {
        public ItemStack armorItemStack;
        public final float originalDamage;
        public float newDamage;

        public ArmorEntry(ItemStack armorStack, float damageIn) {
            this.armorItemStack = armorStack;
            this.originalDamage = damageIn;
            this.newDamage = damageIn;
        }
    }
}
