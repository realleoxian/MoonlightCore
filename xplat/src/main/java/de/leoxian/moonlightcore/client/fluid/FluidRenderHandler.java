package de.leoxian.moonlightcore.client.fluid;

import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import jdk.jfr.Experimental;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Experimental
public interface FluidRenderHandler {
    FluidRenderHandler DEFAULT = new FluidRenderHandler() {};

    /// Appends additional tooltips to the passed list if additional information is contained on the fluid resource
    /// @param resource The fluid resource where to get the fluid and its componnts data
    /// @param tooltip The tooltips
    /// @param tooltipFlag The tooltip flag
    default void appendTooltip(FluidResource resource, List<Component> tooltip, TooltipFlag tooltipFlag) {

    }

    /// Return the tint of the block
    /// @param resource The fluid resource from where to get the fluid and its components data
    /// @param level The level instance where to get the tint, may be `null` if no level instance is present or isn't needed
    /// @param blockPos The target block position, may be `null` if its unknown or isn't needed
    /// @return The tint of the liquid
    default int getColor(FluidResource resource, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos) {
        FluidState fluidState = resource.fluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        if (fluidModel.tintSource() == null) {
            return -1;
        }

        if (level != null && blockPos != null) {
            return fluidModel.tintSource().colorInWorld(Blocks.AIR.defaultBlockState(), level, blockPos);
        } else {
            return fluidModel.tintSource().color(Blocks.AIR.defaultBlockState());
        }
    }
}
