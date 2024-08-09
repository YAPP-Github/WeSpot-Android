import org.jetbrains.kotlin.konan.properties.Properties

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())

plugins {
    alias(libs.plugins.wespot.android.library)
    alias(libs.plugins.wespot.android.hilt)
}

android {
    namespace = "com.bff.wespot.data"

    defaultConfig {
        buildConfigField("String", "MOCK_BASE_URL", properties.getProperty("MOCK_BASE_URL"))
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data-remote"))
    implementation(project(":data-local"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))

    implementation(libs.bundles.ktor.client)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.timber)
    implementation(libs.junit)
    implementation(libs.kakao.sdk)
    implementation(libs.datastore)
}
