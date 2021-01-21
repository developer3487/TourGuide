package com.app.tourguide.ui.videoView


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.tourguide.R
import com.app.tourguide.base_classes.BaseFragment
import com.app.tourguide.utils.Constants
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_video_view.*


class VideoViewFragment : BaseFragment() {

    var dataSourceFac: DataSource.Factory? = null

    companion object {
        var nPlayer: ExoPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBundledArguments()
        ivCancel.setOnClickListener {
            if (nPlayer != null) {
                nPlayer!!.release()
                nPlayer = null
            }
            activity?.onBackPressed()
        }
    }


    private fun getBundledArguments() {
        if (arguments != null) {
            if (arguments?.getString(Constants.VIDEO_URL) != null)
                playVideo(arguments?.getString(Constants.VIDEO_URL)!!)
            else
                activity?.onBackPressed()
        }
    }

    private fun playVideo(videoUrl: String) {
        val player: ExoPlayer
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(activity, Util.getUserAgent(context, context!!.getString(com.app.tourguide.R.string.app_name)), defaultBandwidthMeter)
        dataSourceFac = dataSourceFactory


        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val contentMediaSource = buildMediaSource(Uri.parse(videoUrl))

        val mediaSources = arrayOfNulls<MediaSource>(2) //The Size must change depending on the Uris
        mediaSources[0] = contentMediaSource //uri
        val subtitleSource = SingleSampleMediaSource(Uri.parse("https://www.iandevlin.com/html5test/webvtt/upc-video-subtitles-en.vtt"),
                dataSourceFactory, Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, Format.NO_VALUE, "en", null),
                C.TIME_UNSET)

        // mediaSources[1] = subtitleSource

        //val mediaSource = MergingMediaSource(mediaSources[0], mediaSources[1])

        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        player.setPlayWhenReady(true)
        playerView.player = player

        player.prepare(contentMediaSource)
        nPlayer = player


        nPlayer?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                Log.d(TAG, "" + playbackParameters)
            }

            override fun onSeekProcessed() {
                Log.d(TAG, "")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                Log.d(TAG, "" + trackGroups)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(TAG, "" + error!!.message)
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(TAG, "loading [$isLoading]")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.d(TAG, "" + reason)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(TAG, "" + repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d(TAG, "" + shuffleModeEnabled)
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                Log.d(TAG, "" + timeline)
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d(TAG, "" + getStateString(playbackState))
                if (playbackState == Player.STATE_ENDED) {
                    if (nPlayer != null) {
                        nPlayer!!.release()
                        nPlayer = null
                        activity?.onBackPressed()
                    }
                }
            }

        })

    }

    private fun getStateString(state: Int): String {
        when (state) {
            Player.STATE_BUFFERING -> return "B"
            Player.STATE_ENDED -> return "E"
            Player.STATE_IDLE -> return "I"
            Player.STATE_READY -> return "R"
            else -> return "?"
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri)
        when (type) {
            /*C.TYPE_DASH:
               return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            C.TYPE_SS:
               return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);*/
            C.TYPE_HLS -> return HlsMediaSource.Factory(dataSourceFac).createMediaSource(uri)
            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(dataSourceFac).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type") as Throwable
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nPlayer != null) {
            nPlayer!!.release()
            nPlayer = null
        }
    }
}
