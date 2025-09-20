import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.android.kotlin.kapt)
}

android {
    namespace = "com.nicos.imagepickerandroidexample"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.nicos.imagepickerandroidexample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildFeatures {
            viewBinding = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("17")
        }
    }
}

dependencies {
    implementation(project(":ImagePickerAndroid"))
    // Architecture
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //noinspection LifecycleAnnotationProcessorWithJava8
    kapt(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // RecyclerView
    implementation(libs.androidx.recyclerview)
    // Coroutines
    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)
    // Unit Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}