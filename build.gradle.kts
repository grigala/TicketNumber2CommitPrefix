import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask.FailureLevel
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.changelog")
    id("org.jetbrains.intellij.platform")
}

// Oldest supported IDE. The plugin is compiled against it, so APIs missing there can't be used by accident.
// 2024.2 is the first release running on Java 21, which is the bytecode level this build produces.
val oldestSupportedIde = "2024.2.6"
val oldestSupportedBuild = "242"

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation(libs.junit)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdeaCommunity(oldestSupportedIde)
        bundledPlugin("Git4Idea")
    }
}

kotlin {
    compilerOptions {
        // The plugin uses the Kotlin stdlib bundled with the IDE; 2024.2 ships Kotlin 2.0.
        apiVersion = KotlinVersion.KOTLIN_2_0
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = oldestSupportedBuild
            // No upper bound: the plugin stays installable on future IDE versions without a new release.
            untilBuild = provider { null }
        }
    }

    // `./gradlew verifyPlugin` checks binary compatibility against the oldest supported IDE and all newer releases.
    pluginVerification {
        ides {
            create(IntelliJPlatformType.IntellijIdeaCommunity, oldestSupportedIde)
            select {
                types = listOf(IntelliJPlatformType.IntellijIdea)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "253"
            }
        }
        // Internal / to-be-removed APIs are what typically break plugins in future IDE versions.
        failureLevel = listOf(
            FailureLevel.COMPATIBILITY_PROBLEMS,
            FailureLevel.INTERNAL_API_USAGES,
            FailureLevel.SCHEDULED_FOR_REMOVAL_API_USAGES,
            FailureLevel.MISSING_DEPENDENCIES,
            FailureLevel.INVALID_PLUGIN,
        )
    }
}
