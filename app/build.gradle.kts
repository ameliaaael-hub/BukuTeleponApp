plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bukutelepon"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bukutelepon"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation ("com.google.firebase:firebase-database") // Realtime Database
    implementation ("com.google.firebase:firebase-storage")  // Untuk Upload Foto

    implementation("com.google.firebase:firebase-auth")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Library untuk load gambar dari URL
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")
}