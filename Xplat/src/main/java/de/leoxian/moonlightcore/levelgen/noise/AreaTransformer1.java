package de.leoxian.moonlightcore.levelgen.noise;

public interface AreaTransformer1 {

    default AreaFactory run(AreaContext context, AreaFactory factory) {
        return () -> {
            Area area = factory.make();

            return context.createArea((x, z) -> {
                context.initRandom(x, z);
                return this.apply(context, area, x, z);
            }, area);
        };
    }

    int apply(AreaContext context, Area area, int x, int z);

}
