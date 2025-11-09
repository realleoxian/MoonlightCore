import me.modmuss50.mpp.ReleaseType

plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("fabric-loom")

    id("me.modmuss50.mod-publish-plugin")
}

val modId: String by extra
val modName: String by extra
val modVersion: String by extra
val modJavaVersion: String by extra

val mcVersion: String by extra
val parchmentVersion: String by extra
val parchmentMc: String by extra

val fabricApi: String by extra
val fabricLoader: String by extra

val curseProjectId: String by extra
val modrinthId: String by extra

version = "${mcVersion}-${modVersion}.fabric"

base {
    archivesName.set(modName)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }

    withSourcesJar()
}

evaluationDependsOn(project(":Xplat").path)

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = mcVersion)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${parchmentMc}:${parchmentVersion}@zip")
    })

    modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "${fabricApi}+${mcVersion}")
    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = fabricLoader)

    include(implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.1"))

    compileOnly(project(":Xplat"))
}

loom {
    val awFile = file("src/main/resources/${modId}.accesswidener")
    if(awFile.exists()) {
        accessWidenerPath.set(awFile)
    }

    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":Xplat").sourceSets.main.get())
        }
    }

    runs {
        var loomRunDir = File("run")

        named("client") {
            client()

            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir(loomRunDir.resolve("client").toString())

            vmArgs("-Dfabric.log.level=info")
        }
        named("server") {
            server()

            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir(loomRunDir.resolve("server").toString())

            vmArgs("-Dfabric.log.level=info")
        }
    }
}

publishMods {
    file.set(tasks.remapJar.get().archiveFile)
    changelog.set(provider { file("../changelog/${modVersion}.md").readText() })
    type = ReleaseType.STABLE
    modLoaders.add("fabric")
    displayName.set("[Fabric] $modName | ${mcVersion}-${modVersion}")
    version.set(project.version.toString())

    curseforge {
        projectId = curseProjectId
        accessToken.set(System.getProperty("CURSEFORGE_API_KEY"))
        minecraftVersions.add(mcVersion)
        javaVersions.add(JavaVersion.toVersion(modJavaVersion))
        dryRun = System.getProperty("CURSEFORGE_API_KEY") != null
    }

    modrinth {
        projectId = modrinthId
        accessToken.set(System.getProperty("MODRINTH_TOKEN"))
        minecraftVersions.add(mcVersion)
        dryRun = System.getProperty("MODRINTH_TOKEN") != null
    }
}

publishing {
    publications {
        register<MavenPublication>("fabricJar") {
            @Suppress("UnstableApiUsage")
            loom.disableDeprecatedPomGeneration(this)

            artifactId = base.archivesName.get()
            artifact(tasks.remapJar)
            artifact(tasks.remapSourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("file://" + System.getenv("local_maven"))
        }
    }
}

sourceSets {
    named("main") {
        resources {
            srcDir(project(":Xplat").sourceSets.main.get().resources)
        }
    }
}

idea {
    module {
        for(fileName in listOf("build", "run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}

tasks.jar {
    from(sourceSets.main.get().output)
    from(project(":Xplat").sourceSets.main.get().output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    from(project(":Xplat").sourceSets.main.get().allJava)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("sources")
}

tasks.withType<JavaCompile> {
    source(project(":Xplat").sourceSets.main.get().allSource)
    options.encoding = "UTF-8"

    javaToolchains {
        compilerFor {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        }
    }
}