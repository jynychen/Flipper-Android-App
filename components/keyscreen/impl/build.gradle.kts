plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.keyscreen.impl"

dependencies {
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.share.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.archive.api)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.infrared.api)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.preference)

    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.emulate.api)

    implementation(projects.components.keyedit.api)
    implementation(projects.components.nfceditor.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    implementation(libs.kotlin.serialization.json)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)
    implementation(libs.image.lottie)

    implementation(libs.appcompat)

    implementation(libs.lifecycle.compose)

    implementation(libs.lifecycle.runtime.ktx)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.roboelectric)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.mockk)
}
