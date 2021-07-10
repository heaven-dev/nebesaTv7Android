package ru.tv7.nebesa.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.DESCRIPTION;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_TEXT_ANIMATION_DURATION;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_TEXT_ANIMATION_END;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_TEXT_ANIMATION_START;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_TEXT_ANIMATION_START_OFFSET;
import static ru.tv7.nebesa.helpers.Constants.ID_NULL;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.NAME;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.SERIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SERIES_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SID;

/**
 * Series info fragment. Shows info of series and series button.
 */
public class SeriesInfoFragment extends Fragment {

    private View root = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;

    private ImageView seriesButton = null;
    private ImageView favoriteButton = null;
    private ImageView backgroundImage = null;

    private JSONObject selectedSeries = null;
    private int programFavoritesIndex = -1;

    /**
     * Default constructor.
     */
    public SeriesInfoFragment() {

    }

    /**
     * Creates and returns a new instance of this series info fragment.
     * @return
     */
    public static SeriesInfoFragment newInstance() {
        return new SeriesInfoFragment();
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
                Log.d(LOG_TAG, "SeriesInfoFragment.onCreate() called.");
            }

            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesInfoFragment.onCreate(): Exception: " + e);
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
            root = inflater.inflate(R.layout.fragment_series_info, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            selectedSeries = sharedCacheViewModel.getSelectedSeries();
            if (selectedSeries == null) {
                throw new Exception("Series info page. Not selected series passed to this fragment!");
            }

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesInfoFragment.onCreateView(): Selected program: " + selectedSeries.getString(NAME));
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            backgroundImage = root.findViewById(R.id.backgroundImage);
            if (backgroundImage != null) {
                String imagePath = Utils.getJsonStringValue(selectedSeries, IMAGE_PATH);
                if (imagePath != null && !imagePath.equals(EMPTY) && !imagePath.equals(NULL_VALUE) && !imagePath.contains(ID_NULL)) {
                    Glide.with(this).asBitmap().load(imagePath).into(backgroundImage);
                }
                else {
                    Glide.with(this).asBitmap().load(R.drawable.fallback).into(backgroundImage);
                }
            }

            seriesButton = root.findViewById(R.id.seriesButton);
            favoriteButton = root.findViewById(R.id.favoriteButton);
            if (favoriteButton != null) {
                String seriesId = Utils.getJsonStringValue(selectedSeries, SID);
                if (seriesId != null) {
                    programFavoritesIndex = Utils.isItemInFavorites(getContext(), seriesId, SID);

                    if (programFavoritesIndex != -1) {
                        this.setFavoritesButtonImage(R.drawable.favorites);
                    }
                }
            }

            Utils.requestFocus(seriesButton);

            Resources resources = getResources();
            if (resources != null) {
                String valueText;

                TextView description = root.findViewById(R.id.description);
                if (description != null) {
                    valueText = Utils.getJsonStringValue(selectedSeries, DESCRIPTION);
                    if (valueText != null && valueText.length() > 0) {
                        description.setText(valueText);
                    }
                    else {
                        description.setVisibility(View.GONE);
                    }
                }

                TextView caption = root.findViewById(R.id.caption);
                if (caption != null) {
                    valueText = Utils.getJsonStringValue(selectedSeries, CAPTION);
                    if (valueText != null && valueText.length() > 0) {
                        caption.setText(valueText);
                    }
                    else {
                        caption.setVisibility(View.GONE);
                    }
                }

                TextView seriesName = root.findViewById(R.id.seriesName);
                if (seriesName != null) {
                    valueText = Utils.getJsonStringValue(selectedSeries, NAME);
                    if (valueText != null && valueText.length() > 0) {
                        seriesName.setText(valueText);
                    }
                }
            }
            else {
                throw new Exception("Invalid input parameters!");
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesInfoFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
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
                Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    int focusedId = focusedView.getId();

                    if (focusedId == R.id.seriesButton) {
                        sharedCacheViewModel.setPageToHistory(SERIES_INFO_FRAGMENT);
                        sharedCacheViewModel.setSelectedSeries(selectedSeries);

                        Utils.toPage(SERIES_FRAGMENT, getActivity(), true, false, null);
                    }
                    else if (focusedId == R.id.favoriteButton) {
                        if (programFavoritesIndex == -1) {
                            this.addToSavedFavorites();
                            this.setFavoritesButtonImage(R.drawable.favorites);
                            this.setFavoritesPopupText(R.string.added_to_favorites);
                        }
                        else {
                            this.removeFromSavedFavorites();
                            this.setFavoritesButtonImage(R.drawable.favorites_not_selected);
                            this.setFavoritesPopupText(R.string.removed_from_favorites);
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedId == R.id.favoriteButton) {
                    Utils.requestFocus(seriesButton);
                }
                else if (focusedId == R.id.seriesButton || focusedId == R.id.backgroundImage) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else if (focusedId == R.id.seriesButton) {
                    Utils.requestFocus(favoriteButton);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage == null) {
                        toPage = ARCHIVE_MAIN_FRAGMENT;
                    }

                    Utils.toPage(toPage, getActivity(), true, false,null);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SeriesInfoFragment.onKeyDown(): Exception: " + e);
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
        Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

        Utils.requestFocus(seriesButton);
    }

    /**
     * Adds program to favorites. Saves to shared prefs.
     * @throws Exception
     */
    private void addToSavedFavorites() throws Exception {
        JSONArray jsonArray = Utils.getSavedPrefs(FAVORITES_SP_TAG, FAVORITES_SP_DEFAULT, getContext());
        if (jsonArray != null) {
            jsonArray.put(selectedSeries);

            Utils.savePrefs(FAVORITES_SP_TAG, getContext(), jsonArray);

            programFavoritesIndex = jsonArray.length() - 1;
        }
    }

    /**
     * Removes program from favorites. Saves to shared prefs.
     * @throws Exception
     */
    private void removeFromSavedFavorites() throws Exception {
        JSONArray jsonArray = Utils.getSavedPrefs(FAVORITES_SP_TAG, FAVORITES_SP_DEFAULT, getContext());
        if (jsonArray != null && programFavoritesIndex != -1) {
            jsonArray.remove(programFavoritesIndex);

            Utils.savePrefs(FAVORITES_SP_TAG, getContext(), jsonArray);

            programFavoritesIndex = - 1;
        }
    }

    /**
     * Sets favorite button image.
     * @param drawableId
     */
    private void setFavoritesButtonImage(int drawableId) {
        favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableId, null));
    }

    /**
     * Sets favorite added/removed popup text and start fade out animation.
     * @param textId
     */
    private void setFavoritesPopupText(int textId) {
        TextView addedRemovedFavorites = root.findViewById(R.id.addedRemovedFavorites);
        if (addedRemovedFavorites != null) {
            addedRemovedFavorites.setText(textId);
            addedRemovedFavorites.setVisibility(View.VISIBLE);

            Animation animation = new AlphaAnimation(FAVORITES_TEXT_ANIMATION_START, FAVORITES_TEXT_ANIMATION_END);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setStartOffset(FAVORITES_TEXT_ANIMATION_START_OFFSET);
            animation.setDuration(FAVORITES_TEXT_ANIMATION_DURATION);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    addedRemovedFavorites.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            addedRemovedFavorites.startAnimation(animation);
        }
    }
}
