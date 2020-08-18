package ru.tv7.nebesatv7.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import ru.tv7.nebesatv7.helpers.EpgItem;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.model.SharedViewModel;
import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.R;

import static ru.tv7.nebesatv7.helpers.Constants.COLON;
import static ru.tv7.nebesatv7.helpers.Constants.DASH_WITH_SPACES;
import static ru.tv7.nebesatv7.helpers.Constants.DOT;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesatv7.helpers.Constants.SPACE;
import static ru.tv7.nebesatv7.helpers.Constants.TIMER_TIMEOUT;
import static ru.tv7.nebesatv7.helpers.Constants.TYPE_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.URL_PARAM;

/**
 * Video player fragment. Uses the ExoPlayer to show HLS stream.
 * Home of ExoPlayer: https://github.com/google/ExoPlayer
 */
public class VideoPlayerFragment extends Fragment implements Player.EventListener {

    private View root = null;
    private FragmentManager fragmentManager = null;
    private SharedViewModel viewModel = null;
    private Timer timer = null;
    private int guideIndex = 0;

    private String videoUrl = null;
    private String videoType = null;

    private SimpleExoPlayer exoPlayer = null;
    private boolean isPlaying = false;

    /**
     * Default constructor.
     */
    public VideoPlayerFragment() { }

    /**
     * Creates and return new instance of this fragment.
     * @return
     */
    public static VideoPlayerFragment newInstance() {
        return new VideoPlayerFragment();
    }

    /**
     * onCreateView lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onCreateView(): Called.");
            }

            viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
            root = inflater.inflate(R.layout.fragment_video_player, container, false);

            this.addCountdownTimer();
        }
        catch( Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return root;
    }

    /**
     * onStart lifecycle method. Starts stream.
     */
    @Override
    public void onStart() {
        try {
            super.onStart();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onStart(): Called.");
            }

            // get input parameters
            Bundle bundle = getArguments();
            if (bundle != null) {
                videoUrl = bundle.getString(URL_PARAM);
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onStart(): Parameters. URL: " + videoUrl + " Type: " + videoType);
            }

            PlayerView playerView = root.findViewById(R.id.exoPlayer);
            Context context = getContext();

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, getString(R.string.app_name)));
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(videoUrl));
            exoPlayer = new SimpleExoPlayer.Builder(context).build();
            playerView.setPlayer(exoPlayer);

            exoPlayer.addListener(this);
            exoPlayer.prepare(hlsMediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onStart(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Handles keydown events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (this.isTopGuideBarVisible()) {
                    // hide guide top bar
                    this.hideGuideTopBar();
                }
                else {
                    // show top bar
                    guideIndex = 0;

                    this.setTopBarContent(viewModel.getEpgItemByIndex(guideIndex));
                    this.showGuideTopBar();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (guideIndex > 0 && isTopGuideBarVisible()) {
                    guideIndex--;
                    this.setTopBarContent(viewModel.getEpgItemByIndex(guideIndex));
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (viewModel.isListItemInIndex(guideIndex + 1) && isTopGuideBarVisible()) {
                    guideIndex++;
                    this.setTopBarContent(viewModel.getEpgItemByIndex(guideIndex));
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (this.isTopGuideBarVisible()) {
                    // hide guide top bar
                    this.hideGuideTopBar();
                }
                else {
                    // exit from video player fragment
                    this.releasePlayer();
                    this.backToMainView();
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return false;
    }

    /**
     * onDestroyView lifecycle method.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.releasePlayer();
        this.cancelTimer();
    }

    /**
     * onIsPlayingChanged overridden exoplayer method.
     * @param isPlaying
     */
    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "VideoPlayerFragment.onIsPlayingChanged(): Playing: " + isPlaying);
        }

        this.isPlaying = isPlaying;
    }

    /**
     * onPlayerError overridden exoplayer method.
     * @param error
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException e = error.getSourceException();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "VideoPlayerFragment.onPlayerError(): Exception: " + e);
            }

            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Sets top bar content.
     */
    private void setTopBarContent(EpgItem epgItem) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainFragment.setTopBarContent() called.");
        }

        int removedCount = viewModel.removePastProgramItems();
        if (removedCount > 0) {
            int ngi = guideIndex - removedCount;
            guideIndex = ngi >= 0 ? ngi : 0;
            epgItem = viewModel.getEpgItemByIndex(guideIndex);
        }

        if (epgItem != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.setTopBarContent(): EpgItem time: " +
                        epgItem.getLocalStartTime() + DASH_WITH_SPACES + epgItem.getLocalEndTime() + " Title: " + epgItem.getTitle());
            }

            // time, title and description texts
            TextView timeAndTitle = root.findViewById(R.id.timeAndTitle);
            TextView desc = root.findViewById(R.id.description);
            if (timeAndTitle != null && desc != null) {
                StringBuilder timeAndTitleStr = new StringBuilder(epgItem.getLocalStartTime() + DASH_WITH_SPACES + epgItem.getLocalEndTime() + SPACE + epgItem.getTitle());
                if (!epgItem.getStartDateToday()) {
                    timeAndTitleStr.insert(0, epgItem.getLocalStartDate() + PIPE_WITH_SPACES);
                }

                timeAndTitle.setText(timeAndTitleStr.toString());
                desc.setText(epgItem.getDesc());
            }

            // progressbar and coming on channel text
            ProgressBar ongoingProgress = root.findViewById(R.id.ongoingProgress);
            TextView comingOnChannelText = root.findViewById(R.id.comingOnChannelText);
            if (ongoingProgress != null && comingOnChannelText != null) {
                Integer progressValue = epgItem.getOngoingProgress();

                if (guideIndex == 0 && progressValue != null) {
                    // show progress bar
                    ongoingProgress.setProgress(progressValue);
                    ongoingProgress.setVisibility(View.VISIBLE);
                    comingOnChannelText.setVisibility(View.GONE);
                }
                else {
                    // show coming on channel text
                    ongoingProgress.setVisibility(View.GONE);
                    comingOnChannelText.setVisibility(View.VISIBLE);
                }
            }

            // current date time
            TextView timeNow = root.findViewById(R.id.timeNow);
            if (timeNow != null) {
                Calendar today = GregorianCalendar.getInstance(TimeZone.getDefault());
                today.setTime(new Date());

                String dateTimeStr = today.get(Calendar.DATE) + DOT + (today.get(Calendar.MONTH) + 1) + DOT + today.get(Calendar.YEAR) +
                        SPACE + SPACE + Utils.prependZero(today.get(Calendar.HOUR_OF_DAY)) + COLON + Utils.prependZero(today.get(Calendar.MINUTE));

                timeNow.setText(dateTimeStr);
            }
        }
    }

    private boolean isTopGuideBarVisible() {
        boolean visible = true;
        RelativeLayout videoTopBar = root.findViewById(R.id.videoTopBar);
        if (videoTopBar != null) {
            visible = videoTopBar.getVisibility() == View.VISIBLE;
        }
        return visible;
    }

    /**
     * Shows guide top bar.
     */
    private void showGuideTopBar() {
        RelativeLayout videoTopBar = root.findViewById(R.id.videoTopBar);
        if (videoTopBar != null) {
            videoTopBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides guide top bar.
     */
    private void hideGuideTopBar() {
        RelativeLayout videoTopBar = root.findViewById(R.id.videoTopBar);
        if (videoTopBar != null) {
            videoTopBar.setVisibility(View.GONE);
        }
    }

    /**
     * Starts timer. Used to update top bar at specific intervals.
     */
    private void addCountdownTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainFragment.addCountdownTimer() called.");
        }

        this.cancelTimer();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "Timer.run() called.");
                }

                if (isTopGuideBarVisible()) {
                    updateUiInMainThread();
                }
            }
        }, TIMER_TIMEOUT, TIMER_TIMEOUT);
    }

    /**
     * Stops timer.
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Updates UI on main thread. Called from timer.
     */
    private void updateUiInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    setTopBarContent(viewModel.getEpgItemByIndex(guideIndex));
                }
                catch(Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "Runnable.run(): Exception: " + e);
                    }
                }
            }
        });
    }

    /**
     * Finds main fragment from fragment manager and replaces video fragment with main fragment.
     */
    private void backToMainView() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "VideoPlayerFragment.backToMainView() called.");
        }

        this.checkFragmentManager();

        Fragment mainFragment = fragmentManager.findFragmentByTag(MAIN_FRAGMENT);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }

        // Add parameters
        Bundle bundle = new Bundle();
        bundle.putString(URL_PARAM, videoUrl);
        bundle.putString(TYPE_PARAM, videoUrl);
        mainFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, mainFragment, MAIN_FRAGMENT).addToBackStack(MAIN_FRAGMENT).commit();
    }

    /**
     * Create a fragment manager if not already created.
     */
    private void checkFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
    }

    /**
     * Releases exo player resources.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}