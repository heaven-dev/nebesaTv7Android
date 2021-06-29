package ru.tv7.nebesa.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.NebesaTv7;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;

/**
 * About fragment. Shows app error page.
 */
public class ErrorFragment extends Fragment {

    private View root = null;
    private TextView restartButton = null;
    private TextView closeButton = null;

    /**
     * Default constructor.
     */
    public ErrorFragment() {

    }

    /**
     * Creates and returns a new instance of this error fragment.
     * @return
     */
    public static ErrorFragment newInstance() {
        return new ErrorFragment();
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
            root = inflater.inflate(R.layout.fragment_error, container, false);

            RelativeLayout errorContentContainer = root.findViewById(R.id.errorContentContainer);
            if (errorContentContainer != null) {
                Utils.fadePageAnimation(errorContentContainer);
            }

            TextView tv = root.findViewById(R.id.errorText);
            if (tv != null) {
                String errorText = NebesaTv7.getInstance().getErrorString();
                if (errorText != null) {
                    tv.setText(errorText);
                }
                else {
                    tv.setText(R.string.something_went_wrong);
                }
            }

            restartButton = root.findViewById(R.id.restartButton);
            closeButton = root.findViewById(R.id.closeButton);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ErrorFragment.onCreateView(): Exception: " + e);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (restartButton != null) {
            restartButton.requestFocus();
        }
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
                Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = root.findFocus();
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (focusedView == restartButton) {
                    Activity activity = NebesaTv7.getInstance().getActivity();
                    if (activity != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): Restart app!");
                        }

                        activity.finish();
                        activity.startActivity(activity.getIntent());
                    }
                }
                else if (focusedView == closeButton) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): Exit from app!");
                    }

                    this.exitFromApplication();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (focusedView == closeButton) {
                    restartButton.requestFocus();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (focusedView == restartButton) {
                    closeButton.requestFocus();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ErrorFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Exits from application. User has selected 'Yes' button on exit fragment.
     */
    private void exitFromApplication() {
        getActivity().finishAffinity();
    }
}
