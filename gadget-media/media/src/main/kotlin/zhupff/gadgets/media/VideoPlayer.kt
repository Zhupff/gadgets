package zhupff.gadgets.media

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.annotation.MainThread

class VideoPlayer : IMediaPlayer {

    private val TAG = "VideoPlayer(${hashCode()})"

    private val mediaPlayerListeners = MediaPlayerListeners()

    private val player = MediaPlayer().also { player ->
        player.setOnPreparedListener(mediaPlayerListeners)
        player.setOnBufferingUpdateListener(mediaPlayerListeners)
        player.setOnCompletionListener(mediaPlayerListeners)
        player.setOnSeekCompleteListener(mediaPlayerListeners)
        player.setOnVideoSizeChangedListener(mediaPlayerListeners)
        player.setOnInfoListener(mediaPlayerListeners)
        player.setOnErrorListener(mediaPlayerListeners)
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build()
        )
    }

    private val handler = Handler(Looper.getMainLooper()) { message ->
        if (state.isAlive()) {
            val messageObj = message.obj
            if (messageObj is MediaState) {
                when (messageObj) {
                    is MediaState.IDLE -> {
                        state = messageObj
                        player.reset()
                    }

                    is MediaState.PREPARING -> {
                        state = messageObj
                        this.source = messageObj.source
                        player.reset()
                        player.setDataSource(messageObj.source)
                        player.prepareAsync()
                    }

                    is MediaState.PREPARED -> {
                        state = messageObj
                        messageObj.callback?.run()
                    }

                    is MediaState.PLAYING -> {
                        player.start()
                        state = messageObj
                        messageObj.callback?.run()
                        if (!hasMessage(progress)) {
                            progress.interval = 100L
                            sendMessage(progress)
                        }
                    }

                    is MediaState.PAUSING -> {
                        player.pause()
                        state = messageObj
                        messageObj.callback?.run()
                    }

                    is MediaState.SEEKING -> {
                        state = messageObj
                        player.seekTo(messageObj.toMs.coerceAtLeast(0).toInt())
                    }

                    is MediaState.SOUGHT -> {
                        state = messageObj
                        messageObj.callback?.run()
                    }

                    is MediaState.RELEASING -> {
                        state = messageObj
                        player.stop()
                        player.release()
                        state = MediaState.RELEASED
                        removeMessage(null)
                    }

                    else -> {}
                }
            } else if (messageObj is MediaEvent) {
                when (messageObj) {
                    is MediaEvent.PROGRESS_SYNC -> {
                        messageObj.current = current
                        messageObj.duration = duration
                        val cb = callback
                        if (state is MediaState.PLAYING && cb != null) {
                            event = messageObj
                            messageObj.interval = messageObj.interval.coerceAtLeast(10L)
                            sendMessage(messageObj, messageObj.interval)
                        }
                    }

                    is MediaEvent.COMPLETION -> {
                        progress.current = duration
                        progress.duration = duration
                        event = progress
                        progress.interval = progress.interval.coerceAtLeast(10L)
                        if (state is MediaState.PLAYING) {
                            state = MediaState.PAUSING(isCompleted = true)
                        }
                        event = messageObj
                    }

                    else -> {
                        event = messageObj
                    }
                }
            }
        }
        true
    }

    private val progress: MediaEvent.PROGRESS_SYNC = MediaEvent.PROGRESS_SYNC()

    var state: MediaState = MediaState.IDLE
        @MainThread
        private set(value) {
            if (field !== value) {
                Log.d(TAG, "setState($value)")
                field = value
                callback?.onMediaState(field)
            }
        }

    var event: MediaEvent? = null
        @MainThread
        private set(value) {
            Log.d(TAG, "setEvent($value)")
            field = value
            if (value != null) {
                callback?.onMediaEvent(value)
            }
        }

    val current: Long; get() = if (state.isPrepared()) player.currentPosition.toLong() else 0L

    val duration: Long; get() = if (state.isPrepared()) player.duration.toLong() else 0L

    var callback: MediaPlayerCallback? = null
        set(value) {
            if (field != value) {
                field?.let { oldField ->
                    oldField.onDetached(this)
                    removeMessage(progress)
                }
                field = value
                field?.let { newField ->
                    newField.onAttached(this)
                    if (!hasMessage(progress)) {
                        sendMessage(progress)
                    }
                }
            }
        }

    var source: String? = null
        private set

    var surface: Surface? = null
        set(value) {
            if (field !== value) {
                field = value
                player.setSurface(field)
            }
        }

    override fun prepare(source: String, @MainThread callback: Runnable?): Boolean {
        if (source.isEmpty()) return false
        if (state.isPrepared() && this.source == source) return false
        sendMessage(MediaState.PREPARING(source, callback))
        return true
    }

    override fun play(@MainThread callback: Runnable?): Boolean {
        if (!state.isPlayable()) return false
        sendMessage(MediaState.PLAYING(callback))
        return true
    }

    override fun pause(@MainThread callback: Runnable?): Boolean {
        if (state !is MediaState.PLAYING) return false
        sendMessage(MediaState.PAUSING(callback = callback))
        return true
    }

    override fun seek(toMs: Long, @MainThread callback: Runnable?): Boolean {
        if (!state.isPlayable()) return false
        sendMessage(MediaState.SEEKING(toMs, callback))
        return true
    }

    override fun reset() {
        if (!state.isAlive()) return
        sendMessage(MediaState.IDLE)
    }

    override fun release() {
        if (!state.isAlive()) return
        sendMessage(MediaState.RELEASING)
    }

    private fun sendMessage(obj: Any?, delay: Long = 0L) {
        handler.sendMessageDelayed(handler.obtainMessage(this.hashCode(), obj), delay.coerceAtLeast(0L))
    }

    private fun removeMessage(obj: Any?) {
        handler.removeMessages(this.hashCode(), obj)
    }

    private fun hasMessage(obj: Any?): Boolean = handler.hasMessages(this.hashCode(), obj)

    private inner class MediaPlayerListeners :
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener {
        override fun onPrepared(mp: MediaPlayer) {
            Log.d(TAG, "onPrepared")
            val state = this@VideoPlayer.state as? MediaState.PREPARING ?: return
            this@VideoPlayer.sendMessage(MediaState.PREPARED(state.source, state.callback))
        }
        override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
            Log.d(TAG, "onBufferingUpdate($percent)")
            this@VideoPlayer.sendMessage(MediaEvent.BUFFER_UPDATE(percent / 100F))
        }
        override fun onCompletion(mp: MediaPlayer) {
            Log.d(TAG, "onCompletion")
            this@VideoPlayer.sendMessage(MediaEvent.COMPLETION)
        }
        override fun onSeekComplete(mp: MediaPlayer) {
            Log.d(TAG, "onSeekComplete")
            val state = this@VideoPlayer.state as? MediaState.SEEKING ?: return
            this@VideoPlayer.sendMessage(MediaState.SOUGHT(state.toMs, state.callback))
        }
        override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {
            Log.d(TAG, "onVideoSizeChanged($width, $height)")
            this@VideoPlayer.sendMessage(MediaEvent.SIZE_CHANGED(width, height))
        }
        override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            Log.d(TAG, "onInfo($what, $extra)")
            this@VideoPlayer.sendMessage(MediaEvent.PLAYER_INFO(what, extra))
            return true
        }
        override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            Log.d(TAG, "onError($what, $extra)")
            this@VideoPlayer.sendMessage(MediaEvent.PLAYER_ERROR(what, extra))
            return true
        }
    }
}