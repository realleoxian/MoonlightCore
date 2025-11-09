import me.modmuss50.mpp.ReleaseType

plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("net.neoforged.moddev.legacyforge")

    id("me.modmuss50.mod-publish-plugin")
}

val modId: String by extra
val modName: String by extra
val modVersion: String by extra
val modJavaVersion: String by extra

val mcVersion: String by extra
val parchmentVersion: String by extra
val parchmentMc: String by extra

val forgeVersion: String by extra

val curseProjectId: String by extra
val modrinthId: String by extra

version = "${mcVersion}-${modVersion}.forge"

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
    implementation(jarJar(group = "io.github.llamalad7", name = "mixinextras-common", version = "0.5.0"))
    annotationProcessor(group = "org.spongepowered", name = "mixin", version = "0.8.5", classifier = "processor")

    compileOnly(project(":Xplat"))
}

legacyForge {
    version = "${mcVersion}-${forgeVersion}"

    val atFile = project(":Xplat").file("src/main/resources/META-INF/accesstransformer.cfg")
    if(atFile.exists()) {
        validateAccessTransformers = true
        accessTransformers.from(atFile)
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":Xplat").sourceSets.main.get())
        }
    }

    runs {
        create("client") {
            client()

            ideName.set("Forge Client")
            gameDirectory.set(file("run/client"))
            logLevel.set(org.slf4j.event.Level.DEBUG)

            systemProperty("forge.logging.console.level", "debug")
            systemProperty("fml.earlyprogresswindow", "false")
        }

        create("server") {
            server()

            ideName.set("Forge Server")
            gameDirectory.set(file("run/server"))
            logLevel.set(org.slf4j.event.Level.DEBUG)

            systemProperty("forge.logging.console.level", "debug")
            systemProperty("fml.earlyprogresswindow", "false")
        }
    }
}

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")

    config("${modId}.mixins.json")
    config("${modId}.forge.mixins.json")
}

publishMods {
    file.set(tasks.jar.get().archiveFile)
    changelog.set(provider { file("../Changelog/${modVersion}.md").readText() })
    type = ReleaseType.STABLE
    modLoaders.add("forge")
    displayName.set("[Forge] $modName | ${mcVersion}-${modVersion}")
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
        register<MavenPublication>("forgeJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar.get())
            artifact(tasks.named("sourcesJar").get())
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
    finalizedBy("reobfJar")
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