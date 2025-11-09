pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "FabricMC"
        }

        maven("https://maven.neoforged.net/releases") {
            name = "Neoforged"
        }

        maven("https://repo.spongepowered.org/repository/maven-public") {
            name = "Spongepowered"
        }

        gradlePluginPortal()
    }
}

include("Xplat", "Fabric", "Forge")