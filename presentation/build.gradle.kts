plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain.dependencies {
            // other modules
            implementation(projects.domain)
            implementation(projects.dataUtils)

            // compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            // kotlinx datetime
            implementation(libs.kotlinx.datetime)

            // koin
            implementation(libs.koin.compose.viewmodel)

            // icons
            implementation(libs.material.icons.extended)

            // arrow
            implementation(libs.arrow.core)

            // navigation
            implementation(libs.navigation.compose)

            implementation(libs.kermit)

            // coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.paging.compose)
            implementation(libs.paging.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation("com.avsystem.cemmobile:cem-sdk-android-debug:1.0.0-SNAPSHOT")
        }
    }
}

android {
    namespace = "com.mzs.basket_krk.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
}