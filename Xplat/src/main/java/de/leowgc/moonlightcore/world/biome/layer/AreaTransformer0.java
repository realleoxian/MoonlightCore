package de.leowgc.moonlightcore.world.biome.layer;

public interface AreaTransformer0 {
    default AreaFactory run(AreaContext context) {
        return () -> context.createResult((x, z) -> {
           context.initializeRandom(x, z);
           return this.apply(context, x, z);
        });
    }

    int apply(AreaContext context, int x, int z);
}
