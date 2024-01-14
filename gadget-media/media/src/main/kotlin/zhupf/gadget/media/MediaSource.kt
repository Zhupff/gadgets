package zhupf.gadget.media

data class MediaSource<K>(
    val key: K,
    val source: String,
) {
    var prepared: Boolean = false

    var duration: Int = 0

    var width: Int = 0

    var height: Int = 0

    override fun toString(): String = "MediaSource($key, $source)"
}