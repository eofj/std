plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")  // 카카오 SDK 저장소 추가
    }
}


