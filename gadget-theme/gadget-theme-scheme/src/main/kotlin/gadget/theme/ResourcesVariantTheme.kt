package gadget.theme

import androidx.core.content.res.ResourcesCompat

open class ResourcesVariantTheme(
    id: String,
    parent: ResourcesTheme,
    val variant: String,
) : ResourcesTheme(id, parent, parent.packageName, parent.resources) {

    override fun getIdentifier(r: Resource): Int {
        return idCaches.getOrPut(r.id) {
            try {
                resources.getIdentifier(variant + '_' + r.name, r.type, packageName)
            } catch (throwable: Throwable) {
                ResourcesCompat.ID_NULL
            }
        }
    }
}