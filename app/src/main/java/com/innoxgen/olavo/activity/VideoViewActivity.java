package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.innoxgen.olavo.R;

public class VideoViewActivity extends AppCompatActivity implements Player.EventListener {

    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";

    private PlayerView playerView;
    TextView tv_title;
    String vedioURL, title;
    private static final String TAG = "ExoPlayerActivity";
    private static final String KEY_VIDEO_URI = "video_uri";
    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;
    ProgressBar progress_bar;

    boolean fullscreen = false;
    ImageView fullscreenButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_view);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        playerView = findViewById(R.id.player_view);
        progress_bar = findViewById(R.id.progress_bar);
        tv_title = findViewById(R.id.tv_header_comment);

        fullscreenButton = playerView.findViewById(R.id.bt_fullscreen);

        progress_bar.setVisibility(View.VISIBLE);

        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("ClassName");
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        tv_title.setText(title);


        if (isNetworkConnectionAvailable()) {
            setUp(progress_bar);
            fullscreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fullscreen) {
                        fullscreenButton.setBackgroundResource(R.drawable.ic_baseline_fullscreen_24);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().show();
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                        playerView.setLayoutParams(params);
                        // playerView.setResizeMode(AspectRatioFrameLayout.DRAG_FLAG_OPAQUE);
                        fullscreen = false;
                    } else {
                        fullscreenButton.setBackgroundResource(R.drawable.ic_full_exit);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().hide();
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                        params.width = params.MATCH_PARENT;
                        params.height = params.MATCH_PARENT;
                        playerView.setLayoutParams(params);
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                        fullscreen = true;
                    }
                }
            });
        } else {
            startActivity(new Intent(VideoViewActivity.this, NoNetworkActivity.class));
            finish();
        }
    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    private void setUp(ProgressBar progress_bar) {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri), progress_bar);

    }

    private void initializePlayer() {
        if (player == null) {


            DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getApplicationContext()), trackSelector);
            playerView.setPlayer(player);
        }
    }

    private void buildMediaSource(Uri mUri, ProgressBar progress_bar) {

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        progress_bar.setVisibility(View.VISIBLE);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                !playWhenReady) {

            playerView.setKeepScreenOn(false);
        } else { // STATE_IDLE, STATE_ENDED
            // This prevents the screen from getting dim/lock
            playerView.setKeepScreenOn(true);
        }
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                progress_bar.setVisibility(View.VISIBLE);
                //  spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                progress_bar.setVisibility(View.INVISIBLE);
                //  spinnerVideoDetails.setVisibility(View.GONE);
                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }
}