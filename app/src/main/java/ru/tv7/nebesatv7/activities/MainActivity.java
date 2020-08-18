package ru.tv7.nebesatv7.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.fragments.ExitFragment;
import ru.tv7.nebesatv7.fragments.MainFragment;
import ru.tv7.nebesatv7.fragments.VideoPlayerFragment;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.interfaces.EpgDataLoadedListener;
import ru.tv7.nebesatv7.model.SharedViewModel;

import static ru.tv7.nebesatv7.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRESS_BAR_SIZE;
import static ru.tv7.nebesatv7.helpers.Constants.VIDEO_PLAYER_FRAGMENT;

/**
 * Main activity.
 *  - Load epg data, shows logo and progressbar.
 *  - Opens main fragment.
 *  - Handles keydown events.
 */
public class MainActivity extends AppCompatActivity implements EpgDataLoadedListener {

    private FragmentManager fragmentManager = null;
    private SharedViewModel viewModel = null;
    private EpgDataLoadedListener epgDataLoadedListener = null;

    private View fragmentContainer = null;
    private ImageView startupLogo = null;
    private ProgressBar progressBar = null;

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onCreate() called.");
            }

            viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
            this.setEpgDataLoadedListener(this);

            setContentView(R.layout.activity_main);

            fragmentContainer = findViewById(R.id.fragment_container);
            startupLogo = findViewById(R.id.startupLogo);

            progressBar = findViewById(R.id.startupProgressBar);
            progressBar.setScaleY(PROGRESS_BAR_SIZE);
            progressBar.setScaleX(PROGRESS_BAR_SIZE);

            viewModel.getEpgData(this);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onCreate(): Exception: " + e);
            }
            Utils.showErrorToast(getApplicationContext(), getString(R.string.toast_something_went_wrong));
        }
    }

    /**
     * Handles keydown events and sends event to fragment.
     * @param keyCode
     * @param events
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onKeyDown(): keyCode: " + keyCode);
            }

            this.checkFragmentManager();

            Fragment fragment = this.getVisibleFragment();
            if (fragment instanceof MainFragment) {
                // Main fragment visible - event to main fragment
                Fragment mainFragment = fragmentManager.findFragmentByTag(MAIN_FRAGMENT);
                if (mainFragment != null) {
                    return ((MainFragment) mainFragment).onKeyDown(keyCode, events);
                }
            }
            else if (fragment instanceof VideoPlayerFragment) {
                // Video player fragment visible - event to video player fragment
                Fragment videoPlayerFragment = fragmentManager.findFragmentByTag(VIDEO_PLAYER_FRAGMENT);
                if (videoPlayerFragment != null) {
                    return ((VideoPlayerFragment) videoPlayerFragment).onKeyDown(keyCode, events);
                }
            }
            else if (fragment instanceof ExitFragment) {
                // Exit overlay fragment visible - event to exit overlay fragment
                Fragment exitOverlayFragment = fragmentManager.findFragmentByTag(EXIT_OVERLAY_FRAGMENT);
                if (exitOverlayFragment != null) {
                    return ((ExitFragment) exitOverlayFragment).onKeyDown(keyCode, events);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getApplicationContext(), getString(R.string.toast_something_went_wrong));
        }

        return super.onKeyDown(keyCode, events);
    }

    /**
     * Callback to success epg date load.
     */
    @Override
    public void onEpgDataLoaded() {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onEpgDataLoaded(): EpgData load/parse ok.");
        }

        startupLogo.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        this.toMainFragment();
    }

    /**
     * Callback to error epg date load.
     */
    @Override
    public void onEpgDataLoadError(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onEpgDataLoadError(): EpgData load/parse error: " + message);
        }
        Utils.showErrorToast(getApplicationContext(), getString(R.string.toast_something_went_wrong));
    }

    /**
     * Finds main fragment and opens it.
     */
    private void toMainFragment() {
        checkFragmentManager();

        // Add main fragment to fragment container
        Fragment mainFragment = fragmentManager.findFragmentByTag(MAIN_FRAGMENT);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }

        fragmentManager.beginTransaction().add(R.id.fragment_container, mainFragment, MAIN_FRAGMENT).addToBackStack(MAIN_FRAGMENT).commit();
    }

    /**
     * Returns visible fragment.
     * @return
     */
    private Fragment getVisibleFragment(){
        this.checkFragmentManager();

        // Possible multiple fragments visible because exit overlay fragment
        List<Fragment> visibleFragments = new ArrayList<>();

        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null) {
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    visibleFragments.add(fragment);
            }
        }

        Fragment visibleFragment = null;
        if (visibleFragments.size() > 1) {
            visibleFragment = fragmentManager.findFragmentByTag(EXIT_OVERLAY_FRAGMENT);
        }

        if (visibleFragment != null) {
            // return exit overlay fragment
            return visibleFragment;
        }

        return visibleFragments.get(0);
    }

    /**
     * Checks fragment manager and creates it if needed.
     */
    private void checkFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
    }

    /**
     * Creates epg data load listener.
     * @param epgDataLoadedListener
     */
    private void setEpgDataLoadedListener(EpgDataLoadedListener epgDataLoadedListener) {
        this.epgDataLoadedListener = epgDataLoadedListener;
    }
}
