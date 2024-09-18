pluginManagement {
    repositories {
        maven { setUrl("https://mirrors.cloud.tencent.com/gradle/")}
        maven { setUrl("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")}
        maven { setUrl("https://maven.aliyun.com/repository/central/")}
        maven { setUrl("https://maven.aliyun.com/repository/public/")}
        maven { setUrl("https://maven.aliyun.com/repository/google/")}
        maven { setUrl("https://maven.aliyun.com/repository/jcenter/")}
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin")}

        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "moqui-idea-plugin"