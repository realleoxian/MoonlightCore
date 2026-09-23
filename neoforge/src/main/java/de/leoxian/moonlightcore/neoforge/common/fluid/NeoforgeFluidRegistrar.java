package de.leoxian.moonlightcore.neoforge.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidBehavior;
import de.leoxian.moonlightcore.common.fluid.FluidAttributesHandler;
import de.leoxian.moonlightcore.common.fluid.FluidProperties;
import de.leoxian.moonlightcore.common.fluid.FluidRegistrar;
import de.leoxian.moonlightcore.neoforge.common.ModDeferredRegisters;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public record NeoforgeFluidRegistrar(String namespace) implements FluidRegistrar {
	@Override
	public void register(String id, TagKey<Fluid> fluidType, FluidProperties properties, FluidAttributesHandler propertiesHandler, FluidBehavior entityInteraction) {
		Supplier<FluidType> fluidTypeValue = ModDeferredRegisters.get(NeoForgeRegistries.FLUID_TYPES, namespace())
				.register(id, () -> new NeoforgeFluidTypeImpl(fluidType, propertiesHandler, entityInteraction));

		Identifier identifier = Identifier.fromNamespaceAndPath(namespace, id);
		Supplier<? extends Fluid> sourceGetter = () -> BuiltInRegistries.FLUID.getValue(identifier);
		Supplier<? extends FlowingFluid> flowingGetter = () -> (FlowingFluid) BuiltInRegistries.FLUID.getValue(identifier.withPath(s -> s + "_flowing"));
		Supplier<? extends Item> bucketGetter = () -> BuiltInRegistries.ITEM.getValue(identifier.withPath(s -> s + "_bucket"));
		Supplier<? extends LiquidBlock> blockGetter = () -> (LiquidBlock) BuiltInRegistries.BLOCK.getValue(identifier);
		BaseFlowingFluid.Properties neoProperties = new BaseFlowingFluid.Properties(fluidTypeValue, sourceGetter, flowingGetter)
				.block(blockGetter)
				.bucket(bucketGetter)
				.slopeFindDistance(properties.slopeFindDistance())
				.levelDecreasePerBlock(properties.levelDecreasePerBlock())
				.tickRate(properties.tickRate())
				.explosionResistance(properties.explosionResistance());

		ModDeferredRegisters.get(Registries.FLUID, namespace).register(id, () -> new BaseFlowingFluid.Source(neoProperties));
		ModDeferredRegisters.get(Registries.FLUID, namespace).register(id + "_flowing", () -> new BaseFlowingFluid.Flowing(neoProperties));
		ModDeferredRegisters.get(Registries.ITEM, namespace).register(id + "_bucket", k -> new BucketItem(sourceGetter.get(), new Item.Properties()
				.craftRemainder(Items.BUCKET)
				.stacksTo(1)
				.setId(ResourceKey.create(Registries.ITEM, k))));
		ModDeferredRegisters.get(Registries.BLOCK, namespace).register(id, k -> new LiquidBlock(flowingGetter.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
				.setId(ResourceKey.create(Registries.BLOCK, k))));
	}
}
