package zhupff.gadgets.theme

open class ThemeConfig(
    val prefix: String,
    val attributes: List<ThemeAttribute>,
) {

    protected val attributesMap = attributes.associateBy { it.attributeName }

    open fun obtainAttribute(attributeName: String, resourceId: Int, resourceName: String, resourceType: String): ThemeAttribute? =
        attributesMap[attributeName]?.copy(resourceId, resourceName, resourceType)

    open fun obtainAttribute(attributeName: String, resourceId: Int): ThemeAttribute? = obtainAttribute(
        attributeName,
        resourceId,
        APPLICATION.resources.getResourceEntryName(resourceId),
        APPLICATION.resources.getResourceTypeName(resourceId)
    )
}