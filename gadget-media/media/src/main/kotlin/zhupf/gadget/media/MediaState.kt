package zhupf.gadget.media

class MediaState(
    val TAG: String
) {
    var current: Int = IMediaPlayer.STATE_IDLE
    var last: Int = current

    fun switch(newState: Int) {
        if (current == newState) return
        TAG.d("swtich($newState)")
        last = current
        current = newState
    }

    fun rollback(): Boolean {
        TAG.d("rollback(), current=$current, last=$last")
        if (current == last) return false
        current = last
        return true
    }

    override fun toString(): String = "($current, $last)"
}