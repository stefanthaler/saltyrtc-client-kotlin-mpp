plugins {
    id("com.android.library")
}

android {
    compileSdk = i("android.sdk.min")

    defaultConfig {
        minSdk = i("android.sdk.min")
        targetSdk = i("android.sdk.target")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDir("src/androidMain/kotlin")
            res.srcDir("src/androidMain/res")
        }
    }
}
