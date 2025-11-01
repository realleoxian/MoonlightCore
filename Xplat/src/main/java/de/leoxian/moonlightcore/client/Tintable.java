package de.leoxian.moonlightcore.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface Tintable {

    default int getColor(int index) {
        return -1;
    }

    default int getColor(int index, ItemStack stack) {
        return this.getColor(index);
    }


    enum TintedItem implements ItemColor {
        INSTANCE
        ;

        @Override
        public int getColor(ItemStack itemStack, int i) {
            return ((Tintable) itemStack.getItem()).getColor(i, itemStack);
        }
    }

    enum TintedBlockItemImpl implements ItemColor {
        INSTANCE
        ;

        @Override
        public int getColor(ItemStack itemStack, int i) {
            return ((Tintable) Block.byItem(itemStack.getItem())).getColor(i, itemStack);
        }
    }

    enum TintedBlockImpl implements BlockColor {
        INSTANCE
        ;

        @Override
        public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
            return ((Tintable) blockState.getBlock()).getColor(i);
        }
    }
}
