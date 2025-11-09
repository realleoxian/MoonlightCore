plugins {
    id("me.modmuss50.mod-publish-plugin") version("1.1.0") apply(false)

    id("net.neoforged.moddev.legacyforge") version("2.0.112") apply(false)
    id("fabric-loom") version("1.10-SNAPSHOT") apply(false)

    id("org.moddedmc.wiki.toolkit") version("0.3.2")
}

val modId: String by extra
val modName: String by extra
val modVersion: String by extra
val modGroup: String by extra
val modDescription: String by extra
val modLicense: String by extra
val modAuthor: String by extra
val modCredits: String by extra
val mcVersion: String by extra
val modIssueTracker: String by extra
val modJavaVersion: String by extra

val forgeVersion: String by extra
val forgeRange: String by extra
val fmlRange: String by extra

val fabricApi: String by extra
val fabricLoader: String by extra

subprojects {
    group = modGroup

    repositories {
        fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) {
            exclusiveContent {
                forRepository { maven(url) }
                filter(filter)
            }
        }

        exclusiveMaven("https://repo.spongepowered.org/repository/maven-public/") {
            includeGroupByRegex("org\\.spongepowered\\.mixin.*")
        }

        exclusiveMaven("https://maven.parchmentmc.org/") {
            includeGroupByRegex("org\\.parchmentmc\\.data.*")
        }

        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Jar> {
        manifest {
            attributes(mapOf(
                "Specification-Title" to modId,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to 1,
                "Implementation-Title" to project.name,
                "Implementation-Vendor" to modAuthor,
                "Implementation-Version" to archiveVersion,
                "Built-On-Minecraft" to mcVersion
            ))
        }
    }

    tasks.withType<ProcessResources> {
        val expandedProperties = mapOf(
            "modId" to modId, "modName" to modName, "modVersion" to modVersion, "modAuthor" to modAuthor, "modCredits" to modCredits,
            "modDescription" to modDescription, "modLicense" to modLicense, "modIssueTracker" to modIssueTracker,

            "forgeVersion" to forgeVersion, "forgeRange" to forgeRange, "fmlRange" to fmlRange,
            "fabricApi" to fabricApi, "fabricLoader" to fabricLoader
        )

        val jsonExpandedProperties = expandedProperties.mapValues { (_, value) -> value.replace("\n", "\\\\n") }

        filesMatching(listOf("META-INF/mods.toml")) {
            expand(expandedProperties)
        }

        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) {
            expand(jsonExpandedProperties)
        }

        inputs.properties(expandedProperties)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.withType<JavaCompile> {
        options.release = Integer.parseInt(modJavaVersion)
        options.compilerArgs.add("-Xlint:unchecked")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        val stdJavadocDocletOptions = options as StandardJavadocDocletOptions
        stdJavadocDocletOptions.addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }
}

wiki {
    docs.create(modId) {
        root = file("docs")
    }
}