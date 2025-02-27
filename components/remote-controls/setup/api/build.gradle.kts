plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.setup.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.infrared.utils)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.keyemulate.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
