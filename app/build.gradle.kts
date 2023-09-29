@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.google.maps.get().pluginId)
}

android {
    namespace = "matej.lamza.superbet"
    compileSdk = 33

    defaultConfig {
        applicationId = "matej.lamza.superbet"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    //region CORE
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    //endregion

    //region LIFECYCLE
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.livedata)
    //endregion

    //region MAPS
    implementation(libs.maps.utils.ktx)
    implementation(libs.maps.utils.k)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.maps.utils.android)
    //endregion

    //region DI
    implementation(libs.koin)
    implementation(libs.koin.android)

    //endregion

    implementation(project(mapOf("path" to ":core-data")))
    implementation(project(mapOf("path" to ":core-network")))
    implementation(project(mapOf("path" to ":core-model")))
    
    //region TESTS
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //endregion
}
