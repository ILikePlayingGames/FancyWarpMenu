@file:Suppress("UnstableApiUsage")

import ca.tirelesstraveler.DownloadTranslationsTask
import ca.tirelesstraveler.UploadTranslationsTask

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.kyori.blossom") version "1.3.1"
}

//Constants:

val baseGroup: String by project
val mcVersion: String by project
val modid: String by project
val version: String by project
val updateUrl: String by project
val mixinGroup = "$baseGroup.$modid.mixin"

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

// Minecraft configuration:
loom {
    log4jConfigs.from(file("log4j2.xml"))
    launchConfigs {
        "client" {
            property("devauth.enabled", "true")
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.$modid.json")
    }
    mixin {
        defaultRefmapName.set("mixins.$modid.refmap.json")
    }
}

// Blossom:
blossom {
    val replacement: String = if (System.getenv("CI") !== null) {
        updateUrl
    } else {
        projectDir.resolve("version/update.json").toURI().toString()
    }

    replaceToken("@UPDATE_URL@", replacement, "src/main/java/ca/tirelesstraveler/fancywarpmenu/FancyWarpMenu.java")
}

// Dependencies:

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")
    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.2")
}

// Tasks:

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    archiveBaseName.set(modid)
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        // If you don't want mixins, remove these lines
        this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        this["MixinConfigs"] = "mixins.$modid.json"
    }
}

tasks.processResources {
    if (System.getenv("CI") != null) {
        if (System.getenv("GITHUB_RUN_NUMBER") != null) {
            project.version = "${project.version}+${System.getenv("GITHUB_RUN_NUMBER")}"
        } else {
            throw RuntimeException("Environment variable GITHUB_RUN_NUMBER missing on CI build")
        }
    }

    inputs.property("version", project.version)
    inputs.property("mcversion", mcVersion)
    inputs.property("modid", modid)
    inputs.property("mixinGroup", mixinGroup)
    inputs.property("updateUrl", updateUrl)

    filesMatching(listOf("mcmod.info", "mixins.$modid.json")) {
        expand(inputs.properties)
    }

    rename("(.+_at.cfg)", "META-INF/$1")
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }

    // If you want to include other dependencies and shadow them, you can relocate them in here
    // fun relocate(name: String) = relocate(name, "$baseGroup.deps.$name")
}

tasks.register<DownloadTranslationsTask>("downloadTranslations") {
    group = "translations"
    getTranslationsDirectory().set(buildDir.resolve("generated/resources/crowdin"))
}
tasks.register<UploadTranslationsTask>("uploadTranslations") {
    group = "translations"
}

tasks.register<Copy>("copyTranslationsToClassesDirectory") {
    group = "translations"
    from(tasks.getByName("downloadTranslations"))
    into(sourceSets.main.get().java.classesDirectory.get())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

/**
 * Copy built jar into a Minecraft launcher instance for debugging in a production environment
 */
tasks.register<Copy>("copyJarToMinecraftLauncher") {
    from(buildDir.resolve("libs"))
    into(file(System.getenv("MC_LAUNCHER_DIR")))
}

tasks.assemble.get().dependsOn(tasks.remapJar)

sourceSets.main {
    output.resourcesDir = sourceSets.main.get().java.classesDirectory.get().asFile
    output.dir(tasks.getByName("downloadTranslations"))
}