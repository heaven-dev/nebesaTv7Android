package ru.tv7.nebesa.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.LEFT_BRACKET;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.OS_VERSION;
import static ru.tv7.nebesa.helpers.Constants.RIGHT_BRACKET;
import static ru.tv7.nebesa.helpers.Constants.SPACE;

/**
 * About fragment. Shows app and platform info.
 */
public class AboutFragment extends Fragment {

    private View root = null;
    private List<TextView> menuTexts = null;

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

            Utils.requestFocusById(root, R.id.aboutContentContainer);

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
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (!Sidebar.isSideMenuOpen(menuTexts)) {
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
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.aboutMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.aboutMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "AboutFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
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

        Utils.requestFocusById(root, R.id.aboutContentContainer);
    }
}
