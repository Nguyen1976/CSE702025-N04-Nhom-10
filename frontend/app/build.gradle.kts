import com.android.tools.analytics.AnalyticsSettings.disable

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.taskify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.taskify"
        minSdk = 34
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("androidx.compose.material3:material3:1.1.0")

    val credentialsManagerVersion = "1.5.0-alpha05"

    implementation("androidx.credentials:credentials:$credentialsManagerVersion")
    implementation("androidx.credentials:credentials-play-services-auth:$credentialsManagerVersion")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Icon
    implementation ("androidx.compose.material:material-icons-extended")

    // Constraint Layout
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // Converter Gson (parse JSON)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")

    // Logging Interceptor (log request/response)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Navigation
    implementation ("androidx.navigation:navigation-compose:2.7.0")

    // Other
    implementation ("androidx.compose.foundation:foundation:1.5.0")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation ("androidx.compose.material3:material3:1.1.0")
}