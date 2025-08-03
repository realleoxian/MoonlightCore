# Moonlight Core 4.0.0

## Configuration System

The configuration system had a full rework and now its better than before, being able to even sync your configurations now!
Everything about the configuration now is on `de.leowgc.moonlightcore.api.config.ModConfigSpec.`

The configurations now are side-specified, this means you can have one configuration for client, other for common and one for server, the server one being synced automatically. Here an example of how use the new configuration system:

```java
public class ExampleModConfig {
    public static final ExampleModConfig CONFIG = ModConfigSpec.build(ExampleModConfig::new);
    
    // This config spec that have all the data and values
    private final ModConfigSpec spec;
    
    private final ConfigCategory exampleCategory;
    
    public ExampleModConfig(ModConfigSpec.Builder builder) {
        // Set a temporal description until exampleCategory is pushed
        builder.description("This is a example category - Line 1",
                            "This is a example category - Line 2");
        
        // The description specified above will be automatically clear after building into this category
        builder.pushCategory("exampleCategory", this::exampleCategory);
    
        this.spec = builder.build(ExampleMod.MOD_ID, ModConfigSpec.Side.COMMON);
        this.exampleCategory = this.spec.getCategoryOrThrow("exampleCategory");
    }
    
    private void exampleCategory(ModConfigSpec.ConfigCategory.Builder categoryBuilder) {
        categoryBuilder.description("Same as categories you can put descriptions like this.",
                                    "This is an example integer");
        
        // Define no range int
        categoryBuilder.defineInt("exampleInt", () -> 10);
        
        // Define ranged int
        // This defined value will not have a description
        categoryBuilder.defineInt("exampleRangedInt", 0, 20, () -> 10);
    }
    
    public int exampleInt() {
        return this.exampleCategory.getValue("exampleInt");
    }
    
    public int exampleRangedInt() {
        return this.examplCategory.getValue("exampleRanedInt");
    }
}
```

When creating the instance of the file it automatically its being created in the configurations directory of the mod loader in a `<modId>-<side>.ezc` file.
You can also implement your own custom type configurations by implementing a `ValueKeySerializer` that writes/reads the value from the file and specify it with the 'define' method.

Server side configurations will be synced to players when they join to the server.

## Networking

The networking (like all the mod things) was reworked too! Now you have to implement `MoonlightCustomPacket.ServerBoundCustomPacket` if your packet is C2S or `MoonlightCustomPacket.ClientBoundCustomPacket` if your packet is S2C.

To register these packets you need to implement your `PacketDispatcher`.  Example:

```java
public class ExamplePacketDispatcher extends PacketDispatcher {
    
    public ExamplePacketDispatcher() {
        super(new ResourceLocation(ExampleMod.MOD_ID, "networking"));
    }
    
    // On the bootstrap method you will register your packets
    public void bootstrap() {
        // This will register your packet into S2C packets
        this.registerClientBound(ExamplePacket.ID, ExamplePacket.class, ExamplePacket.CODEC, ExamplePacket::handle);   
    
        // And the same method parameters for server bound packets
        this.registerServerBound(ExampleServerPackt.ID, ExampleServerPackt.class, ExampleServerPackt.CODEC, ExampleServerPackt::handle);
    }
}
```

The Packet dispatcher class provides a method to send a packet to the client of a player or sending a packet to the server.
Also, the Network contexts for client and server packets were removed, now you have to pass directly to a method the context.

## World Generation

The world generation stays likely the sames as the 3.0.0 with some minimal changes.

1. **BiomeProvider** is no longer a abstract class and now is a interface that needs to be registered on BiomeProviderRegistry with a weight.
2. **ParameterPointsListBuilder** now haves a included method to bind all the points to a biome automatically

Out of these 2 changes there is nothing else than a re-organization of the classes.

## Events

The events now don't need to extend EventType (that was removed), now directly you can create the event referencing it on the type parameters.

Now the events support priorities, these priorities being:
1. HIGHEST
2. HIGH
3. NORMAL
4. LOW
5. LOWEST

With these priorities change the `event.custom` package was remove to re-organize the events. There are all the events on this update:

- Client events:
  - ClientLifecycleEvent:
    - **STARTING**: Fired when the client is starting to run
    - **STARTED**: Fired just before the client enters the running loop
    - **STOPPING**: When the client is stopping
    
  - ClientTickEvent: Fired on every client tick which can be on the start and the end of the tick
  - FogRenderEvent:
    - **FOG_RENDERING**: Fired when rendering the fog
    - **FOG_COLOR_COMPUTE**: Fired when selecting the color of the fog
  - GameRenderingEvents:
    - **BLOCK_COLOR_REGISTRATION**: Fired when registering the blocks/fluids colors
    - **BLOCK_RENDERER_REGISTRATION**: Fired when registering render types for blocks
    - **ENTITY_RENDERER_REGISTRATION**: Fired when registering entity renderers
    - **BLOCK_ENTITY_RENDERER_REGISTRATION**: Fired when registering block entity renderers
    - **MODEL_LAYER_REGISTRATION**: Fired when registering entity model layers
    - **PARTICLE_FACTORY_REGISTRATION**: Fired when registering particle sprites
  - HudRenderEvent: Fired when rendering the HUD
  - InputEvent:
    - **KEY_INPUT**: Fired when there is an input from the keyboard
    - **MOUSE_INPUT**: Fired when there is an input from the mouse


- Common events:
  - RegistryEvents:
    - **NEW_REGISTRY**: Fired when registering new registries to Minecraft
    - **REGISTER**: Fired when registering new things to Minecraft
  - CommandRegistrationEvent: Fired when creating new Commands and registering them on Minecraft


- Server events:
  - ServerLifecycleEvent:
    - **STARTING**: Fired when the server is starting
    - **STARTED**: Fired when the server has been started
    - **STOPPING**: Fired when the server is stopping
    - **STOPPED**: Fired when the server already has been stopped
  - ServerPlayerEvents:
    - **JOIN_SERVER**: Fired when a player joins to a server
    - **LEAVE_SERVER** Fired when a player leaves a server
    - **AFTER_RESPAWN**: Fired after a player respawns
    - **COPY_FROM**: Fired when a player data is copied from other player
  - ServerTickEvent: Fired at the start and the end of an tick on the server

## Miscellaneous

The data attachments have been removed since in a few versions there will be the Mojang's DataComponents so there is no point on having them.