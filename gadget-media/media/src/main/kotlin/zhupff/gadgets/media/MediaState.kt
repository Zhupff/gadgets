package zhupff.gadgets.media

sealed class MediaState(
    val name: String,
) {

    object IDLE : MediaState("IDLE") {
    }

    class PREPARING(
        val source: String,
        val callback: Runnable? = null,
    ) : MediaState("PREPARING") {
        override fun toString(): String = "${super.toString()}($source)"
    }

    class PREPARED(
        val source: String,
        val callback: Runnable? = null,
    ) : MediaState("PREPARED") {
        override fun toString(): String = "${super.toString()}($source)"
    }

    class PLAYING(
        val callback: Runnable? = null,
    ) : MediaState("PLAYING") {
    }

    class PAUSING(
        var isCompleted: Boolean = false,
        val callback: Runnable? = null,
    ) : MediaState("PAUSING") {
        override fun toString(): String = "${super.toString()}($isCompleted)"
    }

    class SEEKING(
        val toMs: Long,
        val callback: Runnable? = null,
    ) : MediaState("SEEKING") {
        override fun toString(): String = "${super.toString()}($toMs)"
    }

    class SOUGHT(
        val toMs: Long,
        val callback: Runnable? = null,
    ) : MediaState("SOUGHT") {
        override fun toString(): String = "${super.toString()}($toMs)"
    }

    object RELEASING : MediaState("RELEASING") {
    }

    object RELEASED : MediaState("RELEASED") {
    }


    fun isPrepared(): Boolean = when(this) {
        is PREPARED,
        is PLAYING,
        is PAUSING,
        is SEEKING,
        is SOUGHT -> true
        else -> false
    }

    fun isPlayable(): Boolean = when(this) {
        is PREPARED,
        is PLAYING,
        is PAUSING,
        is SOUGHT -> true
        else -> false
    }

    fun isAlive(): Boolean = when(this) {
        is RELEASING,
        is RELEASED -> false
        else -> true
    }

    override fun toString(): String = "MediaState[$name]"
}