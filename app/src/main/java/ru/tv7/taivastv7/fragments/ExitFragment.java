package ru.tv7.taivastv7.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.tv7.taivastv7.BuildConfig;
import ru.tv7.taivastv7.R;
import ru.tv7.taivastv7.helpers.Utils;

import static ru.tv7.taivastv7.helpers.Constants.LOG_TAG;
import static ru.tv7.taivastv7.helpers.Constants.MAIN_FRAGMENT;

/**
 * Exit fragment. Shows exit overlay when user want to exit from application.
 * Add fragment to top of main fragment. Yes and No buttons available on overlay.
 */
public class ExitFragment extends Fragment {

    private View root = null;
    private TextView yesButton = null;
    private TextView noButton = null;
    private FragmentManager fragmentManager = null;
    private int selectedButtonId = R.id.yesButton;

    /**
     * Default constructor.
     */
    public ExitFragment() {

    }

    /**
     * Creates and returns a new instance of this exit fragment.
     * @return
     */
    public static ExitFragment newInstance() {
        return new ExitFragment();
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
            root = inflater.inflate(R.layout.fragment_exit, container, false);

            RelativeLayout exitOverlayContainer = root.findViewById(R.id.exitOverlayContainer);
            if (exitOverlayContainer != null) {
                Utils.fadePageAnimation(exitOverlayContainer);
            }

            yesButton = root.findViewById(R.id.yesButton);
            noButton = root.findViewById(R.id.noButton);

            if (yesButton != null && noButton != null) {
                this.focusToButton(yesButton, noButton);
            }
            else {
                Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onCreateView(): Exception: " + e);
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
                Log.d(LOG_TAG, "ExitFragment.onKeyDown(): keyCode: " + keyCode);
            }

            int id = getSelectedButtonId();

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (id == R.id.yesButton) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "ExitFragment.onKeyDown(): Yes button selected. Button ID: " + id);
                    }

                    // Exit from application
                    this.exitFromApplication();
                }
                else if (id == R.id.noButton) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "ExitFragment.onKeyDown(): No button selected. Button ID: " + id);
                    }

                    this.backToMainView();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (id == R.id.noButton) {
                    this.focusToButton(yesButton, noButton);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (id == R.id.yesButton) {
                    this.focusToButton(noButton, yesButton);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                this.backToMainView();
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
        }

        return false;
    }

    /**
     * Checks fragment manager and creates it if needed.
     */
    private void checkFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getActivity().getSupportFragmentManager();
        }
    }

    /**
     * Handles button focus functionality.
     * @param focus
     * @param noFocus
     */
    private void focusToButton(TextView focus, TextView noFocus) {
        if (focus != null && noFocus != null) {
            Context context = getContext();

            focus.setBackground(ContextCompat.getDrawable(context, R.drawable.exit_button_focus));
            noFocus.setBackground(ContextCompat.getDrawable(context, R.drawable.exit_button_no_focus));

            this.setSelectedButtonId(focus.getId());
        }
    }

    /**
     * Exits from application. User has selected 'Yes' button on exit fragment.
     */
    private void exitFromApplication() {
        getActivity().finishAffinity();
    }

    private void backToMainView() {
        this.checkFragmentManager();

        Fragment mainFragment = fragmentManager.findFragmentByTag(MAIN_FRAGMENT);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }

        fragmentManager.beginTransaction().replace(R.id.fragment_container, mainFragment, MAIN_FRAGMENT).addToBackStack(MAIN_FRAGMENT).commit();
    }

    /**
     * Returns selected button id.
     * @return
     */
    private int getSelectedButtonId() {
        return selectedButtonId;
    }

    /**
     * Sets selected button id.
     * @param id
     */
    private void setSelectedButtonId(int id) {
        selectedButtonId = id;
    }
}