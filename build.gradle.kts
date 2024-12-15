plugins {
  id("java")
//  id("groovy")
  id("org.jetbrains.kotlin.jvm") version "1.9.21"
//  id("org.jetbrains.intellij") version "1.16.1"
  id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.beiyelin"
version = "20241124.01"

repositories {
  maven { setUrl("https://mirrors.cloud.tencent.com/gradle/")}
  maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")}
  maven { setUrl("https://maven.aliyun.com/repository/central/")}
  maven { setUrl("https://maven.aliyun.com/repository/public/")}
  maven { setUrl("https://maven.aliyun.com/repository/google/")}
  maven { setUrl("https://maven.aliyun.com/repository/jcenter/")}
  maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin")}
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}
dependencies {
  intellijPlatform {
    intellijIdeaCommunity("2023.1")

    bundledPlugin("com.intellij.java")
    bundledPlugin("org.intellij.groovy")
    bundledPlugin("org.intellij.intelliLang")

    pluginVerifier()
    zipSigner()
    instrumentationTools()
//    create("IC", "2023.3.2")
  }
  // 使用 Maven Central 仓库中的依赖
  implementation("org.freemarker:freemarker:2.3.32")
//  implementation("org.jgrapht:jgrapht-core:1.5.2")
//  implementation("com.github.tomnelson:jungrapht-visualization:1.4")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}
// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
//intellij {
//  version.set("2023.3.2")
//  type.set("IC") // Target IDE Platform
//
//  plugins.set(listOf(/* Plugin Dependencies */
//    "java",
//    "com.intellij.java",
//    "org.intellij.groovy",
//    "org.intellij.intelliLang"
//  ))
//}

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
    sinceBuild.set("231") //2023.1版https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html#earlier-versions
//    untilBuild.set("241.*")//2024.1 java 17
    untilBuild.set("243.*")
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
