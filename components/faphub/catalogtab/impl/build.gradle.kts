plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.faphub.catalogtab.impl"

dependencies {
    implementation(projects.components.faphub.catalogtab.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.pager)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.errors.api)
    implementation(projects.components.faphub.appcard.composable)
    implementation(projects.components.faphub.installation.button.api)
    implementation(projects.components.faphub.installation.manifest.api)
    implementation(projects.components.faphub.target.api)

    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.paging)
    implementation(libs.decompose)

    implementation(libs.kotlin.immutable.collections)
}
