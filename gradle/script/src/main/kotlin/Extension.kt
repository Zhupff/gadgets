import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal typealias AndroidExtension = CommonExtension<*, *, *, *, *>

internal val Project.androidExtension: AndroidExtension
    get() = if (pluginManager.hasPlugin("com.android.application"))
        applicationExtension
    else if (pluginManager.hasPlugin("com.android.library"))
        libraryExtension
    else
        throw RuntimeException()

internal val Project.applicationExtension: ApplicationExtension
    get() = extensions.getByType(ApplicationExtension::class.java)

internal val Project.libraryExtension: LibraryExtension
    get() = extensions.getByType(LibraryExtension::class.java)

internal val Project.appExtension: com.android.build.gradle.AppExtension
    get() = extensions.getByType(com.android.build.gradle.AppExtension::class.java)

internal val Project.libExtension: com.android.build.gradle.LibraryExtension
    get() = extensions.getByType(com.android.build.gradle.LibraryExtension::class.java)

internal val Project.versionCatalogExtension: VersionCatalogsExtension
    get() = extensions.getByType(VersionCatalogsExtension::class.java)

internal val Project.libs: VersionCatalog
    get() = versionCatalogExtension.named("libs")

internal val com.android.build.gradle.api.BaseVariant.variantName: String; get() = name

internal fun AndroidExtension.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

internal fun String.toCamelCase(): String = replaceFirstChar { it.uppercaseChar() }