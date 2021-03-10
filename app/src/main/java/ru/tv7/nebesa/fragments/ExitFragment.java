package ru.tv7.nebesa.fragments;

import android.os.Bundle;
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

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;


/**
 * Exit fragment. Shows exit overlay when user want to exit from application.
 * Add fragment to top of main fragment. Yes and No buttons available on overlay.
 */
public class ExitFragment extends Fragment {

    private View root = null;
    private TextView yesButton = null;
    private TextView noButton = null;

    private SharedCacheViewModel sharedCacheViewModel = null;

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
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onCreate() called.");
            }

            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onCreate(): Exception: " + e);
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
            root = inflater.inflate(R.layout.fragment_exit, container, false);

            RelativeLayout exitOverlayContainer = root.findViewById(R.id.exitOverlayContainer);
            if (exitOverlayContainer != null) {
                Utils.fadePageAnimation(exitOverlayContainer);
            }

            LinearLayout exitContentContainer = root.findViewById(R.id.exitContentContainer);
            if (exitContentContainer != null) {
                Utils.fadePageAnimation(exitContentContainer);
            }

            yesButton = root.findViewById(R.id.yesButton);
            noButton = root.findViewById(R.id.noButton);

            if (yesButton != null && noButton != null) {
                yesButton.requestFocus();
            }
            else {
                Utils.toErrorPage(getActivity());
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onCreateView(): Exception: " + e);
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
                Log.d(LOG_TAG, "ExitFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = root.findFocus();

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (focusedView == yesButton) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "ExitFragment.onKeyDown(): Yes button selected.");
                    }

                    // Exit from application
                    this.exitFromApplication();
                }
                else if (focusedView == noButton) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "ExitFragment.onKeyDown(): No button selected.");
                    }

                    this.backToView();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (focusedView == noButton) {
                    yesButton.requestFocus();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (focusedView == yesButton) {
                    noButton.requestFocus();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ExitFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                this.backToView();
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ExitFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Returns back to view the user come from.
     */
    private void backToView() {
        String page = sharedCacheViewModel.getPageFromHistory();
        if (page != null) {
            Utils.toPage(page, getActivity(), true, true,null);
        }
    }

    /**
     * Exits from application. User has selected 'Yes' button on exit fragment.
     */
    private void exitFromApplication() {
        getActivity().finishAffinity();
    }
}
