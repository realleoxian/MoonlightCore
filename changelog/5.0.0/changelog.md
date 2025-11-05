# MoonlightCore | 5.0.0
### - 2025-03-11 | Leoxian / Wonejo / LeoWGC

HII!!! I came back with a new update to MoonlightCore and with a lot of changes on the mod!!

- Note: If you find any error, bug, a crash that happens because of the mod or an API that doesn't work has it should. Report it on the [Github repository](https://github.com/realleoxian/MoonlightCore/tree/dev)

## Added

### 1. Attachment System:
The data attachment system allows mods to attach/store data on block entities, chunks, entities, and levels.
Here an example of how can this system be used:

```java
// On your attachment registrar
public static RegistryEntry<AttachmentType<?>, AttachmentType<Float>> mana;

public static void setupAttachments(Consumer<RegistryEntry<AttachmentType<?>, AttachmentType<?>>> output) {
    mana = AttachmentTypeBuilder.of(new ResourceLocation("exampleMod", "mana")).persistentCodec(Codec.FLOAT).syncWith(ByteBufCodecs.FLOAT, AttachmentSyncPredicate.all()).build(output);
}

// You can also register the attachments to be synced
public static void setupSyncedAttachments(Consumer<RegistryEntry<AttachmentType<?>, AttachmentType<?>>> output) {
    output.accept(mana);
}
```
```java
// NOW, on an IDK, block entity
private final AttachmentMap attachments = new AttachmentMap((AttachmentHolder) (Object) this);

public void addMana(float amount) {
    this.attachments.setAttachedData(ExampleAttachments.mana, this.getMana() + amount);
}

public void removeMana(int amount) {
    this.attachments.setAttachedData(ExampleAttachments.mana, this.getMana() - amount);
}

public float getMana() {
    return this.attachments.getAttachedData(ExampleAttachments.mana);
}
```

### 2. API Lookup system

The API Lookup system allows API instances to be associated with game objects without specifying how the association is implemented. 
This is useful when the same API could be implemented more than once, or implemented in different ways.

This API is based on he fabric-lookup-api and may can be the equivalent of the capabilitie API of Neo/Forge

## Changes

### 1. Configuration System
The configuration system hasn't changed too much, now instead of a fixed name for the file `modId-side.ezc`, it takes a modId and filename. 
The `ModConfigSpec`s now also take the parameter `synced` if the `ModConfigSpec` will be synced between client and server, with this, the `Side` enum was removed.

There is a **LOT** of new methods that may be useful on `ModConfigSpec`

### 2. Item animations
The item animations are **removed** temporally, since i want to make it better than the previous system, i need to think how to do it.
But they are removed from now

### 3. Event system
The event system kinda had a rework, the `EventDispatcher` interface was removed, now the events can be cancellable and have a result (that may cancel the event).
With this update on the event system, there is a **LOT** of new events on the client, and on common. The events now are made with the `EventFactory` methods also.

### 4. World/Level

The `world` package was renamed to `levelgen`. (The most of the changes are internal)

### 5. Networking

The `StreamCodec` that was implemented on the 1.20.5 was backported, with this the `PacketCodec` class was removed.
The `MoonlightCustomPacket` was renamed to `CustomPacket`

### 6. Platform

There is now an `EnvironmentSide` enum that you can use to execute actions on specific sides (CLIENT/SERVER), the methods in this
enum checks automatically if the side where the action should run is the current

### 7. Registry

The registry system was fully reworked, now the `RegistryHelper` can have a set of `Registrar` that will take all the game objects that should be
registered (turning them also into a `RegistryEntry`/`Supplier`). The mod provides a set of builders that will help with the registration of the 
game objects.

Now there is also datapack registry support, using the `DatapackRegistryCreationEvent` you can create/add your own registries/bootstraps to an specific registry

### 8. Transfer API

The transfer API was fully reworked, this being a weird mix of [Common-Storage-Lib](https://www.curseforge.com/minecraft/mc-mods/botarium), the Neoforge's Transfer API
and Fabric's Transfer API. (but it works, that what matters)