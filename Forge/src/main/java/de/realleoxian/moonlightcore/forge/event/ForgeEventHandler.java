package de.realleoxian.moonlightcore.forge.event;

import de.realleoxian.moonlightcore.api.event.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    @SubscribeEvent
    public static void onServerLevelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            switch (event.phase) {
                case START -> ServerLevelTickEvents.START.invoker().onServerLevelTickStart((ServerLevel) event.level);
                case END -> ServerLevelTickEvents.END.invoker().onServerLevelTickEnd((ServerLevel) event.level);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getChunk() instanceof LevelChunk levelChunk && event.getLevel() instanceof ServerLevel level) {
            levelChunk.getBlockEntities().values().forEach(be -> ServerBlockEntityEvents.LOAD.invoker().onBlockEntityLoad(level, be));
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getChunk() instanceof LevelChunk levelChunk && event.getLevel() instanceof ServerLevel level) {
            levelChunk.getBlockEntities().values().forEach(be -> ServerBlockEntityEvents.LOAD.invoker().onBlockEntityLoad(level, be));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityItemPickup(EntityItemPickupEvent event) {
        if (ItemPickupEvent.EVENT.invoker().onItemPickup(event.getEntity(), event.getItem()).isTrue()) {
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemTossEvent(ItemTossEvent event) {
        Player player = event.getPlayer();
        ItemEntity itemEntity = event.getEntity();

        if (ItemTossEvents.PRE_ITEM_TOSS.invoker().onPreItemToss(player, itemEntity).isFalse())
            event.setCanceled(true);
        ItemTossEvents.POST_ITEM_TOSS.invoker().onItemToss(player, itemEntity);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityHurt(LivingHurtEvent event) {
        if (LivingEntityEvents.HURT.invoker().onEntityHurt(event.getEntity(), event.getSource(), event.getAmount()).isFalse())
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (LivingEntityEvents.DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource()).isFalse())
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityAttackEvent(LivingAttackEvent event) {
        if (LivingEntityEvents.ATTACK.invoker().onEntityAttack(event.getSource(), event.getAmount()).isFalse())
            event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        switch (event.phase) {
            case START -> PlayerTickEvents.TICK_START.invoker().onStartPlayerTick(event.player);
            case END -> PlayerTickEvents.TICK_END.invoker().onEndPlayerTick(event.player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer nPlayer && event.getOriginal() instanceof ServerPlayer oPlayer) {
            ServerPlayerEvents.CLONE.invoker().onPlayerClone(oPlayer, nPlayer, event.isWasDeath());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ServerPlayerEvents.CHANGE_DIMENSION.invoker().onPlayerChangeDimension(serverPlayer, event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLifecycleEvents.STARTING.invoker().onServerStarting(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().onServerStarted(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().onServerStopping(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerStopped(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().onServerStopped(event.getServer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START -> ServerTickEvents.START.invoker().onServerTickStart(event.getServer());
            case END -> ServerTickEvents.END.invoker().onServerTickEnd(event.getServer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onVanillaGameEvent(net.minecraftforge.event.VanillaGameEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (VanillaGameEvent.EVENT.invoker().onVanillaGameEvent(serverLevel, event.getVanillaEvent(), event.getEventPosition(), event.getContext()).isFalse()) {
                event.setCanceled(true);
            }
        }
    }
}
