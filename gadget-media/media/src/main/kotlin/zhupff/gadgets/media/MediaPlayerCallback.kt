package zhupff.gadgets.media

interface MediaPlayerCallback {

    fun onAttached(player: IMediaPlayer)

    fun onDetached(player: IMediaPlayer)

    fun onMediaState(state: MediaState)

    fun onMediaEvent(event: MediaEvent)
}