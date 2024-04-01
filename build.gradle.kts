plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.21"
  id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.beiyelin"
version = "20240331.01"

repositories {
  maven { setUrl("https://maven.aliyun.com/repository/central/")}
  maven { setUrl("https://maven.aliyun.com/repository/public/")}
  maven { setUrl("https://maven.aliyun.com/repository/google/")}
  maven { setUrl("https://maven.aliyun.com/repository/jcenter/")}
  maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin")}
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.3.2")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf(/* Plugin Dependencies */
    "java",
    "com.intellij.java"
  ))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("233")
    untilBuild.set("241.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
