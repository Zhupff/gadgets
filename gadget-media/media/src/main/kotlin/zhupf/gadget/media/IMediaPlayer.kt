package zhupf.gadget.media

interface IMediaPlayer {
    companion object {
        const val CODE_NULL = -1

        const val STATE_CODE   = 1000
        const val STATE_IDLE         = STATE_CODE + 1
        const val STATE_PREPARING    = STATE_CODE + 2
        const val STATE_PREPARED     = STATE_CODE + 3
        const val STATE_PLAYING      = STATE_CODE + 4
        const val STATE_PAUSING      = STATE_CODE + 5
        const val STATE_SEEKING      = STATE_CODE + 6
        const val STATE_SOUGHT       = STATE_CODE + 7
        const val STATE_RELEASING    = STATE_CODE + 8
        const val STATE_RELEASED     = STATE_CODE + 9

        const val INFO_CODE    = 2000
        const val INFO_MEDIA         = INFO_CODE + 1
        const val INFO_BUFFERING     = INFO_CODE + 2
        const val INFO_SIZE_CHANGED  = INFO_CODE + 3
        const val INFO_PROGRESS_SYNC = INFO_CODE + 4
        const val INFO_COMPLETION    = INFO_CODE + 5

        const val ERROR_CODE   = 3000
        const val ERROR_MEDIA        = ERROR_CODE + 1
    }

    fun prepare(mediaSource: MediaSource<*>): Boolean

    fun play(seekTo: Int): Boolean

    fun pause(): Boolean

    fun seekTo(seekTo: Int): Boolean

    fun release()
}