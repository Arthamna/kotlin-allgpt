buildscript {
    extra.apply {
        set("compose_ui_version", "1.1.1")
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
}

plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.android.library") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}