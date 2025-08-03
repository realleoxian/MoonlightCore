package de.leowgc.moonlightcore.world.biome.layer;

public interface AreaTransformer1 {
    default AreaFactory run(AreaContext context, AreaFactory factory) {
        return () -> {
            Area area = factory.make();

            return context.createResult((x, z) -> {
                context.initializeRandom(x, z);
                return this.apply(context, area, x, z);
            }, area);
        };
    }

    int apply(AreaContext context, Area area, int x, int z);
}
