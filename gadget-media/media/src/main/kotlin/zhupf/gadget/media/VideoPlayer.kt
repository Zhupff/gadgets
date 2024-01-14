package zhupf.gadget.media

import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
import android.media.MediaPlayer
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnErrorListener
import android.media.MediaPlayer.OnInfoListener
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnSeekCompleteListener
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Surface
import zhupf.gadget.media.IMediaPlayer.Companion.CODE_NULL
import zhupf.gadget.media.IMediaPlayer.Companion.ERROR_CODE
import zhupf.gadget.media.IMediaPlayer.Companion.ERROR_MEDIA
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_BUFFERING
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_CODE
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_COMPLETION
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_MEDIA
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_PROGRESS_SYNC
import zhupf.gadget.media.IMediaPlayer.Companion.INFO_SIZE_CHANGED
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_CODE
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_PAUSING
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_PLAYING
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_PREPARED
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_PREPARING
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_RELEASED
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_RELEASING
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_SEEKING
import zhupf.gadget.media.IMediaPlayer.Companion.STATE_SOUGHT
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class VideoPlayer(
    tag: String,
) : IMediaPlayer,
    OnPreparedListener,
    OnBufferingUpdateListener,
    OnCompletionListener,
    OnErrorListener,
    OnInfoListener,
    OnSeekCompleteListener,
    OnVideoSizeChangedListener,
    IMediaPlayerCallback
{

    private val TAG: String = tag.ifEmpty { "VideoPlayer(${hashCode()})" }

    private val player: MediaPlayer = MediaPlayer().also { player ->
        player.setOnPreparedListener(this)
        player.setOnBufferingUpdateListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        player.setOnInfoListener(this)
        player.setOnSeekCompleteListener(this)
        player.setOnVideoSizeChangedListener(this)
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build()
        )
    }

    private val retriever: MediaMetadataRetriever = MediaMetadataRetriever()

    var mediaSource: MediaSource<*> = MediaSource(0, "").also {
        it.prepared = true
    }; private set

    var surface: Surface? = null
        private set(value) {
            if (field != value) {
                field = value
                player.setSurface(value)
            }
        }

    var state: MediaState = MediaState(TAG)

    private var playAfterSeek: Boolean = false

    private var progressSyncing: Boolean = false

    var callback: IMediaPlayerCallback? = null
        set(value) {
            if (field != value) {
                field?.let { oldValue ->
                    oldValue.onDetach(this)
                }
                field = value
                field?.let { newValue ->
                    newValue.onAttach(this)
                    if (!progressSyncing) {
                        handler.sendMessage(obtainMessage(INFO_CODE, INFO_PROGRESS_SYNC))
                    }
                }
            }
        }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            this@VideoPlayer.handleMessage(msg)
        }
    }

    val progress: Int; get() = player.currentPosition

    val duration: Int; get() = player.duration

    override fun prepare(mediaSource: MediaSource<*>): Boolean {
        TAG.i("prepare($mediaSource)")
        if (this.mediaSource.key == mediaSource.key) {
            TAG.d("new source key is equals to old source key, so return")
            return false
        }
        this.mediaSource = mediaSource
        handler.sendMessage(obtainMessage(STATE_CODE, STATE_PREPARING))
        return true
    }

    fun play(surface: Surface, seekTo: Int = -1): Boolean {
        TAG.i("play($surface, $seekTo)")
        this.surface = surface
        return play(seekTo)
    }

    override fun play(seekTo: Int): Boolean {
        TAG.i("seekTo($seekTo), state=$state")
        return when (state.current) {
            STATE_PREPARED,
            STATE_PAUSING -> {
                handler.sendMessage(obtainMessage(STATE_CODE, STATE_PLAYING, seekTo))
                true
            }
            else -> false
        }
    }

    override fun pause(): Boolean {
        TAG.i("pause(), state=$state")
        return when (state.current) {
            STATE_PLAYING -> {
                handler.sendMessage(obtainMessage(STATE_CODE, STATE_PAUSING))
                true
            }
            else -> false
        }
    }

    override fun seekTo(seekTo: Int): Boolean {
        TAG.i("seekTo($seekTo), state=$state")
        return when (state.current) {
            STATE_PREPARED,
            STATE_PLAYING,
            STATE_PAUSING -> {
                handler.sendMessage(handler.obtainMessage(STATE_CODE, STATE_SEEKING, seekTo))
            }
            else -> false
        }
    }

    override fun release() {
        TAG.i("release()")
        handler.sendMessage(obtainMessage(STATE_CODE, STATE_RELEASING))
        executor.shutdown()
        try {
            if (!executor.awaitTermination(3, TimeUnit.MINUTES)) {
                executor.shutdownNow()
            }
        } catch (e: Exception) {
            TAG.w("release, executor shutdown exception", e)
            executor.shutdownNow()
        }
        player.stop()
        player.release()
        handler.sendMessage(obtainMessage(STATE_CODE, STATE_RELEASED))
    }

    override fun onPrepared(mp: MediaPlayer?) {
        handler.sendMessage(obtainMessage(STATE_CODE, STATE_PREPARED))
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        onEvent(this, INFO_CODE, INFO_BUFFERING, percent)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        state.switch(if (player.isPlaying) STATE_PLAYING else STATE_PAUSING)
        onEvent(this, INFO_CODE, INFO_COMPLETION)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        onEvent(this, ERROR_CODE, ERROR_MEDIA, what, extra)
        return true
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        onEvent(this, INFO_CODE, INFO_MEDIA, what, extra)
        return true
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        handler.sendMessage(obtainMessage(STATE_CODE, STATE_SOUGHT))
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        onEvent(this, INFO_CODE, INFO_SIZE_CHANGED, width, height)
    }

    override fun onAttach(player: IMediaPlayer) {
        super.onAttach(player)
        callback?.onAttach(player)
    }

    override fun onDetach(player: IMediaPlayer) {
        super.onDetach(player)
        callback?.onDetach(player)
    }

    override fun onEvent(
        player: IMediaPlayer,
        code: Int,
        what: Int,
        arg1: Int,
        arg2: Int,
        obj: Any?
    ): Any? {
        TAG.d("onEvent($player, $code, $what, $arg1, $arg2, $obj)")
        return callback?.onEvent(player, code, what, arg1, arg2, obj)
    }

    private fun obtainMessage(what: Int, arg1: Int = CODE_NULL, arg2: Int = CODE_NULL, obj: Any? = null): Message = handler.obtainMessage(what, arg1, arg2, obj)

    private fun handleMessage(msg: Message) {
        TAG.d("handleMessage(Message(${msg.what}, ${msg.arg1}, ${msg.arg2}, ${msg.obj}))")
        when (msg.what) {
            STATE_CODE -> {
                when (msg.arg1) {
                    STATE_PREPARING -> {
                        state.switch(STATE_PREPARING)
                        onEvent(this, STATE_CODE, STATE_PREPARING)
                        player.reset()
                        player.setDataSource(mediaSource.source)
                        executor.execute {
                            retriever.setDataSource(mediaSource.source)
                            if (!mediaSource.prepared) {
                                retriever.extractMetadata(METADATA_KEY_DURATION).let {
                                    mediaSource.duration = it?.toIntOrNull() ?: 0
                                }
                                retriever.extractMetadata(METADATA_KEY_VIDEO_WIDTH).let {
                                    mediaSource.width = it?.toIntOrNull() ?: 0
                                }
                                retriever.extractMetadata(METADATA_KEY_VIDEO_HEIGHT).let {
                                    mediaSource.height = it?.toIntOrNull() ?: 0
                                }
                                mediaSource.prepared = true
                            }
                            player.prepareAsync()
                        }
                    }
                    STATE_PREPARED -> {
                        state.switch(STATE_PREPARED)
                        onEvent(this, STATE_CODE, STATE_PREPARED)
                    }
                    STATE_PLAYING -> {
                        val seekTo = msg.arg2
                        if (seekTo >= 0) {
                            handler.sendMessage(obtainMessage(STATE_CODE, STATE_SEEKING, seekTo, /*autoplay*/true))
                        } else {
                            state.switch(STATE_PLAYING)
                            onEvent(this, STATE_CODE, STATE_PLAYING)
                            player.start()
                            handler.sendMessage(obtainMessage(INFO_CODE, INFO_PROGRESS_SYNC))
                        }
                    }
                    STATE_PAUSING -> {
                        state.switch(STATE_PAUSING)
                        handler.sendMessage(obtainMessage(STATE_CODE, STATE_PAUSING))
                        player.pause()
                    }
                    STATE_SEEKING -> {
                        val seekTo = msg.arg2
                        playAfterSeek = msg.obj as? Boolean ?: false
                        state.switch(STATE_SEEKING)
                        onEvent(this, STATE_CODE, STATE_SEEKING, seekTo)
                        player.seekTo(seekTo)
                    }
                    STATE_SOUGHT -> {
                        state.switch(STATE_SOUGHT)
                        onEvent(this, STATE_CODE, STATE_SOUGHT, if (playAfterSeek) 1 else 0)
                        if (playAfterSeek) {
                            playAfterSeek = false
                            handler.sendMessage(obtainMessage(STATE_CODE, STATE_PLAYING))
                        }
                    }
                    STATE_RELEASING -> {
                        state.switch(STATE_RELEASING)
                        onEvent(this, STATE_CODE, STATE_RELEASING)
                    }
                    STATE_RELEASED -> {
                        state.switch(STATE_RELEASED)
                        onEvent(this, STATE_CODE, STATE_RELEASED)
                    }
                }
            }
            INFO_CODE -> {
                when (msg.arg1) {
                    INFO_PROGRESS_SYNC -> {
                        progressSyncing = false
                        if (state.current == STATE_PLAYING) {
                            val interval = onEvent(this, INFO_CODE, INFO_PROGRESS_SYNC, progress, duration) as? Int
                            if (interval != null && interval > 0 && !progressSyncing) {
                                progressSyncing = true
                                handler.sendMessageDelayed(obtainMessage(INFO_CODE, INFO_PROGRESS_SYNC), interval.toLong())
                            }
                        }
                    }
                }
            }
        }
    }
}