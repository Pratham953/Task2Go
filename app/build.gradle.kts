plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.task2go"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.task2go"
        minSdk = 24
        targetSdk = 35
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
    implementation (libs.firebase.ui.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.google.firebase.auth)
    implementation (libs.firebase.auth.v2230)

    dependencies {
        // UI Components
        implementation (libs.com.google.android.material.material.v190.x10)
        implementation (libs.appcompat.v170)
        implementation (libs.constraintlayout.v220)
        implementation (libs.firebase.appcheck.playintegrity)
        implementation (libs.squareup.picasso)
        implementation (libs.androidx.cardview)
        implementation (libs.androidx.cardview)
        implementation (libs.konfetti.xml)
        implementation (libs.material.v1120)

        // Firebase
        implementation (libs.firebase.auth.v2110)
        implementation (libs.firebase.firestore)
        implementation (libs.firebase.storage)
        implementation (libs.com.google.firebase.firebase.auth.v2231.x4)
        implementation (libs.firebase.database.v2031)
        implementation (libs.firebase.storage.v2031)
        implementation (libs.firebase.firestore.v2491)
        implementation (libs.firebase.analytics)
        implementation (libs.play.services.auth)

        // Networking
        implementation (libs.retrofit)
        implementation (libs.converter.gson)

        // Image Loading
        implementation (libs.glide)

        // RecyclerView
        implementation (libs.recyclerview)
        implementation (libs.recyclerview.v121)
        implementation (libs.androidx.cardview)


        // Navigation Components
        implementation (libs.navigation.fragment)
        implementation (libs.navigation.ui)
    }

}

