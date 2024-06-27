package zhupff.gadgets.media

interface IMediaPlayer {

    fun prepare(source: String, callback: Runnable?): Boolean

    fun play(callback: Runnable?): Boolean

    fun pause(callback: Runnable?): Boolean

    fun seek(toMs: Long, callback: Runnable?): Boolean

    fun reset()

    fun release()
}