plugins {
    id("com.android.library")
}

android {
    compileSdkVersion(i("android.sdk.min"))

    defaultConfig {
        minSdkVersion(i("android.sdk.min"))
        targetSdkVersion(i("android.sdk.target"))
        versionCode = 1
        versionName = "1.0.0" // semantic versioning
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