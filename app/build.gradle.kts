plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.androlearn.ot"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.androlearn.ot"
        minSdk = 26
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val marketApplicationId = "ir.mservices.market"
        val marketBindAddress =
            "ir.mservices.market.InAppBillingService.BIND"

        manifestPlaceholders["marketApplicationId"] =
            marketApplicationId

        manifestPlaceholders["marketBindAddress"] =
            marketBindAddress

        manifestPlaceholders["marketPermission"] =
            "$marketApplicationId.BILLING"

        buildConfigField(
            "String",
            "IAB_PUBLIC_KEY",
            "\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChiCGsXZw5U9sXWwNoc5dkf1hQjdj3parlH9Y1UHKVdv74+YcHbh2rTiykVbo9BJdELwrp9cfGj7XiPbTzX5Kgqif8JAdHgVcm0/aELYWkaSX5YXldQ2BSgn+MwFPOaV+C0mw2jb6OtoKVBArDNGNpOoJyHSg41392K3/3zx+kOwIDAQAB\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.androidx.datastore.preferences)

    //myket
    implementation(libs.billing)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Debug
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}