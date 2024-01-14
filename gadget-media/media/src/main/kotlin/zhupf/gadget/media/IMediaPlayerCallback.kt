package zhupf.gadget.media

interface IMediaPlayerCallback {
    fun onAttach(player: IMediaPlayer) {}
    fun onDetach(player: IMediaPlayer) {}
    fun onEvent(
        player: IMediaPlayer,
        code: Int,
        what: Int,
        arg1: Int = IMediaPlayer.CODE_NULL,
        arg2: Int = IMediaPlayer.CODE_NULL,
        obj: Any? = null,
    ): Any? {
        when (code) {
            IMediaPlayer.STATE_CODE -> {
                when (what) {
                    IMediaPlayer.STATE_SOUGHT -> {
                        // arg1 = 1: autoplay, otherwise keep state.
                    }
                }
            }
            IMediaPlayer.INFO_CODE -> {
                when (what) {
                    IMediaPlayer.INFO_MEDIA -> {
                        // (arg1, arg2) = onInfo(what, extra)
                    }
                    IMediaPlayer.INFO_BUFFERING -> {
                        // arg1 = percent in 0..100
                    }
                    IMediaPlayer.INFO_SIZE_CHANGED -> {
                        // (arg1, arg2) = (width, height)
                    }
                    IMediaPlayer.INFO_PROGRESS_SYNC -> {
                        // arg1 = progress, arg2 = duration, obj = null.
                        // return next sync interval(Int in milliseconds).
                    }
                }
            }
            IMediaPlayer.ERROR_CODE -> {
                when (what) {
                    IMediaPlayer.ERROR_MEDIA -> {
                        // (arg1, arg2) = onError(what, extra)
                    }
                }
            }
        }
        return null
    }
}