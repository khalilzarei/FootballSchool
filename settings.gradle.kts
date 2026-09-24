pluginManagement {
    repositories {
        maven { url = uri("https://maven.myket.ir/") }
//        google()
//        mavenCentral()
//        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        maven { url = uri("https://maven.myket.ir/") }
//        google()
//        mavenCentral()

//        maven { url 'https://maven.myket.ir' }
    }
}

rootProject.name = "MalekAdmin"
include(":app")
