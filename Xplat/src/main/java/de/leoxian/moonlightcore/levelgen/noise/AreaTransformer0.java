package de.leoxian.moonlightcore.levelgen.noise;

public interface AreaTransformer0 {

    default AreaFactory run(AreaContext context) {
        return () -> context.createArea((x, z) -> {
            context.initRandom(x, z);
            return this.apply(context, x, z);
        });
    }

    int apply(AreaContext context, int x, int z);

}
