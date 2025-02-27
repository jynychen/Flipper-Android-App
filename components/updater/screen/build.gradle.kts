plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.updater.screen"

dependencies {
    implementation(projects.components.updater.api)
    implementation(projects.components.info.shared)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.flippermockup)
    implementation(projects.components.core.ui.scrollbar)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.singleactivity.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.changelog.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.serialization.json)

    implementation(projects.components.bridge.connection.orchestrator.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.bundles.decompose)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.appcompat)
}
