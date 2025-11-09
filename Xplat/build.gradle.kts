plugins {
    id("java")
    id("idea")
    id("maven-publish")

    id("net.neoforged.moddev.legacyforge")
}

val modName: String by extra
val modVersion: String by extra
val modJavaVersion: String by extra

val mcVersion: String by extra
val parchmentVersion: String by extra

version = "${mcVersion}-${modVersion}"

base {
    archivesName.set(modName)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }

    withSourcesJar()
}

dependencies {
    compileOnly(group = "io.github.llamalad7", name = "mixinextras-common", version = "0.5.0")
    compileOnly(group = "org.spongepowered", name = "mixin",  version = "0.8.5")

    compileOnly(group = "org.jetbrains", name = "annotations", version = "24.0.1")
}


legacyForge {
    mcpVersion = mcVersion

    val atFile = file("src/main/resources/META-INF/accesstransformer.cfg")
    if(atFile.exists()) {
        validateAccessTransformers.set(true)
        accessTransformers.from(atFile)
    }

    parchment {
        minecraftVersion = mcVersion
        mappingsVersion = parchmentVersion
    }
}

publishing {
    publications {
        register<MavenPublication>("xplatJar") {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))
        }
    }
    repositories {
        maven {
            url = uri("file://" + System.getenv("local_maven"))
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

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    javaToolchains {
        compilerFor {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        }
    }
}