plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.filemanager.search.api"

commonDependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.okio)
}
