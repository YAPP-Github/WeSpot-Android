plugins {
    alias(libs.plugins.wespot.android.feature)
    alias(libs.plugins.wespot.android.compose)
    alias(libs.plugins.wespot.android.hilt)
}

android {
    namespace = "com.bff.wespot.entire"
}

ksp {
    arg("compose-destinations.moduleName", "entire")
    arg("compose-destinations.mode", "destinations")
}

dependencies {
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.orbit)
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.coil.core)
    implementation(libs.coil.compose)
    implementation(libs.timber)
    implementation(libs.paging3)
}
