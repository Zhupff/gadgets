package zhupff.gadgets.theme

import androidx.core.content.res.ResourcesCompat

open class ResourceVariantTheme(
    name: String,
    val variant: String,
    parent: ResourceTheme,
) : ResourceTheme(name, parent, parent.resources) {

    override fun getIdentifier(id: Int): Int {
        return idCache.getOrPut(id) {
            try {
                val name = APPLICATION.resources.getResourceEntryName(id)
                val type = APPLICATION.resources.getResourceTypeName(id)
                resources.getIdentifier(variant + name, type, themeId)
            } catch (throwable: Throwable) {
                ResourcesCompat.ID_NULL
            }
        }
    }
}