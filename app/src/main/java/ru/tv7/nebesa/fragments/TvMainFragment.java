package ru.tv7.nebesa.fragments;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.enums.CallOrigin;
import ru.tv7.nebesa.helpers.ComingProgramImageAndTextId;
import ru.tv7.nebesa.helpers.GuideItem;
import ru.tv7.nebesa.helpers.GuideRowId;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.GuideViewModel;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.BROADCAST_DATE;
import static ru.tv7.nebesa.helpers.Constants.BROADCAST_DATE_TIME;
import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.CHANNEL_URL_PARAM;
import static ru.tv7.nebesa.helpers.Constants.COMING_PROGRAM_IMAGE_AND_TEXT;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX;
import static ru.tv7.nebesa.helpers.Constants.DURATION;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.END_DATE;
import static ru.tv7.nebesa.helpers.Constants.END_TIME;
import static ru.tv7.nebesa.helpers.Constants.EPISODE_NUMBER;
import static ru.tv7.nebesa.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.FORMATTED_END_TIME;
import static ru.tv7.nebesa.helpers.Constants.FORMATTED_START_TIME;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_DATA;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_ELEMENT_COUNT;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_ROWS;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_TIMER_TIMEOUT;
import static ru.tv7.nebesa.helpers.Constants.HTTP;
import static ru.tv7.nebesa.helpers.Constants.HTTPS;
import static ru.tv7.nebesa.helpers.Constants.ID_NULL;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.IS_VISIBLE_ON_VOD;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.NAME;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_LIST_MIN_SIZE;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_VISIBLE_IMAGE_COUNT;
import static ru.tv7.nebesa.helpers.Constants.SERIES;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;
import static ru.tv7.nebesa.helpers.Constants.SID;
import static ru.tv7.nebesa.helpers.Constants.SPACE;
import static ru.tv7.nebesa.helpers.Constants.START_DATE;
import static ru.tv7.nebesa.helpers.Constants.START_END_TIME;
import static ru.tv7.nebesa.helpers.Constants.STREAM_URL;
import static ru.tv7.nebesa.helpers.Constants.TIME;
import static ru.tv7.nebesa.helpers.Constants.TV_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.TV_PLAYER_FRAGMENT;

/**
 * Tv main fragment. Main view of application.
 */
public class TvMainFragment extends Fragment implements ArchiveDataLoadedListener, FragmentManager.OnBackStackChangedListener {

    private View root = null;
    private GuideViewModel guideViewModel = null;
    private ArchiveViewModel archiveViewModel = null;

    private SharedCacheViewModel sharedCacheViewModel = null;
    private ArchiveDataLoadedListener archiveDataLoadedListener = null;

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
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onCreate() called.");
            }

            guideViewModel = ViewModelProviders.of(requireActivity()).get(GuideViewModel.class);
            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);

            FragmentManager fragmentManager = Utils.getFragmentManager(getActivity());
            if (fragmentManager != null) {
                fragmentManager.addOnBackStackChangedListener(this);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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

            this.setEpgDataLoadedListener(this);

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

            guideViewModel.removePastProgramItems();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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
        try {
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
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onViewCreated(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Back stack changed listener.
     */
    @Override
    public void onBackStackChanged() {
        View view = getView();
        String exitFragment = sharedCacheViewModel.getExitFragment();

        if (view != null && view.getId() == R.id.tvMainFragment && exitFragment != null && exitFragment.equals(TV_MAIN_FRAGMENT)) {
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
     * Archive data guide by date load response.
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onEpgDataLoaded(): EpgData load/parse ok.");
            }

            if (jsonArray != null && jsonArray.length() == 1) {
                JSONObject obj = jsonArray.getJSONObject(0);
                if (obj != null) {
                    if (obj.getInt(DATE_INDEX) == 1) {
                        this.addGuideData(obj.getJSONArray(GUIDE_DATA));
                    }
                }
            }

            guideViewModel.removePastProgramItems();
            this.createMainView(CallOrigin.Timer);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onEpgDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Archive data load error response.
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TvMainFragment.onEpgDataLoadError(): EpgData load/parse error: " + message);
        }

        Utils.toErrorPage(getActivity());
    }

    /**
     * Archive data load network error response.
     */
    @Override
    public void onNetworkError(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TvMainFragment.onNetworkError(): ***Network error!***");
        }

        Utils.toErrorPage(getActivity());
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
     * Adds guide data to view model.
     * @param guideData
     * @throws Exception
     */
    private void addGuideData(JSONArray guideData) throws  Exception {
        if (guideData == null) {
            Utils.toErrorPage(getActivity());
        }

        for (int i = 0; i < guideData.length(); i++) {
            JSONObject obj = guideData.getJSONObject(i);
            if (obj != null) {
                GuideItem g = new GuideItem(
                    Utils.getJsonStringValue(obj, TIME),
                    Utils.getJsonStringValue(obj, END_TIME),
                    Utils.getJsonStringValue(obj, IMAGE_PATH),
                    Utils.getJsonStringValue(obj, CAPTION),
                    Utils.getJsonStringValue(obj, START_END_TIME),
                    Utils.getJsonStringValue(obj, START_DATE),
                    Utils.getJsonStringValue(obj, END_DATE),
                    Utils.getJsonStringValue(obj, FORMATTED_START_TIME),
                    Utils.getJsonStringValue(obj, FORMATTED_END_TIME),
                    Utils.getJsonStringValue(obj, BROADCAST_DATE),
                    Utils.getJsonStringValue(obj, BROADCAST_DATE_TIME),
                    Utils.getJsonStringValue(obj, DURATION),
                    Utils.getJsonStringValue(obj, SERIES),
                    Utils.getJsonStringValue(obj, NAME),
                    Utils.getJsonIntValue(obj, SID),
                    Utils.getJsonIntValue(obj, EPISODE_NUMBER),
                    Utils.getJsonIntValue(obj, IS_VISIBLE_ON_VOD),
                    Utils.getJsonStringValue(obj, SERIES_AND_NAME),
                    Utils.isStartDateToday(Utils.getJsonStringValue(obj, TIME)));

                guideViewModel.addItemToGuide(g);
            }
        }
    }

    /**
     * Creates a main view.
     * @param callOrigin
     * @throws Exception
     */
    private synchronized void createMainView(CallOrigin callOrigin) throws Exception {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "TvMainFragment.createMainView() called.");
        }

        boolean updateContent = false;
        List<GuideItem> programs = guideViewModel.getOngoingAndComingPrograms(PROGRAM_VISIBLE_IMAGE_COUNT);

        if (programs.size() == PROGRAM_VISIBLE_IMAGE_COUNT) {

            for(int index = 0; index < programs.size(); index++) {
                GuideItem guideItem = programs.get(index);

                if (index == 0) {
                    // update progress bar
                    ProgressBar programProgress = root.findViewById(R.id.programProgress);
                    if (programProgress != null) {
                        Integer progressValue = guideItem.getOngoingProgress();
                        if (progressValue != null) {
                            programProgress.setProgress(progressValue);
                        }
                    }

                    String time = guideItem.getStart();
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

                    String imageUrl = guideItem.getImagePath();

                    if (imageUrl != null && !imageUrl.equals(EMPTY) && !imageUrl.equals(NULL_VALUE) && !imageUrl.contains(ID_NULL)) {
                        // Change scheme from http to https
                        if (!imageUrl.startsWith(HTTPS)) {
                            imageUrl = imageUrl.replace(HTTP, HTTPS);
                        }
                    }
                    else {
                        imageUrl = null;
                    }

                    if (index == 0) {
                        this.addOngoingProgramImage(imageUrl, guideItem);
                    }
                    else {
                        this.addComingProgramImages(index - 1, imageUrl, guideItem);
                    }
                }
            }

            if (updateContent) {
                if (callOrigin == CallOrigin.Timer) {
                    // remove past epg items from the program list
                    guideViewModel.removePastProgramItems();
                    guideIndex = guideViewModel.getOngoingProgramIndex();
                }

                this.updateGuide();
            }
        }
    }

    /**
     * Adds ongoing program image and texts to view.
     * @param icon
     * @param guideItem
     */
    private void addOngoingProgramImage(String icon, GuideItem guideItem) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addOngoingProgramImage() called.");
            }

            ImageView tvImage = root.findViewById(R.id.tvImage);
            if (tvImage != null) {
                if (icon != null) {
                    Glide.with(this).asBitmap().load(icon).into(tvImage);
                }
                else {
                    Glide.with(this).asBitmap().load(R.drawable.fallback).into(tvImage);
                }
            }

            TextView tvText = root.findViewById(R.id.tvText);
            if (tvText != null) {
                String text = guideItem.getStartTime() + SPACE + guideItem.getSeriesAndName();
                tvText.setText(text);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addOngoingProgramImage(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Add coming program images and texts.
     * @param index
     * @param icon
     * @param guideItem
     */
    private void addComingProgramImages(int index, String icon, GuideItem guideItem)  {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addComingProgramImages() called.");
            }

            ComingProgramImageAndTextId cpi = COMING_PROGRAM_IMAGE_AND_TEXT.get(index);
            if (cpi != null) {
                ImageView imageView = root.findViewById(cpi.getImageId());
                if (imageView != null) {
                    if (icon != null) {
                        Glide.with(this).asBitmap().load(icon).into(imageView);
                    }
                    else {
                        Glide.with(this).asBitmap().load(R.drawable.fallback).into(imageView);
                    }
                }

                TextView textView = root.findViewById(cpi.getTextId());
                if (textView != null) {
                    String text = guideItem.getStartTime() + SPACE + guideItem.getSeriesAndName();
                    textView.setText(text);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.addComingProgramImages(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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

            List<GuideItem> guideElements  = guideViewModel.getGuideData(guideIndex, GUIDE_ELEMENT_COUNT);

            if (guideElements != null && guideElements.size() == GUIDE_ELEMENT_COUNT) {

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "TvMainFragment.updateGuide() Guide elements size: " + guideElements.size());
                }

                for (int i = 0; i < guideElements.size(); i++) {
                    GuideItem g = guideElements.get(i);
                    GuideRowId gd = GUIDE_ROWS.get(i);

                    TextView rowTime = root.findViewById(gd.getTimeId());
                    TextView rowSeries = root.findViewById(gd.getTitleId());
                    TextView rowName = root.findViewById(gd.getDescId());
                    if (rowTime != null && rowSeries != null && rowName != null) {
                        rowTime.setText(g.getStartTime());

                        String series = g.getSeries();
                        if(series != null && series.length() > 0) {
                            rowSeries.setText(series);

                            String name = g.getName();
                            if (name != null && name.length() > 0) {
                                name = PIPE_WITH_SPACES + name;
                                rowName.setText(name);
                            }
                            else {
                                rowName.setText(EMPTY);
                            }
                        }
                        else {
                            rowSeries.setText(g.getName());
                            rowName.setText(EMPTY);
                        }
                    }

                    if (i == 0) {
                        // First row - set today text or date
                        TextView todayText = root.findViewById(R.id.todayText);
                        if (todayText != null) {
                            if (g.getStartDateToday()) {
                                todayText.setText(getResources().getString(R.string.today));
                            }
                            else {
                                todayText.setText(g.getStartDate());
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
            Utils.toErrorPage(getActivity());
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

                int count = guideViewModel.getCountOfNextPrograms();
                if (count <= PROGRAM_LIST_MIN_SIZE) {
                    String date = Utils.getTomorrowUtcFormattedLocalDate();
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "Timer.run() Add items to guide. Date to add: " + date);
                    }
                    archiveViewModel.getGuideByDate(date, 1, TvMainFragment.this);
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
                    Sidebar.showMenuTexts(menuTexts, root);
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

                    if (focusedView == upDownArrows && guideViewModel.isListItemInIndex(guideIndex + GUIDE_ELEMENT_COUNT)) {
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
                    sharedCacheViewModel.setExitFragment(TV_MAIN_FRAGMENT);

                    Utils.toPage(EXIT_OVERLAY_FRAGMENT, getActivity(), false, false,null);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "TvMainFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts, root);
        Sidebar.setSelectedMenuItem(root, R.id.tvMenuContainer);

        Utils.requestFocus(startButton);
    }

    /**
     * Creates epg data load listener.
     * @param archiveDataLoadedListener
     */
    private void setEpgDataLoadedListener(ArchiveDataLoadedListener archiveDataLoadedListener) {
        this.archiveDataLoadedListener = archiveDataLoadedListener;
    }
}
