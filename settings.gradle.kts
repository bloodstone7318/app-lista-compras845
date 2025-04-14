pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.android") version "1.9.23"
        id("com.android.application") version "8.2.0"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShoppingListApp"
include(":app")
