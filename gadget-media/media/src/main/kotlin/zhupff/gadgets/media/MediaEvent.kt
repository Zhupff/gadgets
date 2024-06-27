package zhupff.gadgets.media

sealed class MediaEvent(
    val name: String,
) {

    class SIZE_CHANGED(
        val width: Int,
        val height: Int,
    ) : MediaEvent("SIZE CHANGED") {
        override fun toString(): String = "${super.toString()}($width, $height)"
    }

    class BUFFER_UPDATE(
        val percent: Float,
    ) : MediaEvent("BUFFER UPDATE") {
        override fun toString(): String = "${super.toString()}($percent)"
    }

    class PROGRESS_SYNC(
        var current: Long = 0L,
        var duration: Long = 0L,
        var interval: Long = 100L,
    ) : MediaEvent("PROGRESS_SYNC") {
        override fun toString(): String = "${super.toString()}($current, $duration, $interval)"
    }

    object COMPLETION : MediaEvent("COMPLETION") {
    }

    class PLAYER_INFO(
        val what: Int,
        val extra: Int,
    ) : MediaEvent("PLAYER INFO") {
        override fun toString(): String = "${super.toString()}($what, $extra)"
    }

    class PLAYER_ERROR(
        val what: Int,
        val extra: Int,
    ) : MediaEvent("PLAYER ERROR") {
        override fun toString(): String = "${super.toString()}($what, $extra)"
    }

    override fun toString(): String = "MediaEvent[$name]"
}