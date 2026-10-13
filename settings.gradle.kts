import java.util.Base64

pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Audita"

// Auto-restore debug.keystore from debug.keystore.base64 for local Android Studio builds
val keystoreFile = File(settingsDir, "debug.keystore")
val base64File = File(settingsDir, "debug.keystore.base64")
if (!keystoreFile.exists()) {
  if (base64File.exists()) {
    try {
      val decoded = java.util.Base64.getDecoder().decode(base64File.readText().trim())
      keystoreFile.writeBytes(decoded)
    } catch (_: Exception) {}
  } else {
    val userHomeKeystore = File(System.getProperty("user.home"), ".android/debug.keystore")
    if (userHomeKeystore.exists()) {
      try {
        userHomeKeystore.copyTo(keystoreFile, overwrite = true)
      } catch (_: Exception) {}
    }
  }
}

include(":app")
