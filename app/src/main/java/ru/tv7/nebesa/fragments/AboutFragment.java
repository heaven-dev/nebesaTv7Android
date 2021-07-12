package ru.tv7.nebesa.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CLEAR_MENU_IDS;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.LEFT_BRACKET;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.OS_VERSION;
import static ru.tv7.nebesa.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesa.helpers.Constants.RIGHT_BRACKET;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.SPACE;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_TAG;

/**
 * About fragment. Shows app and platform info.
 */
public class AboutFragment extends Fragment {

    private View root = null;
    private List<TextView> menuTexts = null;

    private boolean clearMenuVisible = false;
    private int selectedClearMenuItemId = 0;

    private SharedCacheViewModel sharedCacheViewModel = null;

    /**
     * Default constructor.
     */
    public AboutFragment() {

    }

    /**
     * Creates and returns a new instance of this about fragment.
     * @return
     */
    public static AboutFragment newInstance() {
        return new AboutFragment();
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
                Log.d(LOG_TAG, "AboutFragment.onCreate() called.");
            }

            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "AboutFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
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
            root = inflater.inflate(R.layout.fragment_about, container, false);

            RelativeLayout aboutContentContainer = root.findViewById(R.id.aboutContentContainer);
            if (aboutContentContainer != null) {
                Utils.fadePageAnimation(aboutContentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.aboutMenuContainer);

            Utils.requestFocusById(root, R.id.clearButton);

            Context context = getContext();

            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String packageName = pInfo.packageName;
            int versionCode = pInfo.versionCode;

            TextView tv = root.findViewById(R.id.appPackage);
            if (tv != null && packageName != null) {
                tv.setText(packageName);
            }

            tv = root.findViewById(R.id.appVersion);
            if (tv != null) {
                tv.setText(String.valueOf(versionCode));
            }

            String osVersion = System.getProperty(OS_VERSION) + LEFT_BRACKET + android.os.Build.VERSION.INCREMENTAL + RIGHT_BRACKET;
            int apiLevel = android.os.Build.VERSION.SDK_INT;
            String androidVersion = android.os.Build.VERSION.RELEASE;
            String device = android.os.Build.DEVICE;
            String modelAndProduct = android.os.Build.MODEL + SPACE + LEFT_BRACKET + android.os.Build.PRODUCT + RIGHT_BRACKET;

            tv = root.findViewById(R.id.osVersion);
            if (tv != null && osVersion != null) {
                tv.setText(osVersion);
            }

            tv = root.findViewById(R.id.apiLevel);
            if (tv != null) {
                tv.setText(String.valueOf(apiLevel));
            }

            tv = root.findViewById(R.id.androidVersion);
            if (tv != null) {
                tv.setText(androidVersion);
            }

            tv = root.findViewById(R.id.device);
            if (tv != null && device != null) {
                tv.setText(device);
            }

            tv = root.findViewById(R.id.modelAndProduct);
            if (tv != null && modelAndProduct != null) {
                tv.setText(modelAndProduct);
            }

        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "AboutFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Handles key down events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "AboutFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.aboutMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "AboutFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else if (this.isClearButtonContainerVisible()) {
                    int id = focusedView.getId();
                    if (id == R.id.okButton) {
                        this.deleteConfiguration();
                        this.showHideClearMenu();
                    }
                    else if (id == R.id.cancelButton) {
                        this.showHideClearMenu();
                    }
                }
                else {
                    int id = focusedView.getId();
                    if (clearMenuVisible) {
                        this.setClearButtonContainerVisibility(View.VISIBLE);
                        Utils.requestFocusById(root, R.id.buttonContainer);

                        selectedClearMenuItemId = id;
                    }
                    else {
                        if (id == R.id.clearButton) {
                            this.showHideClearMenu();
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (this.isClearButtonContainerVisible()) {
                    int id = focusedView.getId();
                    if (id == R.id.cancelButton) {
                        Utils.requestFocusById(root, R.id.okButton);
                    }
                }
                else if (!Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.aboutMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else if (this.isClearButtonContainerVisible()) {
                    int id = focusedView.getId();
                    if ( id == R.id.okButton) {
                        Utils.requestFocusById(root, R.id.cancelButton);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.aboutMenuContainer);
                }
                else {
                    int nextFocusId = focusedView.getNextFocusDownId();
                    if (nextFocusId != -1) {
                        Utils.requestFocusById(root, nextFocusId);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.aboutMenuContainer);
                }
                int nextFocusId = focusedView.getNextFocusUpId();
                if (nextFocusId != -1) {
                    Utils.requestFocusById(root, nextFocusId);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else if (clearMenuVisible) {
                    this.showHideClearMenu();
                }
                else {
                    Utils.toPage(ARCHIVE_MAIN_FRAGMENT, getActivity(), true, false,null);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "AboutFragment.onKeyDown(): Exception: " + e);
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
        Sidebar.setSelectedMenuItem(root, R.id.aboutMenuContainer);

        Utils.requestFocusById(root, R.id.clearButton);
    }

    /**
     * Shows and hides clear menu container.
     */
    private void showHideClearMenu() throws Exception {
        RelativeLayout aboutContentContainer = root.findViewById(R.id.aboutContentContainer);
        RelativeLayout clearMenuContainer = root.findViewById(R.id.clearMenuContainer);
        if (aboutContentContainer != null && clearMenuContainer != null) {
            if (clearMenuVisible) {
                clearMenuContainer.setVisibility(View.GONE);
                clearMenuContainer.getLayoutParams().width = 0;
                this.setClearButtonContainerVisibility(View.GONE);

                Utils.requestFocusById(root, R.id.clearButton);
            }
            else {
                int widthPx = aboutContentContainer.getWidth();
                widthPx = Math.round((float)widthPx / (float)2);

                clearMenuContainer.getLayoutParams().width = widthPx;
                clearMenuContainer.setVisibility(View.VISIBLE);

                List<String> clearConfMenuItems = new ArrayList<>();

                Resources resources = getResources();
                String items = resources.getString(R.string.items);
                Context context = getContext();

                if (items == null || context == null) {
                    return;
                }

                clearConfMenuItems.add(resources.getString(R.string.video_status)
                        + PIPE_WITH_SPACES
                        + Utils.getCountOfPrefs(VIDEO_STATUSES_SP_TAG, VIDEO_STATUSES_SP_DEFAULT, context)
                        + SPACE
                        + items);

                clearConfMenuItems.add(resources.getString(R.string.favorites_eng)
                        + PIPE_WITH_SPACES
                        + Utils.getCountOfPrefs(FAVORITES_SP_TAG, FAVORITES_SP_DEFAULT, context)
                        + SPACE
                        + items);

                clearConfMenuItems.add(resources.getString(R.string.search_history)
                        + PIPE_WITH_SPACES
                        + Utils.getCountOfPrefs(SAVED_SEARCH_SP_TAG, SAVED_SEARCH_SP_DEFAULT, context)
                        + SPACE
                        + items);

                for (int i = 0; i < CLEAR_MENU_IDS.size(); i++) {
                    TextView tv = root.findViewById(CLEAR_MENU_IDS.get(i));
                    if (tv != null) {
                        tv.setText(clearConfMenuItems.get(i));
                        tv.setVisibility(View.VISIBLE);
                    }
                }

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.requestFocusById(root, R.id.cm_0);
                    }
                }, 0);
            }

            clearMenuVisible = !clearMenuVisible;
        }
    }

    /**
     * Sets clear menu question text and buttons visibility.
     * @param visibility
     */
    private void setClearButtonContainerVisibility(int visibility) {
        LinearLayout buttonContainer = root.findViewById(R.id.buttonContainer);
        if (buttonContainer != null) {
            buttonContainer.setVisibility(visibility);
        }
    }

    /**
     * Returns true if clear button container is visible. Otherwise false.
     * @return
     */
    private boolean isClearButtonContainerVisible() {
        LinearLayout buttonContainer = root.findViewById(R.id.buttonContainer);
        if (buttonContainer != null) {
            return buttonContainer.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * Deletes the selected configuration from shared preferences.
     */
    private void deleteConfiguration() {
        Context context = getContext();
        if (context != null) {
            if (selectedClearMenuItemId == R.id.cm_0) {
                // video status
                Utils.deleteSavedPrefs(VIDEO_STATUSES_SP_TAG, context);
            }
            else if (selectedClearMenuItemId == R.id.cm_1) {
                // favorites
                Utils.deleteSavedPrefs(FAVORITES_SP_TAG, context);
            }
            else if (selectedClearMenuItemId == R.id.cm_2) {
                // search history
                Utils.deleteSavedPrefs(SAVED_SEARCH_SP_TAG, context);
            }
        }

        selectedClearMenuItemId = 0;
    }
}
