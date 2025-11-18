package gadget.theme

internal open class ThemeMergeTask {

    companion object {
        fun getTaskName(variant: String): String {
            return ThemeMergeTask::class.java.simpleName + variant.replaceFirstChar { it.uppercaseChar() }
        }
    }
}