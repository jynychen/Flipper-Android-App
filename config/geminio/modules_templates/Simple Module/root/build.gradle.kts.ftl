plugins {
<#if needCompose>
    id("flipper.android-compose")
<#else>
    id("flipper.android-lib")
</#if>
<#if shouldGenerateDI>
    id("flipper.anvil")
</#if>
}

android.namespace = "${packageName}"

dependencies {
<#if needCompose>
    implementation(projects.components.core.ui.theme)
</#if>
<#if shouldGenerateDI>
    implementation(projects.components.core.di)
</#if>

<#if needCompose>
    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
</#if>

<#if needViewModel>
    // ViewModel
    implementation(libs.lifecycle.compose)

</#if>

<#if needTest>
    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
</#if>
}
