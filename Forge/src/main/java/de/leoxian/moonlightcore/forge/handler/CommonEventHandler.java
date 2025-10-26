package de.leoxian.moonlightcore.forge.handler;

import de.leoxian.moonlightcore.event.common.ChunkEvent;

import de.leoxian.moonlightcore.event.common.EntityEvent;
import de.leoxian.moonlightcore.event.common.ServerLevelLifecycleEvent;
import de.leoxian.moonlightcore.event.common.ServerLifecycleEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler {

     // -----[TICK EVENTS]-----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
          de.leoxian.moonlightcore.event.common.TickEvent.Phase phase = event.phase == TickEvent.Phase.START ? de.leoxian.moonlightcore.event.common.TickEvent.Phase.START : de.leoxian.moonlightcore.event.common.TickEvent.Phase.END;

          de.leoxian.moonlightcore.event.common.TickEvent.SERVER_TICK.invoker().onServerTick(phase, event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onLevelTickEvent(TickEvent.LevelTickEvent event) {
          de.leoxian.moonlightcore.event.common.TickEvent.Phase phase = event.phase == TickEvent.Phase.START ? de.leoxian.moonlightcore.event.common.TickEvent.Phase.START : de.leoxian.moonlightcore.event.common.TickEvent.Phase.END;
          boolean isClientSide = event.side == LogicalSide.CLIENT;

          de.leoxian.moonlightcore.event.common.TickEvent.LEVEL_TICK.invoker().onLevelTick(phase, event.level, isClientSide);
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
          de.leoxian.moonlightcore.event.common.TickEvent.Phase phase = event.phase == TickEvent.Phase.START ? de.leoxian.moonlightcore.event.common.TickEvent.Phase.START : de.leoxian.moonlightcore.event.common.TickEvent.Phase.END;
          de.leoxian.moonlightcore.event.common.TickEvent.PLAYER_TICK.invoker().onPlayerTick(phase, event.player);
     }

     // -----[PLAYER EVENTS]-----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onAttackEntity(AttackEntityEvent event) {
          if(de.leoxian.moonlightcore.event.common.PlayerEvent.ATTACK_ENTITY.invoker().onAttackEntity(event.getEntity().level(), event.getEntity(), event.getTarget(), event.getEntity().getUsedItemHand(), null).isFalse()) {
               event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
          if(event.getEntity() instanceof ServerPlayer player) {
               de.leoxian.moonlightcore.event.common.PlayerEvent.CHANGE_DIMENSION.invoker().onChangeDimension(player, event.getFrom(), event.getTo());
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
          de.leoxian.moonlightcore.event.common.PlayerEvent.OPEN_MENU.invoker().onOpenMenu(event.getEntity(), event.getContainer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPlayerCloseContainer(PlayerContainerEvent.Close event) {
          de.leoxian.moonlightcore.event.common.PlayerEvent.CLOSE_MENU.invoker().onCloseMenu(event.getEntity(), event.getContainer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onItemToss(ItemTossEvent event) {
          de.leoxian.moonlightcore.event.common.PlayerEvent.DROP_ITEM.invoker().onDropItem(event.getPlayer(), event.getEntity());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
          de.leoxian.moonlightcore.event.common.PlayerEvent.PICKUP_ITEM.invoker().onItemPickup(event.getEntity(), event.getOriginalEntity(), event.getStack());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onEntityItemPickup(EntityItemPickupEvent event) {
          var result = de.leoxian.moonlightcore.event.common.PlayerEvent.ITEM_PICKUP_VALIDATION.invoker().canPickupItem(event.getEntity(), event.getItem(), event.getItem().getItem());

          if(result.isFalse()) {
              event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onPlayerClone(PlayerEvent.Clone event) {
          if(event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
               de.leoxian.moonlightcore.event.common.PlayerEvent.CLONE.invoker().onPlayerClone(oldPlayer, newPlayer);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onAdvancementAward(AdvancementEvent.AdvancementEarnEvent event) {
          if(event.getEntity() instanceof ServerPlayer player) {
               de.leoxian.moonlightcore.event.common.PlayerEvent.ADVANCEMENT_AWARD.invoker().onAdvancementAward(player, event.getAdvancement());
          }
     }

     // ----[ENTITY EVENTS]----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onEntityJoinEvent(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
          if(EntityEvent.ADDITION.invoker().onEntityAddition(event.getLevel(), event.getEntity()).isFalse()) {
               event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onLivingHurt(LivingHurtEvent event) {
          if(EntityEvent.LIVING_HURT.invoker().onEntityHurt(event.getEntity(), event.getSource(), event.getAmount()).isFalse()) {
               event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onLivingDeath(LivingDeathEvent event) {
          if(EntityEvent.LIVING_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource()).isFalse()) {
               event.setCanceled(true);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onEntityEnterSection(net.minecraftforge.event.entity.EntityEvent.EnteringSection event) {
          EntityEvent.ENTER_SECTION.invoker().onEnterSection(event.getEntity(), event.getNewPos(), event.getOldPos());
     }

     // ----[LIFECYCLE EVENTS]-----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerAboutToStart(ServerAboutToStartEvent event) {
          ServerLifecycleEvent.ABOUT_TO_START.invoker().onLifecycleState(event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerStarting(ServerStartingEvent event) {
          ServerLifecycleEvent.STARTING.invoker().onLifecycleState(event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerStarted(ServerStartedEvent event) {
          ServerLifecycleEvent.STARTED.invoker().onLifecycleState(event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerStopping(ServerStoppingEvent event) {
          ServerLifecycleEvent.STOPPING.invoker().onLifecycleState(event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerStopped(ServerStoppedEvent event) {
          ServerLifecycleEvent.STOPPED.invoker().onLifecycleState(event.getServer());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerLevelLoad(LevelEvent.Load event) {
          if(event.getLevel() instanceof ServerLevel level) {
               ServerLevelLifecycleEvent.LOAD.invoker().onLifecycleState(level);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerLevelUnload(LevelEvent.Unload event) {
          if(event.getLevel() instanceof ServerLevel level) {
               ServerLevelLifecycleEvent.UNLOAD.invoker().onLifecycleState(level);
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onServerLevelSave(LevelEvent.Save event) {
          if(event.getLevel() instanceof ServerLevel level) {
               ServerLevelLifecycleEvent.SAVE.invoker().onLifecycleState(level);
          }
     }

     // ----[BLOCK EVENTS]-----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onBlockBreak(BlockEvent.BreakEvent event) {
          if(event.getPlayer() instanceof ServerPlayer player && event.getLevel() instanceof Level level) {

               if(de.leoxian.moonlightcore.event.common.BlockEvent.BREAK.invoker().onBlockBreak(level, player, event.getPos(), event.getState()).isFalse()) {
                    event.setCanceled(true);
               }
          }
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
          if(event.getLevel() instanceof Level level) {

               if(de.leoxian.moonlightcore.event.common.BlockEvent.PLACE.invoker().onBlockPlace(level, event.getEntity(), event.getState(), event.getPos()).isFalse()) {
                    event.setCanceled(true);
               }
          }
     }

     // ----[CHUNK EVENTS]-----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onChunkDataLoadEvent(ChunkDataEvent.Load forgeEvent) {
          LevelAccessor level = forgeEvent.getLevel();
          de.leoxian.moonlightcore.event.common.ChunkEvent.LOAD.invoker().onChunkDataLoad(forgeEvent.getChunk(), level instanceof ServerLevel ? (ServerLevel) level : null, forgeEvent.getData());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onChunkDataSaveEvent(ChunkDataEvent.Save forgeEvent) {
          if(forgeEvent.getLevel() instanceof ServerLevel level) {
               ChunkEvent.SAVE.invoker().onChunkDataSave(forgeEvent.getChunk(), level, forgeEvent.getData());
          }
     }

     // ----[uh... MISC?]----

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onCommandRegistration(RegisterCommandsEvent event) {
          de.leoxian.moonlightcore.event.common.RegisterCommandsEvent.EVENT.invoker().onCommandRegistration(event.getDispatcher(), event.getCommandSelection(), event.getBuildContext());
     }

     @SubscribeEvent(priority = EventPriority.HIGH)
     public static void onVanillaGameEvent(VanillaGameEvent forgeEvent) {
          Level level = forgeEvent.getLevel();
          GameEvent event = forgeEvent.getVanillaEvent();
          Vec3 position = forgeEvent.getEventPosition();
          GameEvent.Context context = forgeEvent.getContext();

          if(de.leoxian.moonlightcore.event.common.VanillaGameEvent.EVENT.invoker().onGameEvent(level, event, position, context).isFalse()) {
               forgeEvent.setCanceled(true);
          }
     }
}
