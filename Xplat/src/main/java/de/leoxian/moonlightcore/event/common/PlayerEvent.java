package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public interface PlayerEvent {
     /**
      * @see JoinServer#onPlayerJoin(ServerPlayer, MinecraftServer) 
      */
     Event<JoinServer> JOIN_SERVER = EventFactory.create(JoinServer.class);
     /**
      * @see QuitServer#onPlayerQuit(ServerPlayer, MinecraftServer) 
      */
     Event<QuitServer> QUIT_SERVER = EventFactory.create(QuitServer.class);
     /**
      * @see AfterRespawn#onPlayerRespawn(ServerPlayer, ServerPlayer, boolean)
      */
     Event<AfterRespawn> AFTER_RESPAWN = EventFactory.create(AfterRespawn.class);
     /**
      * @see Clone#onPlayerClone(ServerPlayer, ServerPlayer) 
      */
     Event<Clone> CLONE = EventFactory.create(Clone.class);
     /**
      * @see AdvancementAward#onAdvancementAward(ServerPlayer, Advancement) 
      */
     Event<AdvancementAward> ADVANCEMENT_AWARD = EventFactory.create(AdvancementAward.class);
     /**
      * @see ItemPickupValidation#canPickupItem(Player, ItemEntity, ItemStack)
      */
     Event<ItemPickupValidation> ITEM_PICKUP_VALIDATION = EventFactory.createWithResult(ItemPickupValidation.class);
     /**
      * @see PickupItem#onItemPickup(Player, ItemEntity, ItemStack) 
      */
     Event<PickupItem> PICKUP_ITEM = EventFactory.create(PickupItem.class);
     /**
      * @see ChangeDimension#onChangeDimension(ServerPlayer, ResourceKey, ResourceKey) 
      */
     Event<ChangeDimension> CHANGE_DIMENSION = EventFactory.create(ChangeDimension.class);
     /**
      * @see DropItem#onDropItem(Player, ItemEntity) 
      */
     Event<DropItem> DROP_ITEM = EventFactory.createWithResult(DropItem.class);
     /**
      * @see OpenMenu#onOpenMenu(Player, AbstractContainerMenu) 
      */
     Event<OpenMenu> OPEN_MENU = EventFactory.create(OpenMenu.class);
     /**
      * @see CloseMenu#onCloseMenu(Player, AbstractContainerMenu)
      */
     Event<CloseMenu> CLOSE_MENU = EventFactory.create(CloseMenu.class);
     /**
      * @see AttackEntity#onAttackEntity(Level, Player, Entity, InteractionHand, EntityHitResult)
      */
     Event<AttackEntity> ATTACK_ENTITY = EventFactory.createWithResult(AttackEntity.class);

     interface JoinServer {
          /**
           * Invoked after a player joined a server
           * @param player The player who joined
           * @param server The server that the player joined
           */
          void onPlayerJoin(ServerPlayer player, MinecraftServer server);
     }

     interface QuitServer {
          /**
           * Invoked after a player leaves a server
           * @param player The player who left
           * @param server The server that the player left
           */
          void onPlayerQuit(ServerPlayer player, MinecraftServer server);
     }

     interface AfterRespawn {
          /**
           * Invoked when a player is respawned. To manipulate the player use {@link Clone}
           * @param oldPlayer The old player
           * @param newPlayer The respawned player
           * @param alive Whether the old player is still alive
           */
          void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive);
     }

     interface Clone {
          /**
           * Invoked when a player respawns. May be used to manipulate the new player
           * @param oldPlayer The old player
           * @param newPayer The new player
           */
          void onPlayerClone(ServerPlayer oldPlayer, ServerPlayer newPayer);
     }

     interface AdvancementAward {
          /**
           * Invoked when a player gets an advancement
           * @param player The player who got the advancement
           * @param advancement The advancement the player got
           */
          void onAdvancementAward(ServerPlayer player, Advancement advancement);
     }

     interface ItemPickupValidation {
          /**
           * Invoked when a player tries to pickup an {@link ItemEntity}
           * @param player The player picking up
           * @param entity The {@link ItemEntity} that the player tires to pick up
           * @param stack The content of the {@link ItemEntity}
           * @return A {@link Event.Result} determining the outcome of the event, the exexcution of the pickup may be cancelled by the result
           */
          Event.Result canPickupItem(Player player, ItemEntity entity, ItemStack stack);
     }

     interface PickupItem {
          /**
           * Invoked when a player has picked up an {@link ItemEntity}
           * @param player The player
           * @param entity The {@link ItemEntity} that the player picked up
           * @param stack The content of the {@link ItemEntity}
           */
          void onItemPickup(Player player, ItemEntity entity, ItemStack stack);
     }

     interface ChangeDimension {
          /**
           * Invoked when a player changes their dimension
           * @param player The teleporting player
           * @param oldLevel The level the player comes from
           * @param newLevel The level the player teleports to
           */
          void onChangeDimension(ServerPlayer player, ResourceKey<Level> oldLevel, ResourceKey<Level> newLevel);
     }

     interface DropItem {
          /**
           * Invoked when a player drops an item
           * @param player The player dropping something
           * @param entity The entity that has spawned when the player dropped a ItemStack
           * @return A {@link Event.Result} determining the outcome of the event, the execution of the drop may be cancelled by the result
           */
          Event.Result onDropItem(Player player, ItemEntity entity);
     }

     interface OpenMenu {
          /**
           * Invoked when a player opens a menu
           * @param player The player opening the menu
           * @param menu The menu that is opened
           */
          void onOpenMenu(Player player, AbstractContainerMenu menu);
     }

     interface CloseMenu {
          /**
           * Invoked when a player closes a menu
           * @param player The player closing the menu
           * @param menu The menu that is closed
           */
          void onCloseMenu(Player player, AbstractContainerMenu menu);
     }

     interface AttackEntity {
          /**
           * Invoked when a player is about to attack an entity using left-click
           * @param level The level the player is in
           * @param player The player attacking the entity
           * @param target The entity about to be attacked
           * @param hand The hand the player is using
           * @param result The entity hit result, may be {@code null}
           * @return A {@link Event.Result} determining the outcome of the event, the attack may be cancelled by the result
           */
          Event.Result onAttackEntity(Level level, Player player, Entity target, InteractionHand hand, @Nullable EntityHitResult result);
     }
}
