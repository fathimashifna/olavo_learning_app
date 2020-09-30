package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innoxgen.vimeoplayer2.UniversalMediaController;
import com.innoxgen.vimeoplayer2.UniversalVideoView;
import com.innoxgen.vimeoplayer2.vimeoextractor.OnVimeoExtractionListener;
import com.innoxgen.vimeoplayer2.vimeoextractor.VimeoExtractor;
import com.innoxgen.vimeoplayer2.vimeoextractor.VimeoVideo;
import com.innoxgen.olavo.R;

public class  VimeVideoActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback {

    private static final String TAG = "Vimovideo";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    String VIMEO_VIDEO_URL="https://vimeo.com/";

    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;


    ImageView image_play;
    public int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;
    TextView tv_title;
    String title, vimeo_url;
    private static final String KEY_VIDEO_URI = "video_uri";
    RelativeLayout mVideoLayout;
    boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vime_video);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = findViewById(R.id.videoView);
        mMediaController = findViewById(R.id.media_controller);
        image_play = findViewById(R.id.image_play);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setVideoViewCallback(this);

        tv_title = findViewById(R.id.tv_header_comment);

        if (getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("ClassName");
            vimeo_url = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        tv_title.setText(title);


        if (isNetworkConnectionAvailable())
        {
            String new_url=VIMEO_VIDEO_URL+vimeo_url;
            //Log.e("URL Video", ""+new_url);
            setVideoAreaSize();


            image_play.setVisibility(View.GONE);
            mVideoView.start();

            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion ");
                }
            });
             VimeoExtractor.getInstance().fetchVideoWithURL(new_url, null, new OnVimeoExtractionListener() {
                @Override
                public void onSuccess(final VimeoVideo video) {
                    String hdStream = null;
                    for (String key : video.getStreams().keySet()) {
                        hdStream = key;
                    }
                    final String hdStreamuRL = video.getStreams().get(hdStream);
                    if (hdStream != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Start the MediaController
                                mVideoView.setMediaController(mMediaController);
                                // Get the URL from String VideoURL
                                Uri video = Uri.parse(hdStreamuRL);

                                mVideoView.setVideoURI(video);
                                if (mSeekPosition > 0) {
                                    mVideoView.seekTo(mSeekPosition);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    //Error handling here
                    Log.e("Video",""+throwable);
                }
            });

        }
        else
        {
            startActivity(new Intent(VimeVideoActivity.this, NoNetworkActivity.class));
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
        }
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                int height = mVideoLayout.getWidth();

                View rltvlayout = findViewById(R.id.video_layout);
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams)rltvlayout.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                rltvlayout.setLayoutParams(layoutParams);

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
        Log.e(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
       // if (isFullscreen) {
        if (fullscreen) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            mSeekPosition = mVideoView.getCurrentPosition();
            //Log.e("seek pos2",""+mSeekPosition);
            if (mSeekPosition > 0) {
                mVideoView.seekTo(mSeekPosition);
            }
            //Log.e("seek pos3",""+mSeekPosition);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoLayout.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
            mVideoLayout.setLayoutParams(params);
            fullscreen=false;


        } else {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mSeekPosition = mVideoView.getCurrentPosition();
            if(getSupportActionBar() != null){
                getSupportActionBar().hide();
            }

            if (mSeekPosition > 0) {
                mVideoView.seekTo(mSeekPosition);
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoLayout.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            mVideoLayout.setLayoutParams(params);

            fullscreen = true;
        }


    }



    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
    }


    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
        Log.d(TAG, "onPause UniversalVideoView callback");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
            mSeekPosition = mVideoView.getCurrentPosition();
        } else {
            super.onBackPressed();
        }
    }


}