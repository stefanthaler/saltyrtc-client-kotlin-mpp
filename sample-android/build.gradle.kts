plugins {
    id("com.android.application")
    kotlin("android")

    `kotlin-library-conventions`
}

dependencies {
    // salty rtc
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":crypto"))
    implementation(project(":msgpack-default"))
    implementation(project(":logger-default"))
    implementation(project(":websocket-ktor"))
    implementation(project(":task-relayed-data"))

    // web rtc
    implementation("org.webrtc:google-webrtc:1.0.32006")
    
    // kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${v("coroutines")}")

    // android
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.compose.ui:ui:${v("android.compose")}")
    implementation("androidx.compose.material:material:${v("android.compose")}")
    implementation("androidx.compose.ui:ui-tooling-preview:${v("android.compose")}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${v("android.compose")}")
    debugImplementation("androidx.compose.ui:ui-tooling:${v("android.compose")}")
}

android {
    compileSdk = i("android.sdk.compile")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        applicationId = "net.thalerit.saltyrtc.samples.android"
        minSdkVersion(i("android.sdk.min"))
        targetSdkVersion(i("android.sdk.target"))
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = v("android.compose")
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }
    }
    buildToolsVersion = v("android.buildTools")

    sourceSets.getByName("main") {
        java.srcDir("src/main/kotlin")
    }

    sourceSets.getByName("test") {
        java.srcDirs("src/test/kotlin")
    }

    sourceSets.getByName("androidTest") {
        java.srcDir("src/androidTest/kotlin")
    }
}

afterEvaluate {
    tasks.filter { "lint" in it.name }.forEach {
        it.enabled = false
    }
}
