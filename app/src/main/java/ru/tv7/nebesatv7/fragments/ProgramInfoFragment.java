package ru.tv7.nebesatv7.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.List;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.helpers.Sidebar;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.model.SharedCacheViewModel;

import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_PLAYER_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.BROADCAST_DATE_TIME;
import static ru.tv7.nebesatv7.helpers.Constants.CAPTION;
import static ru.tv7.nebesatv7.helpers.Constants.COLON_WITH_SPACE;
import static ru.tv7.nebesatv7.helpers.Constants.DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.EPISODE_NUMBER;
import static ru.tv7.nebesatv7.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesatv7.helpers.Constants.IS_VISIBLE_ON_VOD;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.ONE_STR;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_AND_NAME;

/**
 * Program info fragment. Shows info of program and possible play video button.
 */
public class ProgramInfoFragment extends Fragment {

    private View root = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private JSONObject selectedProgram = null;
    private List<TextView> menuTexts = null;

    private ImageView backgroundImage = null;
    private ImageView startButton = null;

    /**
     * Default constructor.
     */
    public ProgramInfoFragment() {

    }

    /**
     * Creates and returns a new instance of this program info fragment.
     * @return
     */
    public static ProgramInfoFragment newInstance() {
        return new ProgramInfoFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ProgramInfoFragment.onCreate() called.");
        }

        sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
    }

    /**
     * onCreateView() - Android lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            root = inflater.inflate(R.layout.fragment_program_info, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            selectedProgram = sharedCacheViewModel.getSelectedProgram();
            if (selectedProgram == null) {
                throw new Exception("Program info page. Not selected program passed to this fragment!");
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onCreateView(): Selected program: " + selectedProgram.getString(SERIES_AND_NAME));
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            backgroundImage = root.findViewById(R.id.backgroundImage);
            if (backgroundImage != null) {
                String imagePath = Utils.getValue(selectedProgram, IMAGE_PATH);
                if (imagePath != null) {
                    Glide.with(this).asBitmap().load(imagePath).into(backgroundImage);
                }
                else {
                    Glide.with(this).asBitmap().load(R.drawable.tv7_app_icon).into(backgroundImage);
                }
            }

            boolean videoAvailable = Utils.getValue(selectedProgram, IS_VISIBLE_ON_VOD).equals(ONE_STR);

            startButton = root.findViewById(R.id.startButton);
            if (startButton != null) {
                if (videoAvailable) {
                    // play button available
                    Utils.requestFocus(startButton);
                }
                else {
                    startButton.setVisibility(View.GONE);
                    if (backgroundImage != null) {
                        Utils.requestFocus(backgroundImage);
                    }
                }
            }

            Resources resources = getResources();
            if (resources != null) {
                String titleText = null;
                String valueText = null;

                TextView firstBroadcast = root.findViewById(R.id.firstBroadcast);
                if (firstBroadcast != null) {
                    valueText = Utils.getValue(selectedProgram, BROADCAST_DATE_TIME);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(videoAvailable ? R.string.first_broadcast : R.string.coming_on_channel);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        firstBroadcast.setText(valueText);
                    }
                    else {
                        firstBroadcast.setVisibility(View.GONE);
                    }
                }

                TextView duration = root.findViewById(R.id.duration);
                if (duration != null) {
                    valueText = Utils.getValue(selectedProgram, DURATION);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(R.string.duration);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        duration.setText(valueText);
                    }
                    else {
                        duration.setVisibility(View.GONE);
                    }
                }

                TextView episodeNbr = root.findViewById(R.id.episode);
                if (episodeNbr != null) {
                    valueText = Utils.getValue(selectedProgram, EPISODE_NUMBER);
                    if (valueText != null && valueText.length() > 0) {
                        titleText = resources.getString(R.string.episode);
                        valueText = titleText + COLON_WITH_SPACE + valueText;

                        episodeNbr.setText(valueText);
                    }
                    else {
                        episodeNbr.setVisibility(View.GONE);
                    }
                }

                TextView caption = root.findViewById(R.id.caption);
                if (caption != null) {
                    valueText = Utils.getValue(selectedProgram, CAPTION);
                    if (valueText != null && valueText.length() > 0) {
                        caption.setText(valueText);
                    }
                    else {
                        caption.setVisibility(View.GONE);
                    }
                }

                TextView seriesAndName = root.findViewById(R.id.seriesAndName);
                if (seriesAndName != null) {
                    valueText = Utils.getValue(selectedProgram, SERIES_AND_NAME);
                    if (valueText != null) {
                        seriesAndName.setText(valueText);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }
        return root;
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
                Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    if (focusedView.getId() == R.id.startButton) {
                        sharedCacheViewModel.setPageToHistory(PROGRAM_INFO_FRAGMENT);
                        Utils.toPage(ARCHIVE_PLAYER_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedView != null && (focusedId == R.id.startButton || focusedId == R.id.backgroundImage)) {
                    Sidebar.showMenuTexts(menuTexts);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage != null) {
                        Utils.toPage(toPage, getActivity(), true, false,null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramInfoFragment.onKeyDown(): Exception: " + e);
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
        Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

        if (startButton.getVisibility() == View.VISIBLE) {
            Utils.requestFocus(startButton);
        }
        else {
            Utils.requestFocus(backgroundImage);
        }
    }
}
