package de.realleoxian.moonlightcore.api.client.render.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface Tintable {
    default int getColor(int idx) {
        return -1;
    }

    default int getColor(int idx, ItemStack stack) {
        return getColor(idx);
    }

    final class TintedItem implements ItemColor {
        @Override
        public int getColor(ItemStack itemStack, int i) {
            return ((Tintable) itemStack.getItem()).getColor(i, itemStack);
        }
    }

    final class TinterBlockItemImpl implements ItemColor {
        @Override
        public int getColor(ItemStack itemStack, int i) {
            return ((Tintable) Block.byItem(itemStack.getItem())).getColor(i, itemStack);
        }
    }

    final class TintedBlockImpl implements BlockColor {
        @Override
        public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
            return ((Tintable) blockState.getBlock()).getColor(i);
        }
    }
}
