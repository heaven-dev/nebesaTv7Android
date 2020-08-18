package ru.tv7.taivastv7.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.tv7.taivastv7.BuildConfig;
import ru.tv7.taivastv7.R;
import ru.tv7.taivastv7.helpers.CallOrigin;
import ru.tv7.taivastv7.helpers.ComingProgramImageAndTextId;
import ru.tv7.taivastv7.helpers.EpgItem;
import ru.tv7.taivastv7.helpers.ProgramRowId;
import ru.tv7.taivastv7.helpers.Utils;
import ru.tv7.taivastv7.model.SharedViewModel;

import static ru.tv7.taivastv7.helpers.Constants.COMING_PROGRAM_IMAGE_AND_TEXT;
import static ru.tv7.taivastv7.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.taivastv7.helpers.Constants.GUIDE_ELEMENT_COUNT;
import static ru.tv7.taivastv7.helpers.Constants.PROGRAM_ROW;
import static ru.tv7.taivastv7.helpers.Constants.HTTP;
import static ru.tv7.taivastv7.helpers.Constants.HTTPS;
import static ru.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static ru.tv7.taivastv7.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.taivastv7.helpers.Constants.PROGRAM_VISIBLE_IMAGE_COUNT;
import static ru.tv7.taivastv7.helpers.Constants.SPACE;
import static ru.tv7.taivastv7.helpers.Constants.STREAM_URL;
import static ru.tv7.taivastv7.helpers.Constants.TIMER_TIMEOUT;
import static ru.tv7.taivastv7.helpers.Constants.URL_PARAM;
import static ru.tv7.taivastv7.helpers.Constants.VIDEO_PLAYER_FRAGMENT;

/**
 * Main fragment. Main view of application.
 */
public class MainFragment extends Fragment {

    private View root = null;
    private FragmentManager fragmentManager = null;
    private SharedViewModel viewModel = null;
    private String programStartUtc = null;
    private int guideIndex = 0;
    private Timer timer = null;

    /**
     * Default constructor.
     */
    public MainFragment() { }

    /**
     * Creates and return a new instance of main fragment class.
     * @return
     */
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainFragment.onCreate() called.");
        }

        viewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel.class);
    }

    /**
     * onCreateView() - Android lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.onCreateView() called.");
            }

            root = inflater.inflate(R.layout.fragment_main, container, false);

            LinearLayout fragmentMainRoot = root.findViewById(R.id.fragmentMainRoot);
            if (fragmentMainRoot != null) {
                Utils.fadePageAnimation(fragmentMainRoot);
            }

            programStartUtc = null;
            guideIndex = 0;

            this.createMainView(CallOrigin.NoTimer);
            this.addCountdownTimer();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
        return root;
    }

    /**
     * onDestroy() - Android lifecycle method.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.cancelTimer();
    }

    /**
     * Creates a main view.
     * @param callOrigin
     * @throws Exception
     */
    private void createMainView(CallOrigin callOrigin) throws Exception {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainFragment.createMainView() called.");
        }

        boolean updateContent = false;
        List<EpgItem> programs = viewModel.getOngoingAndComingPrograms(PROGRAM_VISIBLE_IMAGE_COUNT);

        for(int index = 0; index < programs.size(); index++) {
            EpgItem epgItem = programs.get(index);

            if (index == 0) {
                // update progress bar
                ProgressBar programProgress = root.findViewById(R.id.programProgress);
                if (programProgress != null) {
                    Integer progressValue = epgItem.getOngoingProgress();
                    if (progressValue != null) {
                        programProgress.setProgress(progressValue);
                    }
                }

                String utcTime = epgItem.getStartUtcStr();
                if (!utcTime.equals(programStartUtc)) {
                    // ongoing program changed from previous check - updated images and guide
                    updateContent = true;
                    programStartUtc = utcTime;
                }
            }

            if (updateContent) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.createMainView(): Update content.");
                }

                String imageUrl = epgItem.getIcon();

                // Change scheme from http to https
                if (imageUrl != null && !imageUrl.startsWith(HTTPS)) {
                    imageUrl = imageUrl.replace(HTTP, HTTPS);
                }

                if (index == 0) {
                    this.addOngoingProgramImage(imageUrl, epgItem);
                }
                else {
                    this.addComingProgramImages(index - 1, imageUrl, epgItem);
                }
            }
        }

        if (updateContent) {
            if (callOrigin == CallOrigin.Timer) {
                // remove past epg items from the program list
                viewModel.removePastProgramItems();
                guideIndex = viewModel.getOngoingProgramIndex();
            }

            this.updateGuide();
        }
    }

    /**
     * Adds ongoing program image and texts to view.
     * @param icon
     * @param epgItem
     */
    private void addOngoingProgramImage(String icon, EpgItem epgItem) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.addOngoingProgramImage() called.");
            }

            ImageView tvImage = root.findViewById(R.id.tvImage);
            if (tvImage != null) {
                Glide.with(this).asBitmap().load(icon).into(tvImage);
            }

            TextView tvText = root.findViewById(R.id.tvText);
            if (tvText != null) {
                String text = epgItem.getLocalStartTime() + SPACE + epgItem.getTitle();
                String desc = epgItem.getDesc();
                if (desc != null) {
                    text += (PIPE_WITH_SPACES + desc);
                }
                tvText.setText(text);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.addOngoingProgramImage(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Add coming program images and texts.
     * @param index
     * @param icon
     * @param epgItem
     */
    private void addComingProgramImages(int index, String icon, EpgItem epgItem)  {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.addComingProgramImages() called.");
            }

            ComingProgramImageAndTextId cpi = COMING_PROGRAM_IMAGE_AND_TEXT.get(index);
            if (cpi != null) {
                ImageView imageView = root.findViewById(cpi.getImageId());
                if (imageView != null) {
                    Glide.with(this).asBitmap().load(icon).into(imageView);
                }

                TextView textView = root.findViewById(cpi.getTextId());
                if (textView != null) {
                    String text = epgItem.getLocalStartTime() + SPACE + epgItem.getTitle();
                    String desc = epgItem.getDesc();
                    if (desc != null) {
                        text += (PIPE_WITH_SPACES + desc);
                    }
                    textView.setText(text);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.addComingProgramImages(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Adds and updates guide data on main view.
     */
    private void updateGuide()  {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.updateGuide() called.");
            }

            List<EpgItem> guideElements  = viewModel.getGuideData(guideIndex, GUIDE_ELEMENT_COUNT);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.updateGuide() Guide elements size: " + guideElements.size());
            }

            for(int i = 0; i < guideElements.size(); i++) {
                EpgItem e = guideElements.get(i);
                ProgramRowId gd = PROGRAM_ROW.get(i);

                TextView rowTime = root.findViewById(gd.getTimeId());
                TextView rowTitle = root.findViewById(gd.getTitleId());
                TextView rowDesc = root.findViewById(gd.getDescId());
                if (rowTime != null && rowTitle != null && rowDesc != null) {
                    rowTime.setText(e.getLocalStartTime());
                    rowTitle.setText(e.getTitle());

                    String desc = e.getDesc();
                    if (desc != null) {
                        desc = PIPE_WITH_SPACES + desc;
                        rowDesc.setText(desc);
                    }
                }

                if (i == 0) {
                    // First row - set today text or date
                    TextView todayText = root.findViewById(R.id.todayText);
                    if (todayText != null) {
                        if (e.getStartDateToday()) {
                            todayText.setText(getResources().getString(R.string.today));
                        }
                        else {
                            todayText.setText(e.getLocalStartDate());
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.addComingProgramImages(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Starts timer. Used to update images and guide data on main view at a specific interval.
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

                updateUiInMainThread();
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
     * Update UI on main thread. This method is called from timer.
     */
    private void updateUiInMainThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    createMainView(CallOrigin.Timer);
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
     * Handles keydown events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                this.toVideoPlayer();
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (viewModel.isListItemInIndex(guideIndex + GUIDE_ELEMENT_COUNT)) {
                    guideIndex++;
                    this.updateGuide();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (guideIndex > 0) {
                    guideIndex--;
                    this.updateGuide();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "MainFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                this.toExitOverlay();
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return true;
    }

    /**
     * Finds a video player fragment and replaces main fragment from it.
     */
    private void toVideoPlayer() {
        // Add video player fragment to container

        this.checkFragmentManager();

        Fragment videoPlayerFragment = fragmentManager.findFragmentByTag(VIDEO_PLAYER_FRAGMENT);
        if (videoPlayerFragment == null) {
            videoPlayerFragment = VideoPlayerFragment.newInstance();
        }

        // Add parameters
        Bundle bundle = new Bundle();
        bundle.putString(URL_PARAM, STREAM_URL);
        videoPlayerFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.fragment_container, videoPlayerFragment, VIDEO_PLAYER_FRAGMENT).addToBackStack(VIDEO_PLAYER_FRAGMENT).commit();
    }

    /**
     * Adds exit overlay on top of main fragment. Called when user want to exit from application.
     */
    private void toExitOverlay() {
        // Add exit overlay fragment to container

        this.checkFragmentManager();

        Fragment exitOverlayFragment = fragmentManager.findFragmentByTag(EXIT_OVERLAY_FRAGMENT);
        if (exitOverlayFragment == null) {
            exitOverlayFragment = ExitFragment.newInstance();
        }

        fragmentManager.beginTransaction().add(R.id.fragment_container, exitOverlayFragment, EXIT_OVERLAY_FRAGMENT).addToBackStack(EXIT_OVERLAY_FRAGMENT).commit();
    }

    /**
     * Checks fragment manager and creates it if needed.
     */
    private void checkFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
    }
}
