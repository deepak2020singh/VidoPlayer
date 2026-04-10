import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.3.10"
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.foss.vidoplay"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.foss.vidoplay"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    //noinspection WrongGradleMethod
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    val keystorePropertiesFile = file("$rootDir/../local.properties")
    val keystoreProperties = Properties()
    val keystoreExists = keystorePropertiesFile.exists()

    if (keystoreExists) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
        println("Keystore properties loaded successfully.")
    } else {
        println("Keystore properties file not found. No signing configuration will be applied.")
    }

    signingConfigs {
        if (keystoreExists) {
            create("release") {
                storeFile = file("$rootDir/../keystore.jks")
                storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD") ?: ""
                keyAlias = keystoreProperties.getProperty("KEY_ALIAS") ?: ""
                keyPassword = keystoreProperties.getProperty("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            if (keystoreExists) {
                signingConfig = signingConfigs.getByName("release")
            }
        }

        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)



    implementation(libs.media3.exoplayer)

    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.datasource)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.effect)
    implementation(libs.androidx.compose.material.icons.extended.android)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)
    implementation(libs.koin.test)
    implementation(libs.koin.androidx.compose.navigation)

    implementation(libs.coil.compose)
    implementation(libs.coil.video)

    ksp(libs.androidx.room.compiler)
}