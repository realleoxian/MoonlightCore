package de.leoxian.moonlightcore.fabric.common.fluid;

import de.leoxian.moonlightcore.common.fluid.FluidAttributesHandler;
import de.leoxian.moonlightcore.common.fluid.FluidBehavior;
import de.leoxian.moonlightcore.common.fluid.FluidProperties;
import de.leoxian.moonlightcore.common.fluid.FluidRegistrar;
import net.fabricmc.fabric.api.registry.fluid.EntityFluidInteractionRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public record FabricFluidRegistrar(String namespace) implements FluidRegistrar {
	@Override
	public void register(String id, TagKey<Fluid> fluidType, FluidProperties properties, FluidAttributesHandler propertiesHandler, FluidBehavior entityInteraction) {
		Identifier identifier = Identifier.fromNamespaceAndPath(namespace, id);

		Supplier<Fluid> sourceGetter = () -> BuiltInRegistries.FLUID.getValue(identifier);
		Supplier<Fluid> flowingGetter = () -> BuiltInRegistries.FLUID.getValue(identifier.withPath(s -> s + "_flowing"));
		Supplier<Item> bucketGetter = () -> BuiltInRegistries.ITEM.getValue(identifier.withPath(s -> s + "_bucket"));
		Supplier<Block> blockGetter = () -> BuiltInRegistries.BLOCK.getValue(identifier);

		Fluid sourceFluid = Registry.register(BuiltInRegistries.FLUID, identifier,
				new FabricBaseFluid.Source(flowingGetter, sourceGetter, bucketGetter, blockGetter,
						properties.slopeFindDistance(), properties.levelDecreasePerBlock(), properties.explosionResistance(), properties.tickRate()));

		Fluid flowingFluid = Registry.register(BuiltInRegistries.FLUID, identifier.withPath(s -> s + "_flowing"),
				new FabricBaseFluid.Flowing(flowingGetter, sourceGetter, bucketGetter, blockGetter,
						properties.slopeFindDistance(), properties.levelDecreasePerBlock(), properties.explosionResistance(), properties.tickRate()));

		Registry.register(BuiltInRegistries.ITEM, identifier.withPath(s -> s + "_bucket"),
				new BucketItem(sourceFluid, new Item.Properties()
						.craftRemainder(Items.BUCKET)
						.stacksTo(1)
						.setId(ResourceKey.create(Registries.ITEM, identifier.withPath(s -> s + "_bucket")))));

		Registry.register(BuiltInRegistries.BLOCK, identifier,
				new LiquidBlock((FlowingFluid) flowingFluid, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
						.setId(ResourceKey.create(Registries.BLOCK, identifier))));

		EntityFluidInteractionRegistry.register(fluidType, new FabricFluidBehaviorWrapper(propertiesHandler, entityInteraction));
		FluidVariantAttributes.register(sourceFluid, new FluidAttributeHandlerWrapper(propertiesHandler));
		FluidVariantAttributes.register(flowingFluid, new FluidAttributeHandlerWrapper(propertiesHandler));
	}
}
