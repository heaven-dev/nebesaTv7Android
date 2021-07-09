package ru.tv7.nebesa.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.NebesaTv7;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.fragments.AboutFragment;
import ru.tv7.nebesa.fragments.ArchiveMainFragment;
import ru.tv7.nebesa.fragments.ArchivePlayerFragment;
import ru.tv7.nebesa.fragments.CategoriesFragment;
import ru.tv7.nebesa.fragments.ChannelInfoFragment;
import ru.tv7.nebesa.fragments.ErrorFragment;
import ru.tv7.nebesa.fragments.ExitFragment;
import ru.tv7.nebesa.fragments.FavoritesFragment;
import ru.tv7.nebesa.fragments.GuideFragment;
import ru.tv7.nebesa.fragments.ProgramInfoFragment;
import ru.tv7.nebesa.fragments.SearchFragment;
import ru.tv7.nebesa.fragments.SearchResultFragment;
import ru.tv7.nebesa.fragments.SeriesFragment;
import ru.tv7.nebesa.fragments.TvMainFragment;
import ru.tv7.nebesa.fragments.TvPlayerFragment;
import ru.tv7.nebesa.helpers.GuideItem;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.GuideViewModel;

import static ru.tv7.nebesa.helpers.Constants.BROADCAST_DATE;
import static ru.tv7.nebesa.helpers.Constants.BROADCAST_DATE_TIME;
import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX;
import static ru.tv7.nebesa.helpers.Constants.DURATION;
import static ru.tv7.nebesa.helpers.Constants.END_DATE;
import static ru.tv7.nebesa.helpers.Constants.END_TIME;
import static ru.tv7.nebesa.helpers.Constants.EPISODE_NUMBER;
import static ru.tv7.nebesa.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.FORMATTED_END_TIME;
import static ru.tv7.nebesa.helpers.Constants.FORMATTED_START_TIME;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_DATA;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.IS_VISIBLE_ON_VOD;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.NAME;
import static ru.tv7.nebesa.helpers.Constants.PROGRESS_BAR_SIZE;
import static ru.tv7.nebesa.helpers.Constants.SERIES;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;
import static ru.tv7.nebesa.helpers.Constants.SID;
import static ru.tv7.nebesa.helpers.Constants.START_DATE;
import static ru.tv7.nebesa.helpers.Constants.START_END_TIME;
import static ru.tv7.nebesa.helpers.Constants.TIME;
import static ru.tv7.nebesa.helpers.Constants.TV_MAIN_FRAGMENT;

/**
 * Main activity.
 *  - Load epg data, shows logo and progressbar.
 *  - Opens main fragment.
 *  - Handles keydown events.
 */
public class MainActivity extends AppCompatActivity implements ArchiveDataLoadedListener {

    private FragmentManager fragmentManager = null;
    private ArchiveViewModel archiveViewModel = null;
    private GuideViewModel guidViewModel = null;

    private ArchiveDataLoadedListener guideDataLoadedListener = null;

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

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            fragmentManager = Utils.getFragmentManager(this);

            archiveViewModel = ViewModelProviders.of(this).get(ArchiveViewModel.class);
            guidViewModel = ViewModelProviders.of(this).get(GuideViewModel.class);

            this.setGuideByDateDataLoadedListener(this);

            setContentView(R.layout.activity_main);

            fragmentContainer = findViewById(R.id.fragment_container);
            startupLogo = findViewById(R.id.startupLogo);

            progressBar = findViewById(R.id.startupProgressBar);
            progressBar.setScaleY(PROGRESS_BAR_SIZE);
            progressBar.setScaleX(PROGRESS_BAR_SIZE);

            NebesaTv7.getInstance().setActivity(this);

            archiveViewModel.getGuideByDate(Utils.getTodayUtcFormattedLocalDate(), 0, this);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(this);
        }
    }

    /**
     * Handles key down events and sends events to visible fragment.
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

            Fragment fragment = this.getVisibleFragment();
            if (fragment != null) {
                if (fragment instanceof TvMainFragment) {
                    // TV main fragment visible
                    return ((TvMainFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ArchiveMainFragment) {
                    // Archive main fragment visible
                    return ((ArchiveMainFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof TvPlayerFragment) {
                    // Video player fragment visible
                    return ((TvPlayerFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ArchivePlayerFragment) {
                    // Archive player fragment visible
                    return ((ArchivePlayerFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ProgramInfoFragment) {
                    // Program info fragment visible
                    return ((ProgramInfoFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof CategoriesFragment) {
                    // Categories fragment visible
                    return ((CategoriesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SeriesFragment) {
                    // Series fragment visible
                    return ((SeriesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof GuideFragment) {
                    // Guide fragment visible
                    return ((GuideFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SearchFragment) {
                    // Search fragment visible
                    return ((SearchFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof SearchResultFragment) {
                    // Search result fragment visible
                    return ((SearchResultFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof FavoritesFragment) {
                    // Favorites fragment visible
                    return ((FavoritesFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ChannelInfoFragment) {
                    // Channel info fragment visible
                    return ((ChannelInfoFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof AboutFragment) {
                    // About fragment visible
                    return ((AboutFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ErrorFragment) {
                    // Error fragment visible
                    return ((ErrorFragment) fragment).onKeyDown(keyCode, events);
                }
                else if (fragment instanceof ExitFragment) {
                    // Exit overlay fragment visible
                    return ((ExitFragment) fragment).onKeyDown(keyCode, events);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(this);
        }

        return super.onKeyDown(keyCode, events);
    }

    /**
     * Remote home button pressed. If playback ongoing - stop it.
     */
    @Override
    public void onUserLeaveHint()
    {
        super.onUserLeaveHint();

        Fragment fragment = this.getVisibleFragment();
        if (fragment != null) {
            if (fragment instanceof TvPlayerFragment) {
                ((TvPlayerFragment) fragment).onHomeButtonPressed();
            }
            else if (fragment instanceof ArchivePlayerFragment) {
                // Archive player fragment visible
                ((ArchivePlayerFragment) fragment).onHomeButtonPressed();
            }
        }
    }

    /**
     * Callback to success guide by date load.
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onArchiveDataLoaded(): EpgData load/parse ok.");
            }

            if (jsonArray != null && jsonArray.length() == 1) {
                JSONObject obj = jsonArray.getJSONObject(0);
                if (obj != null) {
                    JSONArray guideData = obj.getJSONArray(GUIDE_DATA);
                    if (obj.getInt(DATE_INDEX) == 0) {
                        this.addGuideData(guideData);

                        archiveViewModel.getGuideByDate(Utils.getTomorrowUtcFormattedLocalDate(), 1, this);
                    }
                    else {
                        this.addGuideData(guideData);

                        this.prepareUi();
                        Utils.toPage(TV_MAIN_FRAGMENT, this, false, false, null);
                    }
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "MainActivity.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(this);
        }
    }

    /**
     * Callback to error guide by date load.
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onArchiveDataLoadError(): EpgData load/parse error: " + message);
        }

        this.prepareUi();

        Utils.toErrorPage(this);
    }

    /**
     * Callback to network error guide by date load.
     */
    @Override
    public void onNetworkError(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "MainActivity.onNetworkError(): ***Network error!***");
        }

        this.prepareUi();

        Utils.toErrorPage(this);
    }

    /**
     * Adds guide data to view model.
     * @param guideData
     * @throws Exception
     */
    private void addGuideData(JSONArray guideData) throws  Exception {
        if (guideData == null) {
            Utils.toErrorPage(this);
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

                guidViewModel.addItemToGuide(g);
            }
        }
    }

    /**
     * Hides logo and progressbar. Shows fragment container.
     */
    private void prepareUi() {
        startupLogo.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Returns visible fragment.
     * @return
     */
    private Fragment getVisibleFragment(){
        // Possible multiple fragments visible because exit overlay fragment
        List<Fragment> visibleFragments = new ArrayList<>();

        if (fragmentManager != null) {
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
        }

        return visibleFragments.size() > 0 ? visibleFragments.get(0) : null;
    }

    /**
     * Creates epg data load listener.
     * @param guideDataLoadedListener
     */
    private void setGuideByDateDataLoadedListener(ArchiveDataLoadedListener guideDataLoadedListener) {
        this.guideDataLoadedListener = guideDataLoadedListener;
    }
}
