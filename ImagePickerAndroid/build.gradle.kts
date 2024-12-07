plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    namespace = "com.nick.imagepickerandroid"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        minSdk = 24
        lint.targetSdk = 35
        testOptions.targetSdk = 35
        buildFeatures {
            compose = true
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
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

val appCompatVersion by extra("1.7.0")
val coreKtxVersion by extra("1.15.0")
val constraintLayoutVersion by extra("2.2.0")
val lifeCycleAndLiveDataCompilerAndViewModelKTXVersion by extra("2.8.7")
val activityVersion by extra("1.9.3")
val fragmentVersion by extra("1.6.1")
val coroutineVersion by extra("1.9.0")
val multidexVersion by extra("2.0.1")
val materialDesignVersion by extra("1.12.0")
val recyclerViewVersion by extra("1.3.2")
val composeVersion by extra("1.7.5")
val composeLiveDataVersion by extra("1.7.5")
val composeFoundationVersion by extra("1.7.5")
val composeMaterialVersion by extra("1.7.5")
val composeMaterial3Version by extra("1.3.1")

dependencies {
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialDesignVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifeCycleAndLiveDataCompilerAndViewModelKTXVersion")
    //noinspection LifecycleAnnotationProcessorWithJava8
    kapt("androidx.lifecycle:lifecycle-compiler:$lifeCycleAndLiveDataCompilerAndViewModelKTXVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifeCycleAndLiveDataCompilerAndViewModelKTXVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifeCycleAndLiveDataCompilerAndViewModelKTXVersion")
    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:$recyclerViewVersion")
    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    //Unit Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //Compose
    implementation("androidx.compose.foundation:foundation:$composeFoundationVersion")
    implementation("androidx.compose.material:material:$composeMaterialVersion")
    implementation("androidx.compose.material3:material3:$composeMaterial3Version")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeLiveDataVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifeCycleAndLiveDataCompilerAndViewModelKTXVersion")
    implementation("androidx.activity:activity-compose:$activityVersion")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                groupId = "com.github.NicosNicolaou16"
                artifactId = "ImagePickerAndroid"
                version = "2.3.0"
                from(components["release"])
            }
        }
    }
}
