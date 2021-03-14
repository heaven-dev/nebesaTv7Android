package ru.tv7.nebesa.fragments;

import android.content.Context;
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
import ru.tv7.nebesa.adapter.CategoryGridAdapter;
import ru.tv7.nebesa.helpers.PageStateItem;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.CATEGORIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY_ID;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY_NAME;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY_PROGRAMS_SEARCH_LIMIT;
import static ru.tv7.nebesa.helpers.Constants.COLON_WITH_SPACE;
import static ru.tv7.nebesa.helpers.Constants.ID;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.NO_MORE_PAGING_DATA;
import static ru.tv7.nebesa.helpers.Constants.PARENT_NAME;
import static ru.tv7.nebesa.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesa.helpers.Constants.PLAY;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_METHOD;
import static ru.tv7.nebesa.helpers.Constants.SERIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ZERO_STR;
import static ru.tv7.nebesa.helpers.PageStateItem.DATA;
import static ru.tv7.nebesa.helpers.PageStateItem.DATA_LENGTH;
import static ru.tv7.nebesa.helpers.PageStateItem.OFFSET;
import static ru.tv7.nebesa.helpers.PageStateItem.SELECTED_POS;

/**
 * Categories fragment.
 */
public class CategoriesFragment extends Fragment implements ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private JSONObject selectedCategory = null;

    private List<TextView> menuTexts = null;

    private int offset = 0;
    private int dataLength = 0;

    private boolean loadingData = false;

    private CategoryGridAdapter categoryGridAdapter = null;
    private VerticalGridView categoriesScroll = null;

    /**
     * Default constructor.
     */
    public CategoriesFragment() {

    }

    /**
     * Creates and returns a new instance of this categories fragment.
     * @return
     */
    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
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
                Log.d(LOG_TAG, "CategoriesFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onCreate(): Exception: " + e);
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
            root = inflater.inflate(R.layout.fragment_categories, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            dataLength = 0;
            offset = 0;

            selectedCategory = sharedCacheViewModel.getSelectedCategory();
            if (selectedCategory != null) {
                this.setPageTitle();

                PageStateItem pageStateItem = sharedCacheViewModel.getCategoriesPageStateItem();
                if (pageStateItem != null) {
                    Utils.showProgressBar(root, R.id.categoriesProgress);

                    this.addElements((JSONArray)pageStateItem.getValue(DATA));
                    this.scrollToPosition((Integer)pageStateItem.getValue(SELECTED_POS));
                    dataLength = (Integer)pageStateItem.getValue(DATA_LENGTH);
                    offset = (Integer)pageStateItem.getValue(OFFSET);
                }
                else {
                    this.loadCategoryPrograms(Utils.stringToInt(Utils.getValue(selectedCategory, CATEGORY_ID)));
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Creates grid and adds date to it.
     * @param jsonArray
     */
    private void addElements(JSONArray jsonArray) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.addElements(): Archive data loaded. Data length: " + jsonArray.length());
            }

            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            if (dataLength == 0) {
                // first load
                categoriesScroll = root.findViewById(R.id.categoriesScroll);
                categoryGridAdapter = new CategoryGridAdapter(getActivity(), getContext(), jsonArray);

                categoriesScroll.setAdapter(categoryGridAdapter);
            }
            else {
                // next loads
                categoryGridAdapter.addElements(jsonArray);
                categoryGridAdapter.notifyDataSetChanged();
            }

            int length = jsonArray.length();
            dataLength += length;
            offset += length;

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.addElements(): Items loaded: " + dataLength);
            }

            if (length < CATEGORY_PROGRAMS_SEARCH_LIMIT) {
                // no more data
                offset = NO_MORE_PAGING_DATA;
            }

            loadingData = false;

            Utils.hideProgressBar(root, R.id.categoriesProgress);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.addElements(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Date loaded callback.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onArchiveDataLoaded(): Archive data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, R.id.categoriesProgress);

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
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Date loaded error callback.
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
     * Handles keydown events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (categoriesScroll == null || categoryGridAdapter == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuItemSelected(Sidebar.getFocusedMenuItem(root), getActivity(), sharedCacheViewModel);
                }
                else {
                    int pos = this.getSelectedPosition();

                    JSONObject obj = categoryGridAdapter.getElementByIndex(pos);
                    if (obj != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): Selected element: " + obj.toString());
                        }

                        sharedCacheViewModel.setPageToHistory(CATEGORIES_FRAGMENT);

                        sharedCacheViewModel.setCategoriesPageStateItem(new PageStateItem(
                                categoryGridAdapter.getElements(),
                                pos,
                                dataLength,
                                offset));

                        if (this.isSeries(obj)) {
                            sharedCacheViewModel.setSelectedProgram(obj);
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
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                if (!Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
                else {
                    if (!loadingData) {
                        int pos = this.getSelectedPosition();

                        this.setSelectedPosition(++pos);

                        // paging
                        if (pos > 0 && offset != NO_MORE_PAGING_DATA && pos + CATEGORY_PROGRAMS_SEARCH_LIMIT / 2 == dataLength) {
                            loadingData = true;

                            this.loadCategoryPrograms(Utils.stringToInt(Utils.getValue(selectedCategory, CATEGORY_ID)));
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
                else {
                    int pos = this.getSelectedPosition();

                    this.setSelectedPosition(--pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    sharedCacheViewModel.resetCategoriesPageStateItem();

                    String toPage = sharedCacheViewModel.getPageFromHistory();
                    if (toPage != null) {
                        Utils.toPage(toPage, getActivity(), true, false,null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "CategoriesFragment.onKeyDown(): Exception: " + e);
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

        Utils.requestFocus(categoriesScroll);
    }

    /**
     * Calls load category programs method.
     * @param categoryId
     */
    private void loadCategoryPrograms(int categoryId) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "CategoriesFragment.loadCategoryPrograms(): Load more category programs data: " + categoryId);
        }

        Utils.showProgressBar(root, R.id.categoriesProgress);
        archiveViewModel.getCategoryPrograms(categoryId, CATEGORY_PROGRAMS_SEARCH_LIMIT, offset, this);
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, R.id.categoriesProgress);
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
        if (categoriesScroll != null) {
            int pos = categoriesScroll.getSelectedPosition();
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
        if (categoriesScroll != null) {
            categoriesScroll.setSelectedPositionSmooth(position);
        }
    }

    /**
     * Scrolls to position on grid.
     * @param position
     */
    private void scrollToPosition(int position) {
        if (categoriesScroll != null) {
            categoriesScroll.scrollToPosition(position);
        }
    }

    /**
     * Checks is item series item.
     * @param obj
     * @return
     * @throws Exception
     */
    private boolean isSeries(JSONObject obj) throws Exception {
        return obj != null && obj.has(PLAY) && obj.getString(PLAY).equals(ZERO_STR);
    }

    /**
     * Setts page title text.
     * @throws Exception
     */
    private void setPageTitle() throws Exception {
        TextView categoriesTitle = root.findViewById(R.id.categoriesTitle);
        if (categoriesTitle != null) {

            String categoryName = Utils.getValue(selectedCategory, CATEGORY_NAME);
            String parentName = Utils.getValue(selectedCategory, PARENT_NAME);
            if (categoryName != null && parentName != null && !categoryName.equals(parentName)) {
                categoryName = parentName + PIPE_WITH_SPACES + categoryName;
            }

            categoryName = getString(R.string.category) + COLON_WITH_SPACE + categoryName;
            categoriesTitle.setText(categoryName);
        }
    }
}
