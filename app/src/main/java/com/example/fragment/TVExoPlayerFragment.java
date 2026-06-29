package com.example.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;

import com.example.livetvseries.R;
import com.example.util.Events;
import com.example.util.GlobalBus;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

@OptIn(markerClass = UnstableApi.class)
public class TVExoPlayerFragment extends Fragment {
    private ExoPlayer player;
    private ProgressBar progressBar;
    ImageView imgFull, imgSetting;
    public boolean isFullScr = false;
    Button btnTryAgain;
    String channelUrl, userAgentName;
    boolean isUserAgent;
    private static final String streamUrl = "streamUrl", userAgent = "userAgent", userAgentOnOff = "userAgentOnOff";

    public static TVExoPlayerFragment newInstance(String SId, String userAgentName, boolean isUserAgent) {
        TVExoPlayerFragment f = new TVExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, SId);
        args.putString(userAgent, userAgentName);
        args.putBoolean(userAgentOnOff, isUserAgent);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exo_player, container, false);
        GlobalBus.getBus().register(this);

        if (getArguments() != null) {
            channelUrl = getArguments().getString(streamUrl);
            userAgentName = getArguments().getString(userAgent);
            isUserAgent = getArguments().getBoolean(userAgentOnOff, false);
        }

        player = new ExoPlayer.Builder(requireActivity()).setSeekBackIncrementMs(10000).setSeekForwardIncrementMs(10000).build();
        PlayerView playerView = rootView.findViewById(R.id.exoPlayerView);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();

        progressBar = rootView.findViewById(R.id.progressBar);
        imgFull = playerView.findViewById(R.id.exo_fullscreen);
        imgSetting = playerView.findViewById(R.id.exo_settings);
        imgFull.setVisibility(View.VISIBLE);
        imgSetting.setVisibility(View.GONE);
        btnTryAgain = rootView.findViewById(R.id.btn_try_again);


        Uri uri = Uri.parse(channelUrl);

        MediaSource mediaSource = buildMediaSource(uri);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(@NotNull PlaybackException error) {
                player.stop();
                btnTryAgain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScr) {
                    isFullScr = false;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(false);
                    GlobalBus.getBus().post(fullScreen);
                } else {
                    isFullScr = true;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(true);
                    GlobalBus.getBus().post(fullScreen);
                }
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                retryLoad();
            }
        });

        return rootView;
    }

    public void retryLoad() {
        Uri uri = Uri.parse(channelUrl);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        switch (type) {
            case C.CONTENT_TYPE_SS:
                return new SsMediaSource.Factory(buildDataSourceFactory()).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_DASH:
                return new DashMediaSource.Factory(buildDataSourceFactory()).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_HLS:
                return new HlsMediaSource.Factory(buildDataSourceFactory()).createMediaSource(mediaItem);
            case C.CONTENT_TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(buildDataSourceFactory()).createMediaSource(mediaItem);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory() {
        return new DefaultHttpDataSource.Factory().setUserAgent(isUserAgent ? userAgentName : Util.getUserAgent(requireActivity(), "ExoPlayerDemo"));
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScr = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            imgFull.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            imgFull.setImageResource(R.drawable.ic_fullscreen);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
    }
}
