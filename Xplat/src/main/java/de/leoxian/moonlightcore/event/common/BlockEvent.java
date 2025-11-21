package de.leoxian.moonlightcore.event.common;

import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockEvent {
     /**
      * @see Place#onBlockPlace(Level, Entity, BlockState, BlockPos) 
      */
     Event<Place> PLACE = EventFactory.createWithResult(BlockEvent.Place.class);
     /**
      * @see Break#onBlockBreak(Level, ServerPlayer, BlockPos, BlockState) 
      */
     Event<Break> BREAK = EventFactory.createWithResult(BlockEvent.Break.class);
     
     interface Place {
          /**
           * Invoked when a block is placed
           * @param level The level the block is in
           * @param entity The entity who is placing the block, me be {@code null}, e.g: When  a dispenser places something
           * @param blockState The future state of the block
           * @param blockPos The position of the block
           */
          Event.Result onBlockPlace(Level level, @Nullable Entity entity, BlockState blockState, BlockPos blockPos);
     }

     interface Break {
          /**
           * Invoked when a block is destroyed by a player
           * @param level The level the block is in
           * @param player The place who is breaking the block
           * @param blockPos The position of the block
           * @param blockState The current state of the block
           */
          Event.Result onBlockBreak(Level level, ServerPlayer player, BlockPos blockPos, BlockState blockState);
     }
}
