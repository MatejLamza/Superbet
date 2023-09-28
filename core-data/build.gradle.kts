@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
}

android {
    namespace = "matej.lamza.core_data"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    //DI
    implementation(libs.koin)
    implementation(libs.koin.android)

    implementation(libs.sandwich)

    implementation(project(mapOf("path" to ":core-model")))
    implementation(project(mapOf("path" to ":core-network")))
}
