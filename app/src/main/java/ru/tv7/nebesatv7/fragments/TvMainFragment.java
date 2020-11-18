package ru.tv7.nebesatv7.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.enums.CallOrigin;
import ru.tv7.nebesatv7.helpers.ComingProgramImageAndTextId;
import ru.tv7.nebesatv7.helpers.EpgItem;
import ru.tv7.nebesatv7.helpers.GuideRowId;
import ru.tv7.nebesatv7.helpers.Sidebar;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.model.ProgramScheduleViewModel;
import ru.tv7.nebesatv7.model.SharedCacheViewModel;

import static ru.tv7.nebesatv7.helpers.Constants.CHANNEL_URL_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.COMING_PROGRAM_IMAGE_AND_TEXT;
import static ru.tv7.nebesatv7.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_ELEMENT_COUNT;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_ROWS;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_TIMER_TIMEOUT;
import static ru.tv7.nebesatv7.helpers.Constants.HTTP;
import static ru.tv7.nebesatv7.helpers.Constants.HTTPS;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAM_VISIBLE_IMAGE_COUNT;
import static ru.tv7.nebesatv7.helpers.Constants.SPACE;
import static ru.tv7.nebesatv7.helpers.Constants.STREAM_URL;
import static ru.tv7.nebesatv7.helpers.Constants.TV_MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.TV_PLAYER_FRAGMENT;

/**
 * Tv main fragment. Main view of application.
 */
public class TvMainFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    private View root = null;
    private ProgramScheduleViewModel viewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private String programStart = null;
    private int guideIndex = 0;
    private Timer timer = null;

    private ImageView startButton = null;
    private ImageView upDownArrows = null;

    private List<TextView> menuTexts = null;

    /**
     * Default constructor.
     */
    public TvMainFragment() { }

    /**
     * Creates and return a new instance of main fragment class.
     * @return
     */
    public static TvMainFragment newInstance() {
        return new TvMainFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TvMainFragment.onCreate() called.");
        }

        viewModel = ViewModelProviders.of(requireActivity()).get(ProgramScheduleViewModel.class);
        sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);

        FragmentManager fragmentManager = Utils.getFragmentManager(getActivity());
        if (fragmentManager != null) {
            fragmentManager.addOnBackStackChangedListener(this);
        }
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
                Log.d(LOG_TAG, "TvMainFragment.onCreateView() called.");
            }

            root = inflater.inflate(R.layout.fragment_tv_main, container, false);

            LinearLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            programStart = null;
            guideIndex = 0;

            this.createMainView(CallOrigin.NoTimer);
            this.addCountdownTimer();

            startButton = root.findViewById(R.id.startButton);
            if (startButton != null) {
                Utils.requestFocus(startButton);
            }

            upDownArrows = root.findViewById(R.id.upDownArrows);

            menuTexts = Sidebar.getMenuTextItems(root);

            Sidebar.setSelectedMenuItem(root, R.id.tvMenuContainer);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
        return root;
    }

    /**
     * onViewCreated() - Android lifecycle method.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startButton = root.findViewById(R.id.startButton);
        if (startButton != null) {
            startButton.post(new Runnable() {
                @Override
                public void run() {
                    Utils.requestFocus(startButton);
                }
            });
        }
    }

    /**
     * Back stack changed listener.
     */
    @Override
    public void onBackStackChanged() {
        View view = getView();
        if (view != null && view.getId() == R.id.tvMainFragment) {
            ImageView startButton = root.findViewById(R.id.startButton);
            if (startButton != null) {
                startButton.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.requestFocus(startButton);
                    }
                });
            }
        }
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
            Log.d(LOG_TAG, "TvMainFragment.createMainView() called.");
        }

        boolean updateContent = false;
        List<EpgItem> programs = viewModel.getOngoingAndComingPrograms(PROGRAM_VISIBLE_IMAGE_COUNT);

        if (programs.size() == PROGRAM_VISIBLE_IMAGE_COUNT) {

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

                    String time = epgItem.getStart();
                    if (!time.equals(programStart)) {
                        // ongoing program changed from previous check - updated images and guide
                        updateContent = true;
                        programStart = time;
                    }
                }

                if (updateContent) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "TvMainFragment.createMainView(): Update content.");
                    }

                    String imageUrl = epgItem.getIcon();

                    // Change scheme from http to https
                    if (imageUrl != null && !imageUrl.startsWith(HTTPS)) {
                        imageUrl = imageUrl.replace(HTTP, HTTPS);
                    }

                    if (index == 0) {
                        this.addOngoingProgramImage(imageUrl, epgItem);
                    } else {
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
    }

    /**
     * Adds ongoing program image and texts to view.
     * @param icon
     * @param epgItem
     */
    private void addOngoingProgramImage(String icon, EpgItem epgItem) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addOngoingProgramImage() called.");
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
                Log.d(LOG_TAG, "TvMainFragment.addOngoingProgramImage(): Exception: " + e);
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
                Log.d(LOG_TAG, "TvMainFragment.addComingProgramImages() called.");
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
                    textView.setText(text);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addComingProgramImages(): Exception: " + e);
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
                Log.d(LOG_TAG, "TvMainFragment.updateGuide() called.");
            }

            List<EpgItem> guideElements  = viewModel.getGuideData(guideIndex, GUIDE_ELEMENT_COUNT);

            if (guideElements.size() == GUIDE_ELEMENT_COUNT) {

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.updateGuide() Guide elements size: " + guideElements.size());
                }

                for (int i = 0; i < guideElements.size(); i++) {
                    EpgItem e = guideElements.get(i);
                    GuideRowId gd = GUIDE_ROWS.get(i);

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
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.updateGuide(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Starts timer. Used to update images and guide data on main view at a specific interval.
     */
    private void addCountdownTimer() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TvMainFragment.addCountdownTimer() called.");
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
        }, GUIDE_TIMER_TIMEOUT, GUIDE_TIMER_TIMEOUT);
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
                Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                Utils.requestFocus(startButton);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.tvMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else {
                    if (focusedView == startButton) {
                        sharedCacheViewModel.setPageToHistory(TV_MAIN_FRAGMENT);

                        Bundle bundle = new Bundle();
                        bundle.putString(CHANNEL_URL_PARAM, STREAM_URL);

                        Utils.toPage(TV_PLAYER_FRAGMENT, getActivity(), true, false, bundle);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (focusedView == upDownArrows) {
                    Utils.requestFocus(startButton);
                }
                else if (focusedView == startButton) {
                    Sidebar.showMenuTexts(menuTexts);
                    Sidebar.setFocusToMenu(root, R.id.tvMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    if (focusedView == startButton) {
                        Utils.requestFocus(upDownArrows);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.tvMenuContainer);
                }
                else {
                    focusedView = Utils.getFocusedView(getActivity());

                    if (focusedView == upDownArrows && viewModel.isListItemInIndex(guideIndex + GUIDE_ELEMENT_COUNT)) {
                        guideIndex++;
                        this.updateGuide();
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.tvMenuContainer);
                }
                else {
                    focusedView = Utils.getFocusedView(getActivity());

                    if (focusedView == upDownArrows && guideIndex > 0) {
                        guideIndex--;
                        this.updateGuide();
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.setPageToHistory(TV_MAIN_FRAGMENT);
                    Utils.toPage(EXIT_OVERLAY_FRAGMENT, getActivity(), false, false,null);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts);
        Sidebar.setSelectedMenuItem(root, R.id.tvMenuContainer);

        Utils.requestFocus(startButton);
    }
}
