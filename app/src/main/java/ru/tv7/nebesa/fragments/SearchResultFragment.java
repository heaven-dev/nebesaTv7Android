package ru.tv7.nebesa.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.adapter.SearchResultGridAdapter;
import ru.tv7.nebesa.helpers.PageStateItem;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ID;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_METHOD;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_RESULT_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SERIES;
import static ru.tv7.nebesa.helpers.Constants.SERIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.TYPE;
import static ru.tv7.nebesa.helpers.PageStateItem.DATA;
import static ru.tv7.nebesa.helpers.PageStateItem.SELECTED_POS;

/**
 * Search result fragment. Shows info of program and possible play video button.
 */
public class SearchResultFragment extends Fragment implements ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private SearchResultGridAdapter searchResultGridAdapter = null;
    private VerticalGridView searchResultScroll = null;

    private int hitCount = 0;

    private List<TextView> menuTexts = null;

    /**
     * Default constructor.
     */
    public SearchResultFragment() {

    }

    /**
     * Creates and returns a new instance of this search result fragment.
     * @return
     */
    public static SearchResultFragment newInstance() {
        return new SearchResultFragment();
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
                Log.d(LOG_TAG, "SearchResultFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.onCreate(): Exception: " + e);
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
            root = inflater.inflate(R.layout.fragment_search_result, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.searchMenuContainer);

            PageStateItem pageStateItem = sharedCacheViewModel.getSearchResultPageStateItem();
            if (pageStateItem != null) {
                Utils.showProgressBar(root, R.id.searchResultProgress);

                this.addElements((JSONArray)pageStateItem.getValue(DATA));
                this.scrollToPosition((Integer)pageStateItem.getValue(SELECTED_POS));
            }
            else {
                String searchString = sharedCacheViewModel.getSearchString();
                if (searchString != null) {
                    this.loadSearchResults(searchString);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Creates grid and add data to it.
     * @param jsonArray
     */
    private void addElements(JSONArray jsonArray) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.addElements(): Archive data loaded. Data length: " + jsonArray.length());
            }

            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            hitCount = jsonArray.length();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.addElements(): Search items loaded: " + hitCount);
            }

            searchResultScroll = root.findViewById(R.id.searchResultScroll);
            searchResultGridAdapter = new SearchResultGridAdapter(getActivity(), getContext(), jsonArray);
            searchResultScroll.setAdapter(searchResultGridAdapter);

            if (jsonArray.length() == 0) {
                Utils.requestFocusById(root, R.id.searchResultTitle);

                TextView noHitsText = root.findViewById(R.id.noHitsText);
                if (noHitsText != null) {
                    noHitsText.setVisibility(View.VISIBLE);
                }
            }

            Utils.hideProgressBar(root, R.id.searchResultProgress);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.addElements(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Data loaded callback method.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.onArchiveDataLoaded(): Archive data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, R.id.searchResultProgress);

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedProgram(obj);

                        Utils.toPage(PROGRAM_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else {
                this.addElements(jsonArray);
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Data error loaded method.
     * @param message
     * @param type
     */
    @Override
    public void onArchiveDataLoadError(String message, String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - Error message: " + message);
        }

        Utils.toErrorPage(getActivity());
    }

    /**
     * Archive data load network error response.
     * @param type
     */
    @Override
    public void onNetworkError(String type) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Archive data load error. Type: " + type + " - ***Network error!***");
        }

        Utils.toErrorPage(getActivity());
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
                Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (searchResultScroll == null || searchResultGridAdapter == null) {
                return false;
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    if (hitCount == 0) {
                        return false;
                    }

                    sharedCacheViewModel.setPageToHistory(SEARCH_RESULT_FRAGMENT);

                    int pos = this.getSelectedPosition();

                    JSONObject obj = searchResultGridAdapter.getElementByIndex(pos);
                    if (obj != null) {
                        sharedCacheViewModel.setSearchResultPageStateItem(new PageStateItem(
                                searchResultGridAdapter.getElements(),
                                pos));

                        sharedCacheViewModel.setSelectedProgram(obj);

                        if (this.isSeries(obj)) {
                            Utils.toPage(SERIES_FRAGMENT, getActivity(), true, false,null);
                        }
                        else {
                            this.loadProgramInfo(obj);
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedId == R.id.searchResultContainer || focusedId == R.id.searchResultTitle) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.searchMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.searchMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(++pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.searchMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(--pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.resetSearchResultPageStateItem();

                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage != null) {
                        Utils.toPage(toPage, getActivity(), true, false,null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchResultFragment.onKeyDown(): Exception: " + e);
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
        Sidebar.setSelectedMenuItem(root, R.id.searchMenuContainer);

        if (hitCount > 0)  {
            Utils.requestFocusById(root, R.id.searchResultScroll);
        }
        else {
            Utils.requestFocusById(root, R.id.searchResultTitle);
        }
    }

    /**
     * Calls search method.
     * @param searchString
     */
    private void loadSearchResults(String searchString) {
        Utils.showProgressBar(root, R.id.searchResultProgress);
        archiveViewModel.searchItemsByString(searchString, this);
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, R.id.searchResultProgress);
        String programId = Utils.getValue(obj, ID);
        if (programId != null) {
            archiveViewModel.getProgramInfo(programId, this);
        }
    }

    /**
     * Returns selected position from grid.
     * @return
     */
    private int getSelectedPosition() {
        if (searchResultScroll != null) {
            int pos = searchResultScroll.getSelectedPosition();
            if (pos < 0) {
                pos = 0;
            }
            return pos;
        }
        return 0;
    }

    /**
     * Sets selected position to grid.
     * @param position
     */
    private void setSelectedPosition(int position) {
        if (searchResultScroll != null) {
            searchResultScroll.setSelectedPositionSmooth(position);
            this.refresh();
        }
    }

    /**
     * Scrolls to given position on grid.
     * @param position
     */
    private void scrollToPosition(int position) {
        if (searchResultScroll != null) {
            searchResultScroll.scrollToPosition(position);
            this.refresh();
        }
    }

    /**
     * Refresh layout.
     */
    private void refresh() {
        if (searchResultScroll != null) {
            searchResultScroll.invalidate();
            searchResultScroll.requestLayout();
        }
    }

    /**
     * Checks is item series or not.
     * @param obj
     * @return
     * @throws Exception
     */
    private boolean isSeries(JSONObject obj) throws Exception {
        return obj != null && obj.has(TYPE) && obj.getString(TYPE).equals(SERIES);
    }
}
