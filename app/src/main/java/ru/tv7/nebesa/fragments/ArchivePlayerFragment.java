package ru.tv7.nebesa.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.AMPERSAND;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_LANGUAGE;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_VIDEO_URL;
import static ru.tv7.nebesa.helpers.Constants.AUDIO_INDEX_ENABLE_LANG;
import static ru.tv7.nebesa.helpers.Constants.AUDIO_INDEX_PARAM;
import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.COLON_WITH_SPACE;
import static ru.tv7.nebesa.helpers.Constants.DASH;
import static ru.tv7.nebesa.helpers.Constants.EPISODE_NUMBER;
import static ru.tv7.nebesa.helpers.Constants.EQUAL;
import static ru.tv7.nebesa.helpers.Constants.ID;
import static ru.tv7.nebesa.helpers.Constants.IS_SUBTITLE;
import static ru.tv7.nebesa.helpers.Constants.LANG_ID;
import static ru.tv7.nebesa.helpers.Constants.LINK_PATH;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.ONE_STR;
import static ru.tv7.nebesa.helpers.Constants.PATH;
import static ru.tv7.nebesa.helpers.Constants.PAUSE_START_ICON_ANIMATION_DURATION;
import static ru.tv7.nebesa.helpers.Constants.PAUSE_START_ICON_ANIMATION_END;
import static ru.tv7.nebesa.helpers.Constants.PAUSE_START_ICON_ANIMATION_START;
import static ru.tv7.nebesa.helpers.Constants.PAUSE_START_ICON_ANIMATION_START_OFFSET;
import static ru.tv7.nebesa.helpers.Constants.PERCENT;
import static ru.tv7.nebesa.helpers.Constants.PNID_PARAM;
import static ru.tv7.nebesa.helpers.Constants.POSITION;
import static ru.tv7.nebesa.helpers.Constants.QUESTION_MARK;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;
import static ru.tv7.nebesa.helpers.Constants.SLASH_WITH_SPACES;
import static ru.tv7.nebesa.helpers.Constants.SUBTITLES_URL;
import static ru.tv7.nebesa.helpers.Constants.TRANSLATION_LANG_ID;
import static ru.tv7.nebesa.helpers.Constants.TV_BRAND;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_CONTROLS_TIMEOUT;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_POSITION_TIMEOUT;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_SEEK_STEP_SECONDS;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.VOD_PARAM;
import static ru.tv7.nebesa.helpers.Constants.ZERO_STR;
import static ru.tv7.nebesa.helpers.Constants._LINK_PATH_;

/**
 * Archive player fragment. Uses the ExoPlayer to show HLS stream.
 * Home of ExoPlayer: https://github.com/google/ExoPlayer
 */
public class ArchivePlayerFragment extends Fragment implements Player.EventListener, ArchiveDataLoadedListener {

    private View root = null;

    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private JSONObject selectedProgram = null;

    private SimpleExoPlayer exoPlayer = null;
    private boolean controlsVisible = false;
    private boolean paused = false;
    private boolean seeking = false;

    private long videoDuration = 0;
    private long videoPosition = 0;

    private RelativeLayout controls = null;
    private ProgressBar videoProgressBar = null;
    private TextView positionAndDurationTimes = null;

    private Timer videoProgressTimer = null;
    private Timer videoControlsTimer = null;

    /**
     * Default constructor.
     */
    public ArchivePlayerFragment() { }

    /**
     * Creates and return new instance of this fragment.
     * @return
     */
    public static ArchivePlayerFragment newInstance() {
        return new ArchivePlayerFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
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
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreateView(): Called.");
            }

            root = inflater.inflate(R.layout.fragment_archive_player, container, false);

            selectedProgram = sharedCacheViewModel.getSelectedProgram();
            if (selectedProgram == null) {
                throw new Exception("Archive player page. Not selected program passed to this fragment!");
            }

            String programId = Utils.getValue(selectedProgram, ID);
            if (programId == null) {
                throw new Exception("Archive player page. Not selected program id passed to this fragment!");
            }

            controls = root.findViewById(R.id.controls);
            videoProgressBar = root.findViewById(R.id.videoProgressBar);
            positionAndDurationTimes = root.findViewById(R.id.positionAndDurationTimes);

            Resources resources = getResources();

            TextView episode = root.findViewById(R.id.episode);
            if (episode != null) {
                String episodeNbr = Utils.getValue(selectedProgram, EPISODE_NUMBER);
                if (episodeNbr != null) {
                    String text = resources.getString(R.string.episode) + COLON_WITH_SPACE + episodeNbr;
                    episode.setText(text);
                }
            }

            TextView caption = root.findViewById(R.id.caption);
            if (caption != null) {
                String captionText = Utils.getValue(selectedProgram, CAPTION);
                if (captionText != null && captionText.length() > 0) {
                    caption.setText(captionText);
                }
                else {
                    caption.setVisibility(View.GONE);
                }
            }

            TextView seriesAndName = root.findViewById(R.id.seriesAndName);
            if (seriesAndName != null) {
                String name = Utils.getValue(selectedProgram, SERIES_AND_NAME);
                if (name == null) {
                    throw new Exception("Archive player page. Not selected program serias and name passed to this fragment!");
                }

                seriesAndName.setText(name);
            }

            archiveViewModel.getTranslation(programId, this);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return root;
    }

    /**
     * Starts video on exoplayer.
     * @param subtitlesUrl - subtitles URL
     * @param langId
     */
    public void startVideo(String subtitlesUrl, String langId) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Called.");
            }

            String videoUrl = getVideoUrl();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Parameters. URL: " + videoUrl + " - subtitles path: " + (subtitlesUrl != null ? subtitlesUrl : "-"));
            }

            PlayerView playerView = root.findViewById(R.id.exoPlayer);
            Context context = getContext();

            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, getString(R.string.app_name)));
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(videoUrl));
            exoPlayer = new SimpleExoPlayer.Builder(context).build();
            playerView.setPlayer(exoPlayer);

            boolean hasSubtitles = subtitlesUrl != null;

            MergingMediaSource mergedMediaSource = null;
            if (hasSubtitles) {
                Format subtitleFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, Format.NO_VALUE, langId);

                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitlesUrl), subtitleFormat, C.TIME_UNSET);
                mergedMediaSource = new MergingMediaSource(hlsMediaSource, subtitleSource);

                SubtitleView subtitleView = playerView.getSubtitleView();
                if (subtitleView != null) {
                    CaptionStyleCompat captionStyleCompat = new CaptionStyleCompat(Color.WHITE, Color.BLACK,
                            Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.TRANSPARENT, null);
                    subtitleView.setStyle(captionStyleCompat);
                    subtitleView.setAlpha(0.8f);
                }
            }

            exoPlayer.addListener(this);
            exoPlayer.prepare(hasSubtitles ? mergedMediaSource : hlsMediaSource);
            exoPlayer.setPlayWhenReady(true);

            JSONObject videoStatus = this.getVideoStatus();
            if (videoStatus != null && videoStatus.getInt(PERCENT) < 100) {
                // Start video from position
                videoPosition = videoStatus.getInt(POSITION);
                this.seekTo();
            }

            this.hideProgressBar();
            this.addVideoProgressTimer();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.startVideo(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Handles video end event.
     * @param playWhenReady
     * @param state
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        if (state == ExoPlayer.STATE_ENDED) {
            this.toPreviousPage();
        }
    }

    /**
     * Home button pressed => pause.
     */
    public void onHomeButtonPressed() {
        if (!controlsVisible) {
            this.showControls();
        }

        this.cancelVideoControlsTimer();

        if (seeking) {
            this.seekTo();
        }

        if (!paused) {
            this.pause();
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
                Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (exoPlayer == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (controlsVisible) {
                    this.cancelVideoControlsTimer();

                    if (seeking) {
                        this.seekTo();
                        this.play();
                    }
                    else {
                        if (paused) {
                            this.play();
                        }
                        else {
                            this.pause();
                        }
                    }
                }
                else {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (controlsVisible) {
                    this.cancelVideoControlsTimer();

                    seeking = true;
                    this.pause();

                    if (videoPosition > 0) {
                        videoPosition -= VIDEO_SEEK_STEP_SECONDS;
                    }

                    if (videoPosition < 0) {
                        videoPosition = 0;
                    }

                    this.updateControls(videoPosition);
                }
                else {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (controlsVisible) {
                    this.cancelVideoControlsTimer();

                    seeking = true;
                    this.pause();

                    if (videoPosition < videoDuration) {
                        videoPosition += VIDEO_SEEK_STEP_SECONDS;
                    }

                    if (videoPosition > videoDuration) {
                        videoPosition = videoDuration;
                    }

                    this.updateControls(videoPosition);
                }
                else {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (!controlsVisible) {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (!controlsVisible) {
                    this.showControls();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PAUSE: keyCode: " + keyCode);
                }

                if (!paused) {
                    this.showControls();
                    this.cancelVideoControlsTimer();

                    this.pause();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PLAY: keyCode: " + keyCode);
                }

                if (paused) {
                    if (seeking) {
                        this.seekTo();
                    }

                    this.play();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_MEDIA_PLAY_PAUSE: keyCode: " + keyCode);
                }

                if (paused) {
                    if (seeking) {
                        this.seekTo();
                    }

                    this.play();
                }
                else {
                    this.showControls();
                    this.cancelVideoControlsTimer();

                    this.pause();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (controlsVisible) {
                    if (seeking) {
                        this.seekTo();
                    }
                    else {
                        this.cancelVideoControlsTimer();
                    }

                    if (paused) {
                        this.play();
                    }
                    else {
                        this.hideControls();
                    }
                }
                else {
                    this.toPreviousPage();
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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
                Log.d(LOG_TAG, "ArchivePlayerFragment.onPlayerError(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Data loaded callback for subtitles of video.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        String subtitlesUrl = null;
        String langId = null;

        try {
            // Check is subtitles available
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    if (obj.has(LANG_ID) && obj.has(PATH) && obj.has(IS_SUBTITLE)
                            && obj.getString(IS_SUBTITLE).equals(ONE_STR)
                            && obj.getString(LANG_ID).equals(TRANSLATION_LANG_ID)) {
                        subtitlesUrl = obj.getString(PATH);
                        langId = obj.getString(LANG_ID);
                        break;
                    }
                }

                if (subtitlesUrl != null) {
                    subtitlesUrl = SUBTITLES_URL + subtitlesUrl;
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }

        this.startVideo(subtitlesUrl, langId);
    }

    /**
     * Data loaded callback error method.
     * @param message
     * @param type
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - Error message: " + message);
        }

        Utils.toErrorPage(getActivity());
    }

    /**
     * Archive data load no network error response.
     * @param type
     */
    @Override
    public void onNoNetwork(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - ***No network connection!***");
        }

        Utils.toErrorPage(getActivity());
    }

    /**
     * Returns video status object from shared preferences.
     * @return JSONObject
     */
    private JSONObject getVideoStatus() {
        JSONObject statusItem = null;
        try {
            String value = Utils.getValue(selectedProgram, ID);
            if (value != null) {
                statusItem = Utils.getVideoStatus(Utils.stringToInt(value), getContext());
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.getVideoStatus(): Exception: " + e);
            }
        }

        return statusItem;
    }

    /**
     * Saves video watch status to shared preferences.
     */
    private void saveVideoStatus() {
        try {
            JSONArray jsonArray = Utils.getSavedPrefs(VIDEO_STATUSES_SP_TAG, VIDEO_STATUSES_SP_DEFAULT, getContext());
            if (jsonArray != null) {
                int programId = 0;
                String value = Utils.getValue(selectedProgram, ID);
                if (value != null) {
                    programId = Utils.stringToInt(value);
                }

                // Remove existing value
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    value = Utils.getValue(obj, ID);
                    if (value == null) {
                        continue;
                    }

                    int id = Utils.stringToInt(value);
                    if (id == programId) {
                        jsonArray.remove(i);
                        break;
                    }
                }

                long position = exoPlayer.getContentPosition() / 1000;
                long duration = exoPlayer.getDuration() / 1000;

                int percent = 0;
                if (duration - position <= 60) {
                    percent = 100;
                }
                else {
                    percent = Math.round((float)position / (float)duration * (float)100);
                    if (percent < 0) {
                        percent = 0;
                    }
                    if (percent > 100) {
                        percent = 100;
                    }
                }

                // Add new value
                JSONObject obj = new JSONObject();
                obj.put(ID, programId);
                obj.put(POSITION, position);
                obj.put(PERCENT, percent);

                jsonArray.put(obj);

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchivePlayerFragment.saveVideoStatus(): Save status: " + obj.toString());
                }

                Utils.savePrefs(VIDEO_STATUSES_SP_TAG, getContext(), jsonArray);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.saveVideoStatus(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Release the resources and opens the previous page.
     */
    private synchronized void toPreviousPage() {
        this.saveVideoStatus();

        this.releasePlayer();
        this.cancelVideoProgressTimer();
        this.cancelVideoControlsTimer();

        String page = sharedCacheViewModel.getPageFromHistory();
        if (page != null) {
            Utils.toPage(page, getActivity(), true, false, null);
        }
    }

    /**
     * Creates video progress time.
     */
    private void addVideoProgressTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.videoProgressTimer() called.");
        }

        videoProgressTimer = new Timer();
        videoProgressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (controlsVisible && !paused) {
                    updateControlsInMainThread();
                }
            }
        }, VIDEO_POSITION_TIMEOUT, VIDEO_POSITION_TIMEOUT);
    }

    /**
     * Cancels video progress timer.
     */
    private void cancelVideoProgressTimer() {
        if (videoProgressTimer != null) {
            videoProgressTimer.cancel();
            videoProgressTimer = null;
        }
    }

    /**
     * Runs on main thread.
     */
    private void updateControlsInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    long pos = exoPlayer.getContentPosition() / 1000;
                    updateControls(pos);
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
     * Creates video controls timer.
     */
    private void addVideoControlsTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.addVideoControlsTimer() called.");
        }

        this.cancelVideoControlsTimer();

        videoControlsTimer = new Timer();
        videoControlsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (controlsVisible) {
                    hideControlsInMainThread();
                }
            }
        }, VIDEO_CONTROLS_TIMEOUT, VIDEO_CONTROLS_TIMEOUT);
    }

    /**
     * Cancels video controls timer.
     */
    private void cancelVideoControlsTimer() {
        if (videoControlsTimer != null) {
            videoControlsTimer.cancel();
            videoControlsTimer = null;
        }
    }

    /**
     * Runs on main thread.
     */
    private void hideControlsInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hideControls();
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
     * Updates controls (time and progress bar).
     * @param pos
     */
    private void updateControls(long pos) {
        if (exoPlayer != null) {
            if (videoDuration == 0) {
                videoDuration = exoPlayer.getDuration() / 1000;
            }

            videoPosition = pos;

            if (videoPosition > videoDuration) {
                videoPosition = videoDuration;
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video position: " + pos + " second(s)");
            }

            StringBuilder times = new StringBuilder(Utils.getTimeStampByDurationMs(String.valueOf(pos * 1000)));
            times.append(SLASH_WITH_SPACES);
            times.append(Utils.getTimeStampByDurationMs(String.valueOf(videoDuration * 1000)));

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video position/duration string: " + times.toString());
            }

            pos = Math.round((((float) pos * 100.0f) / (float) videoDuration));
            if (pos < 0) {
                pos = 0;
            }

            if (pos > 100) {
                pos = 100;
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchivePlayerFragment.updateControls(): Video progress bar position: " + pos);
            }

            if (videoProgressBar != null) {
                videoProgressBar.setProgress((int) pos);
            }

            if (positionAndDurationTimes != null) {
                positionAndDurationTimes.setText(times.toString());
            }
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

    /**
     Archive video URL:
     - https://vod.tv7.fi:443/vod/_definst_/mp4:" + link_path + "/playlist.m3u8
     - query string:
     - pnid = program id
     - vod = app id (samsung, lg, android) + channel id (FI1, ET1, SV1, RU1), for example lg-FI1 or samsung-ET1
     - audioindex is 0, nebesa channel videos audioindex is always 1
     **/
    private String getVideoUrl() throws Exception {
        if (!selectedProgram.has(ID)) {
            throw new Exception("Program id value missing in archive player!");
        }

        StringBuilder url = new StringBuilder();
        url.append(ARCHIVE_VIDEO_URL.replace(_LINK_PATH_, this.getPath()));

        url.append(QUESTION_MARK);
        url.append(PNID_PARAM);
        url.append(EQUAL);
        url.append(selectedProgram.getString(ID));

        url.append(AMPERSAND);
        url.append(VOD_PARAM);
        url.append(EQUAL);
        url.append(TV_BRAND);
        url.append(DASH);
        url.append(ARCHIVE_LANGUAGE);

        url.append(AMPERSAND);
        url.append(AUDIO_INDEX_PARAM);
        url.append(EQUAL);
        url.append(ARCHIVE_LANGUAGE.equals(AUDIO_INDEX_ENABLE_LANG) ? ONE_STR : ZERO_STR);

        String urlStr = url.toString();

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchivePlayerFragment.getVideoUrl(): URL: " + urlStr);
        }

        return urlStr;
    }

    /**
     * Returns video path.
     * @return
     * @throws Exception
     */
    private String getPath() throws Exception {
        String path = null;

        if (selectedProgram.has(LINK_PATH)) {
            path = selectedProgram.getString(LINK_PATH);
        }
        else if (selectedProgram.has(PATH)) {
            path = selectedProgram.getString(PATH);
        }

        if (path == null) {
            throw new Exception("Video link missing in archive player!");
        }

        return path;
    }

    /**
     * Hides progress bar.
     */
    private void hideProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Shows video controls.
     */
    private void showControls() {
        if (exoPlayer != null) {
            long pos = exoPlayer.getContentPosition() / 1000;
            this.updateControls(pos);

            controlsVisible = true;
            controls.setVisibility(View.VISIBLE);

            this.addVideoControlsTimer();
        }
    }

    /**
     * Hides video controls.
     */
    private void hideControls() {
        controlsVisible = false;
        seeking = false;
        paused = false;

        controls.setVisibility(View.GONE);

        this.cancelVideoControlsTimer();
    }

    /**
     * Starts video.
     */
    private void play() {
        this.hideControls();
        exoPlayer.setPlayWhenReady(true);

        this.animatePauseStartIcon(false);
    }

    /**
     * Pause video.
     */
    private void pause() {
        if (!paused) {
            paused = true;
            exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());

            this.animatePauseStartIcon(true);
        }
    }

    /**
     * Seeks video to position.
     */
    private void seekTo() {
        exoPlayer.seekTo(videoPosition * 1000);
    }

    /**
     * Show, hides and animates pause/start icon.
     * @param pause
     */
    private void animatePauseStartIcon(boolean pause) {
        ImageView pauseStartIcon = root.findViewById(R.id.pauseStartIcon);
        if (pauseStartIcon != null) {
            pauseStartIcon.setImageResource(pause ? R.drawable.pause : R.drawable.start);
            pauseStartIcon.setVisibility(View.VISIBLE);

            Animation animation = new AlphaAnimation(PAUSE_START_ICON_ANIMATION_START, PAUSE_START_ICON_ANIMATION_END);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setStartOffset(PAUSE_START_ICON_ANIMATION_START_OFFSET);
            animation.setDuration(PAUSE_START_ICON_ANIMATION_DURATION);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    pauseStartIcon.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            pauseStartIcon.startAnimation(animation);
        }
    }
}
