package ru.tv7.nebesa.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.leanback.widget.HorizontalGridView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.adapter.ArchiveMainCategoryGridAdapter;
import ru.tv7.nebesa.adapter.ArchiveMainProgramGridAdapter;
import ru.tv7.nebesa.adapter.ArchiveMainSeriesGridAdapter;
import ru.tv7.nebesa.helpers.ArchiveMainPageStateItem;
import ru.tv7.nebesa.helpers.GuideItem;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.interfaces.ArchiveDataLoadedListener;
import ru.tv7.nebesa.model.ArchiveViewModel;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_LANGUAGE;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_CONTENT_ROW_IDS;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_NO_SEL_POS;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_ROW_COUNT;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_SCROLL_Y_DURATION;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_TITLE_HEIGHT;
import static ru.tv7.nebesa.helpers.Constants.BACK_TEXT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX_DAY_BEFORE_YESTERDAY;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX_TODAY;
import static ru.tv7.nebesa.helpers.Constants.DATE_INDEX_YESTERDAY;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_COUNT;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_FIVE;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_FIVE_ID;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_FOUR;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_FOUR_ID;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_ONE;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_ONE_ID;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_THREE;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_THREE_ID;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_TWO;
import static ru.tv7.nebesa.helpers.Constants.DYNAMIC_ROW_TWO_ID;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_DATA;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_DATE_METHOD;
import static ru.tv7.nebesa.helpers.Constants.RECOMMENDED_PROGRAMS_LIMIT;
import static ru.tv7.nebesa.helpers.Constants.BROADCAST_RECOMMENDATIONS_METHOD;
import static ru.tv7.nebesa.helpers.Constants.CATEGORIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORIES_ROW_ID;
import static ru.tv7.nebesa.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ID;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.MOST_VIEWED_METHOD;
import static ru.tv7.nebesa.helpers.Constants.MOST_VIEWED_ROW_ID;
import static ru.tv7.nebesa.helpers.Constants.NAME;
import static ru.tv7.nebesa.helpers.Constants.NEWEST_LIMIT;
import static ru.tv7.nebesa.helpers.Constants.NEWEST_METHOD;
import static ru.tv7.nebesa.helpers.Constants.NEWEST_ROW_ID;
import static ru.tv7.nebesa.helpers.Constants.PARENT_CATEGORIES_METHOD;
import static ru.tv7.nebesa.helpers.Constants.PARENT_NAME;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_METHOD;
import static ru.tv7.nebesa.helpers.Constants.RECOMMENDATIONS_METHOD;
import static ru.tv7.nebesa.helpers.Constants.RECOMMENDATIONS_ROW_ID;
import static ru.tv7.nebesa.helpers.Constants.SCROLL_Y;
import static ru.tv7.nebesa.helpers.Constants.SERIES_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SERIES_INFO_METHOD;
import static ru.tv7.nebesa.helpers.Constants.SERIES_METHOD;
import static ru.tv7.nebesa.helpers.Constants.SERIES_ROW_ID;
import static ru.tv7.nebesa.helpers.Constants.SID;
import static ru.tv7.nebesa.helpers.Constants.SUB_CATEGORIES_METHOD;
import static ru.tv7.nebesa.helpers.Constants.TOOLBAR_HEIGHT;

/**
 * Archive main fragment.
 */
public class ArchiveMainFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, ArchiveDataLoadedListener {

    private View root = null;
    private ArchiveViewModel archiveViewModel = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private HorizontalGridView recommendScroll = null;
    private HorizontalGridView mostViewedScroll = null;
    private HorizontalGridView newestScroll = null;
    private HorizontalGridView categoriesScroll = null;
    private HorizontalGridView topicalSeriesScroll = null;
    private HorizontalGridView dynamicRowOneScroll = null;
    private HorizontalGridView dynamicRowTwoScroll = null;
    private HorizontalGridView dynamicRowThreeScroll = null;
    private HorizontalGridView dynamicRowFourScroll = null;
    private HorizontalGridView dynamicRowFiveScroll = null;

    private JSONArray visibleSubCategories = null;

    private Map<Integer, Integer> colFocusWas = new HashMap<>();
    private int focusedRow = RECOMMENDATIONS_ROW_ID;

    private List<TextView> menuTexts = null;

    /**
     * Default constructor.
     */
    public ArchiveMainFragment() { }

    /**
     * Creates and return a new instance of archive main fragment class.
     * @return
     */
    public static ArchiveMainFragment newInstance() {
        return new ArchiveMainFragment();
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
                Log.d(LOG_TAG, "ArchiveMainFragment.onCreate() called.");
            }

            archiveViewModel = ViewModelProviders.of(requireActivity()).get(ArchiveViewModel.class);
            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);

            FragmentManager fragmentManager = Utils.getFragmentManager(getActivity());
            if (fragmentManager != null) {
                fragmentManager.addOnBackStackChangedListener(this);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * onCreateView() - Android lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.onCreateView() called.");
            }

            root = inflater.inflate(R.layout.fragment_archive_main, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);

            Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

            this.calculateAndSetContentRowHeight(DYNAMIC_ROW_COUNT);

            this.loadRecommendedPrograms();
            this.loadMostViewedPrograms();
            this.loadNewestPrograms();
            this.loadCategories(true);
            this.loadCategories(false);
            this.loadGuideData(DATE_INDEX_YESTERDAY);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Back stack changed listener. Called when the user back the exit overlay fragment to this fragment.
     */
    @Override
    public void onBackStackChanged() {
        View view = getView();
        String exitFragment = sharedCacheViewModel.getExitFragment();

        if (view != null && view.getId() == R.id.archiveMainFragment && exitFragment != null && exitFragment.equals(ARCHIVE_MAIN_FRAGMENT)) {
            Utils.requestFocusById(root, R.id.archiveMenuContainer);

            view.post(new Runnable() {
                @Override
                public void run() {
                    restorePageState(focusedRow);
                }
            });
        }
    }

    /**
     * onDestroy() - Android lifecycle method.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Restores page state in case of user back to page.
     * @param row
     * @throws Exception
     */
    private void restorePageState(int row) {
        try {
            ArchiveMainPageStateItem pageStateItem = sharedCacheViewModel.getArchiveMainPageStateItem();
            if (pageStateItem != null) {
                focusedRow = pageStateItem.getActiveRow();
                colFocusWas = pageStateItem.getColFocus();

                if (row == CATEGORIES_ROW_ID) {
                    visibleSubCategories = pageStateItem.getVisibleSubCategories();
                    if (visibleSubCategories != null) {
                        this.addCategories(visibleSubCategories, SUB_CATEGORIES_METHOD, true, false);

                        String titleText = Utils.getJsonStringValue(visibleSubCategories.getJSONObject(0), PARENT_NAME);
                        this.setCategoriesText(titleText);
                    }
                }

                if (row == focusedRow) {
                    this.scrollTo(focusedRow, false);

                    int pos = pageStateItem.getSelectedPos();
                    this.setFocusToColumn(pos);
                }
                else {
                    Integer col = colFocusWas.get(row);
                    if (col != null) {
                        this.scrollRowToColumn(row, col);
                    }
                }
            }
            else if (row == RECOMMENDATIONS_ROW_ID) {
                this.setFocusToColumn(0);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.restorePageState(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Creates and adds data to grids.
     * @param jsonArray
     * @param type
     * @throws Exception
     */
    private void addElements(JSONArray jsonArray, String type) {
        Context context = getContext();

        if (type.equals(BROADCAST_RECOMMENDATIONS_METHOD) || type.equals(RECOMMENDATIONS_METHOD)) {
            recommendScroll = root.findViewById(R.id.recommendScroll);
            recommendScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recommendScroll != null) {
                        recommendScroll.invalidate();
                        recommendScroll.requestLayout();
                    }
                }
            });

            ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(getActivity(), context, jsonArray);
            recommendScroll.setAdapter(adapter);

            // Change row background to white
            this.setRowWhiteBackground(context, R.id.recommendContainer);

            this.restorePageState(RECOMMENDATIONS_ROW_ID);
            Utils.hideProgressBar(root, R.id.recommendProgress);
        }
        else if(type.equals(MOST_VIEWED_METHOD)) {
            mostViewedScroll = root.findViewById(R.id.mostViewedScroll);
            mostViewedScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mostViewedScroll != null) {
                        mostViewedScroll.invalidate();
                        mostViewedScroll.requestLayout();
                    }
                }
            });

            ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(getActivity(), context, jsonArray);
            mostViewedScroll.setAdapter(adapter);

            // Change row background to white
            this.setRowWhiteBackground(context, R.id.mostViewedContainer);

            this.restorePageState(MOST_VIEWED_ROW_ID);
            Utils.hideProgressBar(root, R.id.mostViewedProgress);
        }
        else if(type.equals(NEWEST_METHOD)) {
            newestScroll = root.findViewById(R.id.newestScroll);
            newestScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (newestScroll != null) {
                        newestScroll.invalidate();
                        newestScroll.requestLayout();
                    }
                }
            });

            ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(getActivity(), context, jsonArray);
            newestScroll.setAdapter(adapter);

            // Change row background to white
            this.setRowWhiteBackground(context, R.id.newestContainer);

            this.restorePageState(NEWEST_ROW_ID);
            Utils.hideProgressBar(root, R.id.newestProgress);
        }
        else if(type.equals(PARENT_CATEGORIES_METHOD)) {
            this.addCategories(jsonArray, type, true, true);
        }
    }

    /**
     * Creates and adds data to grids.
     * @param jsonArray
     * @param type
     * @throws Exception
     */
    private void addGuideItemElements(JSONArray jsonArray, String type) throws Exception {
        Context context = getContext();

        if(type.equals(SERIES_METHOD)) {
            topicalSeriesScroll = root.findViewById(R.id.topicalSeriesScroll);
            topicalSeriesScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (topicalSeriesScroll != null) {
                        topicalSeriesScroll.invalidate();
                        topicalSeriesScroll.requestLayout();
                    }
                }
            });

            ArchiveMainSeriesGridAdapter adapter = new ArchiveMainSeriesGridAdapter(getActivity(), context, jsonArray);
            topicalSeriesScroll.setAdapter(adapter);

            // Change row background to white
            this.setRowWhiteBackground(context, R.id.topicalSeriesContainer);

            this.restorePageState(SERIES_ROW_ID);
            Utils.hideProgressBar(root, R.id.topicalSeriesProgress);

            this.addDynamicRowElements();
        }
    }

    /**
     * Add dynamic row elements.
     * @throws Exception
     */
    private void addDynamicRowElements() throws Exception {
        Context context = getContext();
        FragmentActivity activity = getActivity();

        // Row one
        JSONArray rowOneData = archiveViewModel.getDynamicDataRow(DYNAMIC_ROW_ONE);
        if (rowOneData != null) {
            JSONObject obj = rowOneData.getJSONObject(0);
            if (obj != null) {
                this.setDynamicRowVisibility(Utils.getJsonStringValue(obj, CATEGORY), R.id.dynamicRowOneText, R.id.dynamicRowOneContainer);

                dynamicRowOneScroll = root.findViewById(R.id.dynamicRowOneScroll);
                dynamicRowOneScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dynamicRowOneScroll != null) {
                            dynamicRowOneScroll.invalidate();
                            dynamicRowOneScroll.requestLayout();
                        }
                    }
                });

                ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(activity, context, rowOneData);
                dynamicRowOneScroll.setAdapter(adapter);

                // Change row background to white
                this.setRowWhiteBackground(context, R.id.dynamicRowOneContainer);

                this.restorePageState(DYNAMIC_ROW_ONE_ID);
                Utils.hideProgressBar(root, R.id.dynamicRowOneProgress);
            }
        }

        // Row two
        JSONArray rowTwoData = archiveViewModel.getDynamicDataRow(DYNAMIC_ROW_TWO);
        if (rowTwoData != null) {
            JSONObject obj = rowTwoData.getJSONObject(0);
            if (obj != null) {
                this.setDynamicRowVisibility(Utils.getJsonStringValue(obj, CATEGORY), R.id.dynamicRowTwoText, R.id.dynamicRowTwoContainer);

                dynamicRowTwoScroll = root.findViewById(R.id.dynamicRowTwoScroll);
                dynamicRowTwoScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dynamicRowTwoScroll != null) {
                            dynamicRowTwoScroll.invalidate();
                            dynamicRowTwoScroll.requestLayout();
                        }
                    }
                });

                ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(activity, context, rowTwoData);
                dynamicRowTwoScroll.setAdapter(adapter);

                // Change row background to white
                this.setRowWhiteBackground(context, R.id.dynamicRowTwoContainer);

                this.restorePageState(DYNAMIC_ROW_TWO_ID);
                Utils.hideProgressBar(root, R.id.dynamicRowTwoProgress);
            }
        }

        // Row three
        JSONArray rowThreeData = archiveViewModel.getDynamicDataRow(DYNAMIC_ROW_THREE);
        if (rowThreeData != null) {
            JSONObject obj = rowThreeData.getJSONObject(0);
            if (obj != null) {
                this.setDynamicRowVisibility(Utils.getJsonStringValue(obj, CATEGORY), R.id.dynamicRowThreeText, R.id.dynamicRowThreeContainer);

                dynamicRowThreeScroll = root.findViewById(R.id.dynamicRowThreeScroll);
                dynamicRowThreeScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dynamicRowThreeScroll != null) {
                            dynamicRowThreeScroll.invalidate();
                            dynamicRowThreeScroll.requestLayout();
                        }
                    }
                });

                ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(activity, context, rowThreeData);
                dynamicRowThreeScroll.setAdapter(adapter);

                // Change row background to white
                this.setRowWhiteBackground(context, R.id.dynamicRowThreeContainer);

                this.restorePageState(DYNAMIC_ROW_THREE_ID);
                Utils.hideProgressBar(root, R.id.dynamicRowThreeProgress);
            }
        }

        // Row four
        JSONArray rowFourData = archiveViewModel.getDynamicDataRow(DYNAMIC_ROW_FOUR);
        if (rowFourData != null) {
            JSONObject obj = rowFourData.getJSONObject(0);
            if (obj != null) {
                this.setDynamicRowVisibility(Utils.getJsonStringValue(obj, CATEGORY), R.id.dynamicRowFourText, R.id.dynamicRowFourContainer);

                dynamicRowFourScroll = root.findViewById(R.id.dynamicRowFourScroll);
                dynamicRowFourScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dynamicRowFourScroll != null) {
                            dynamicRowFourScroll.invalidate();
                            dynamicRowFourScroll.requestLayout();
                        }
                    }
                });

                ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(activity, context, rowFourData);
                dynamicRowFourScroll.setAdapter(adapter);

                // Change row background to white
                this.setRowWhiteBackground(context, R.id.dynamicRowFourContainer);

                this.restorePageState(DYNAMIC_ROW_FOUR_ID);
                Utils.hideProgressBar(root, R.id.dynamicRowFourProgress);
            }
        }

        // Row five
        JSONArray rowFiveData = archiveViewModel.getDynamicDataRow(DYNAMIC_ROW_FIVE);
        if (rowFiveData != null) {
            JSONObject obj = rowFiveData.getJSONObject(0);
            if (obj != null) {
                this.setDynamicRowVisibility(Utils.getJsonStringValue(obj, CATEGORY), R.id.dynamicRowFiveText, R.id.dynamicRowFiveContainer);

                dynamicRowFiveScroll = root.findViewById(R.id.dynamicRowFiveScroll);
                dynamicRowFiveScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dynamicRowFiveScroll != null) {
                            dynamicRowFiveScroll.invalidate();
                            dynamicRowFiveScroll.requestLayout();
                        }
                    }
                });

                ArchiveMainProgramGridAdapter adapter = new ArchiveMainProgramGridAdapter(activity, context, rowFiveData);
                dynamicRowFiveScroll.setAdapter(adapter);

                // Change row background to white
                this.setRowWhiteBackground(context, R.id.dynamicRowFiveContainer);

                this.restorePageState(DYNAMIC_ROW_FIVE_ID);
                Utils.hideProgressBar(root, R.id.dynamicRowFiveProgress);
            }
        }
    }

    /**
     * Sets dynamic row visibility and category text.
     * @param category
     * @param textViewId
     * @param relativeLayoutId
     */
    private void setDynamicRowVisibility(String category, int textViewId, int relativeLayoutId) {
        TextView tv = root.findViewById(textViewId);
        if (tv != null) {
            tv.setText(category);
            tv.setVisibility(View.VISIBLE);
        }

        RelativeLayout rl = root.findViewById(relativeLayoutId);
        if (rl != null) {
            rl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Creates categories grid and adds data to grid.
     * @param jsonArray
     * @param type
     * @param isPageLoad
     * @param restorePageState
     * @throws Exception
     */
    private void addCategories(JSONArray jsonArray, String type, boolean isPageLoad, boolean restorePageState) {
        Context context = getContext();

        categoriesScroll = root.findViewById(R.id.categoriesScroll);
        categoriesScroll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (categoriesScroll != null) {
                    categoriesScroll.invalidate();
                    categoriesScroll.requestLayout();
                }
            }
        });

        ArchiveMainCategoryGridAdapter adapter = new ArchiveMainCategoryGridAdapter(getActivity(), context, jsonArray, this.getContentRowHeight());
        categoriesScroll.setAdapter(adapter);

        // Change row background to white
        this.setRowWhiteBackground(context, R.id.categoriesContainer);

        if (!isPageLoad) {
            Utils.fadePageAnimation(categoriesScroll);
        }

        if (restorePageState) {
            this.restorePageState(CATEGORIES_ROW_ID);
        }
    }

    /**
     * Archive data load response.
     * @param jsonArray
     * @param type
     */
    @Override
    public void onArchiveDataLoaded(JSONArray jsonArray, String type) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "Archive data loaded. Type: " + type);
            }

            if (type.equals(PROGRAM_INFO_METHOD)) {
                Utils.hideProgressBar(root, this.getProgressBarByRow());

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedProgram(obj);

                        Utils.toPage(PROGRAM_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else if (type.equals(GUIDE_DATE_METHOD)) {
                Utils.hideProgressBar(root, this.getProgressBarByRow());

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null && obj.getInt(DATE_INDEX) == -1) {
                        JSONArray guideData = obj.getJSONArray(GUIDE_DATA);

                        this.addGuideData(guideData);

                        this.loadGuideData(DATE_INDEX_DAY_BEFORE_YESTERDAY);
                    }
                    else if (obj != null && obj.getInt(DATE_INDEX) == -2) {
                        JSONArray guideData = obj.getJSONArray(GUIDE_DATA);

                        this.addGuideData(guideData);

                        JSONArray seriesData = archiveViewModel.getSeriesData();
                        if (seriesData == null) {
                            archiveViewModel.initializeSeriesData(archiveViewModel.getFourDaysGuide());

                            if (!archiveViewModel.isDynamicRowsInitialized()) {
                                archiveViewModel.initializeDynamicData();
                                this.calculateAndSetContentRowHeight(archiveViewModel.getDynamicRowCount());
                            }

                            seriesData = archiveViewModel.getSeriesData();
                        }

                        this.addGuideItemElements(seriesData, SERIES_METHOD);
                    }
                }
            }
            else if (type.equals(SERIES_INFO_METHOD)) {
                Utils.hideProgressBar(root, this.getProgressBarByRow());

                if (jsonArray != null && jsonArray.length() == 1) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (obj != null) {
                        sharedCacheViewModel.setSelectedSeries(obj);

                        Utils.toPage(SERIES_INFO_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
            else if (type.equals(RECOMMENDATIONS_METHOD)) {
                if (jsonArray != null && jsonArray.length() <= 4) {
                    archiveViewModel.getBroadcastRecommendationPrograms(Utils.getUtcFormattedLocalDate(0), RECOMMENDED_PROGRAMS_LIMIT, 0, this);
                }
                else {
                    archiveViewModel.clearBroadcastRecommendations();
                    this.addElements(jsonArray, type);
                }
            }
            else {
                this.addElements(jsonArray, type);
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.onArchiveDataLoaded(): Exception: " + e);
            }

            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * Archive data load error response.
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
                Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): keyCode: " + keyCode);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.archiveMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else {
                    int pos = this.getSelectedPosition();
                    if (pos == ARCHIVE_MAIN_NO_SEL_POS) {
                        return false;
                    }

                    if (focusedRow == RECOMMENDATIONS_ROW_ID || focusedRow == MOST_VIEWED_ROW_ID || focusedRow == NEWEST_ROW_ID
                            || focusedRow == DYNAMIC_ROW_ONE_ID || focusedRow == DYNAMIC_ROW_TWO_ID || focusedRow == DYNAMIC_ROW_THREE_ID
                            || focusedRow == DYNAMIC_ROW_FOUR_ID || focusedRow == DYNAMIC_ROW_FIVE_ID) {
                        JSONObject program = this.getProgramByIndex(pos);
                        if (program != null) {
                            sharedCacheViewModel.setPageToHistory(ARCHIVE_MAIN_FRAGMENT);

                            this.cachePageState();

                            this.loadProgramInfo(program);
                        }
                    }
                    else if (focusedRow == SERIES_ROW_ID) {
                        JSONObject obj = archiveViewModel.getSeriesByIndex(pos);
                        if (obj != null) {
                            sharedCacheViewModel.setPageToHistory(ARCHIVE_MAIN_FRAGMENT);

                            this.cachePageState();

                            this.loadSeriesInfo(obj);
                        }
                    }
                    else {
                        // categories selection
                        JSONArray result = null;
                        if (visibleSubCategories == null) {
                            result = archiveViewModel.hasSubCategories(pos);
                            if (result != null) {
                                if (result.length() > 1) {
                                    result = this.addBackItem(result);

                                    this.addCategories(result, SUB_CATEGORIES_METHOD, false, false);
                                    this.setCategoriesTextByIndex(pos);

                                    setFocusToColumn(0);
                                    visibleSubCategories = result;

                                    colFocusWas.put(CATEGORIES_ROW_ID, 0);
                                }
                                else {
                                    JSONObject obj = result.getJSONObject(0);
                                    if (obj != null) {
                                        this.toCategoriesPage(obj);
                                    }
                                }
                            }
                        }
                        else {
                            JSONObject obj = visibleSubCategories.getJSONObject(pos);
                            if (obj != null) {
                                if (Utils.getJsonStringValue(obj, BACK_TEXT) != null) {

                                    result = archiveViewModel.getParentCategories();
                                    if (result != null) {
                                        this.addCategories(result, PARENT_CATEGORIES_METHOD, false, false);
                                        this.setCategoriesText(String.valueOf(getText(R.string.categories)));

                                        setFocusToColumn(0);
                                        visibleSubCategories = null;

                                        colFocusWas.put(CATEGORIES_ROW_ID, 0);
                                    }
                                }
                                else {
                                    this.toCategoriesPage(obj);
                                }
                            }
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int pos = this.getSelectedPosition();
                if (pos == ARCHIVE_MAIN_NO_SEL_POS) {
                    return false;
                }

                if (pos > 0) {
                    setSelectedPosition(--pos);
                    colFocusWas.put(focusedRow, pos);
                }
                else {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.archiveMenuContainer);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    int pos = this.getSelectedPosition();
                    if (pos == ARCHIVE_MAIN_NO_SEL_POS) {
                        return false;
                    }

                    setSelectedPosition(++pos);
                    colFocusWas.put(focusedRow, pos);
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.archiveMenuContainer);
                }
                else {
                    if (focusedRow < ARCHIVE_MAIN_ROW_COUNT + archiveViewModel.getDynamicRowCount() - 1) {
                        focusedRow++;

                        if (focusedRow > 1) {
                            this.scrollTo(focusedRow, true);
                        }

                        Integer col = colFocusWas.get(focusedRow);
                        setFocusToColumn(col != null ? col : 0);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.archiveMenuContainer);
                }
                else {
                    if (focusedRow > 0) {
                        focusedRow--;

                        if (focusedRow > 0) {
                            this.scrollTo(focusedRow, true);
                        }

                        Integer col = colFocusWas.get(focusedRow);
                        setFocusToColumn(col != null ? col : 0);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    this.cachePageState();

                    sharedCacheViewModel.setPageToHistory(ARCHIVE_MAIN_FRAGMENT);
                    sharedCacheViewModel.setExitFragment(ARCHIVE_MAIN_FRAGMENT);

                    Utils.toPage(EXIT_OVERLAY_FRAGMENT, getActivity(), false, false, null);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveMainFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Adds guide data to view model.
     * @param guideData
     * @throws Exception
     */
    private void addGuideData(JSONArray guideData) throws Exception {
        if (guideData == null) {
            Utils.toErrorPage(getActivity());
        }

        List<GuideItem> guideList = new ArrayList<>();

        for (int i = 0; i < guideData.length(); i++) {
            JSONObject obj = guideData.getJSONObject(i);
            if (obj != null) {
                guideList.add(Utils.getGuideItemByJsonObj(obj));
            }
        }

        archiveViewModel.addGuideData(guideList, false);
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts, root);
        Sidebar.setSelectedMenuItem(root, R.id.archiveMenuContainer);

        setFocusToColumn(0);
    }

    /**
     * Returns scroll view base on given row.
     * @return
     */
    private HorizontalGridView getScrollView(int row) {
        HorizontalGridView grid = null;
        if (row == RECOMMENDATIONS_ROW_ID) {
            grid = recommendScroll;
        }
        else if (row == MOST_VIEWED_ROW_ID) {
            grid = mostViewedScroll;
        }
        else if (row == NEWEST_ROW_ID) {
            grid = newestScroll;
        }
        else if (row == CATEGORIES_ROW_ID) {
            grid = categoriesScroll;
        }
        else if (row == SERIES_ROW_ID) {
            grid = topicalSeriesScroll;
        }
        else if (row == DYNAMIC_ROW_ONE_ID) {
            grid = dynamicRowOneScroll;
        }
        else if (row == DYNAMIC_ROW_TWO_ID) {
            grid = dynamicRowTwoScroll;
        }
        else if (row == DYNAMIC_ROW_THREE_ID) {
            grid = dynamicRowThreeScroll;
        }
        else if (row == DYNAMIC_ROW_FOUR_ID) {
            grid = dynamicRowFourScroll;
        }
        else if (row == DYNAMIC_ROW_FIVE_ID) {
            grid = dynamicRowFiveScroll;
        }
        return grid;
    }

    /**
     * Calls load recommendation programs method.
     */
    private void loadRecommendedPrograms() {
        Utils.showProgressBar(root, R.id.recommendProgress);
        archiveViewModel.getRecommendPrograms(Utils.getUtcFormattedLocalDate(DATE_INDEX_TODAY), RECOMMENDED_PROGRAMS_LIMIT, 0, this);
    }

    /**
     * Calls load most viewed method.
     */
    private void loadMostViewedPrograms() {
        Utils.showProgressBar(root, R.id.mostViewedProgress);
        archiveViewModel.getMostViewedPrograms(ARCHIVE_LANGUAGE, this);
    }

    /**
     * Calls load newest programs.
     */
    private void loadNewestPrograms() {
        Utils.showProgressBar(root, R.id.newestProgress);
        archiveViewModel.getNewestPrograms(Utils.getUtcFormattedLocalDate(DATE_INDEX_TODAY), NEWEST_LIMIT, 0, this);
    }

    /**
     * Calls load categories method (parent or subcategories).
     * @param isParent
     */
    private void loadCategories(boolean isParent) {
        //Utils.showProgressBar(root, R.id.categoriesProgress);
        if (isParent) {
            archiveViewModel.getParentCategories(this);
        }
        else {
            archiveViewModel.getSubCategories(this);
        }
    }

    /**
     * Calls get guide by date.
     * @param dateIndex
     * @throws Exception
     */
    private void loadGuideData(int dateIndex) throws Exception {
        Utils.showProgressBar(root, R.id.topicalSeriesProgress);

        JSONArray jsonArray = archiveViewModel.getSeriesData();
        if (jsonArray != null && jsonArray.length() > 0) {
            this.addGuideItemElements(jsonArray, SERIES_METHOD);
        }
        else {
            archiveViewModel.getGuideByDate(Utils.getUtcFormattedLocalDate(dateIndex), dateIndex, this);
        }
    }

    /**
     * Calls get program info method.
     * @param obj
     * @throws Exception
     */
    private void loadProgramInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, this.getProgressBarByRow());
        String programId = Utils.getJsonStringValue(obj, ID);
        if (programId != null) {
            archiveViewModel.getProgramInfo(programId, this);
        }
    }

    /**
     * Calls get series info method.
     * @param obj
     * @throws Exception
     */
    private void loadSeriesInfo(JSONObject obj) throws Exception {
        Utils.showProgressBar(root, this.getProgressBarByRow());
        String sid = Utils.getJsonIntValue(obj, SID).toString();
        if (sid != null) {
            archiveViewModel.getSeriesInfo(sid, this);
        }
    }

    private int getProgressBarByRow() {
        int progressBarId = 0;
        if (focusedRow == RECOMMENDATIONS_ROW_ID) {
            progressBarId = R.id.recommendProgress;
        }
        else if (focusedRow == MOST_VIEWED_ROW_ID) {
            progressBarId = R.id.mostViewedProgress;
        }
        else if (focusedRow == NEWEST_ROW_ID) {
            progressBarId = R.id.newestProgress;
        }
        else if (focusedRow == SERIES_ROW_ID) {
            progressBarId = R.id.topicalSeriesProgress;
        }
        else if (focusedRow == DYNAMIC_ROW_ONE_ID) {
            progressBarId = R.id.dynamicRowOneProgress;
        }
        else if (focusedRow == DYNAMIC_ROW_TWO_ID) {
            progressBarId = R.id.dynamicRowTwoProgress;
        }
        else if (focusedRow == DYNAMIC_ROW_THREE_ID) {
            progressBarId = R.id.dynamicRowThreeProgress;
        }
        else if (focusedRow == DYNAMIC_ROW_FOUR_ID) {
            progressBarId = R.id.dynamicRowFourProgress;
        }
        else if (focusedRow == DYNAMIC_ROW_FIVE_ID) {
            progressBarId = R.id.dynamicRowFiveProgress;
        }

        return progressBarId;
    }

    /**
     * Calculates and sets row and content height.
     * @param dynamicRowCount
     */
    private void calculateAndSetContentRowHeight(int dynamicRowCount) {
        double contentRowHeight = this.getContentRowHeight();

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Content row height: " + contentRowHeight + "dp");
        }

        int contentRowHeightPx = Utils.dpToPx(contentRowHeight);
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Content row height: " + contentRowHeightPx + "px");
        }

        for (int containerId: ARCHIVE_MAIN_CONTENT_ROW_IDS) {
            RelativeLayout container = root.findViewById(containerId);
            if (container != null) {
                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.height = contentRowHeightPx;
                container.setLayoutParams(params);
            }
        }

        int contentContainerHeight = (contentRowHeightPx + 30) * (6 + dynamicRowCount);

        RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
        if (contentContainer != null) {
            ViewGroup.LayoutParams params = contentContainer.getLayoutParams();
            params.height = contentContainerHeight;
            contentContainer.setLayoutParams(params);
        }
    }

    /**
     * Calculates row height.
     * @return
     */
    private double getContentRowHeight() {
        float screenHeight = Utils.getScreenHeightDp();
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Screen height: " + screenHeight + "dp");
        }

        double spaceAvailable = screenHeight - (TOOLBAR_HEIGHT + ARCHIVE_MAIN_TITLE_HEIGHT * 3);
        return spaceAvailable / 2.5;
    }

    /**
     * Restores white background to content row.
     * @param context
     * @param id
     */
    private void setRowWhiteBackground(Context context, int id) {
        RelativeLayout relativeLayout = root.findViewById(id);
        if (relativeLayout != null) {
            relativeLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        }
    }

    /**
     * Scrolls vertically.
     * @param row
     * @param smoothScroll
     */
    private void scrollTo(int row, boolean smoothScroll) {
        RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
        if (contentContainer != null) {
            int multiplier = row - 1;

            if (multiplier < 0) {
                multiplier = 0;
            }

            int value = Utils.dpToPx(getContentRowHeight() + ARCHIVE_MAIN_TITLE_HEIGHT) * multiplier;

            if (smoothScroll) {
                ObjectAnimator animator = ObjectAnimator.ofInt(contentContainer, SCROLL_Y, value);
                if (animator != null) {
                    animator.setDuration(ARCHIVE_MAIN_SCROLL_Y_DURATION);
                    animator.start();
                }
                else {
                    contentContainer.scrollTo(0, value);
                }
            }
            else {
                contentContainer.scrollTo(0, value);
            }
        }
    }

    /**
     * Sets focus to the item of row.
     */
    private void setFocusToColumn(int pos) {
        HorizontalGridView grid = this.getScrollView(focusedRow);
        if (grid != null) {
            grid.scrollToPosition(pos);
            Utils.requestFocus(grid);
        }
    }

    /**
     * Scrolls row to given column.
     * @param row
     * @param column
     */
    private void scrollRowToColumn(int row, int column) {
        HorizontalGridView grid = this.getScrollView(row);
        if (grid != null) {
            grid.scrollToPosition(column);
        }
    }

    /**
     * Get scroll view item position.
     */
    private int getSelectedPosition() {
        HorizontalGridView horizontalGridView = this.getScrollView(focusedRow);
        if (horizontalGridView != null) {
            int pos = horizontalGridView.getSelectedPosition();
            if (pos < 0) {
                pos = 0;
            }
            return pos;
        }

        return ARCHIVE_MAIN_NO_SEL_POS;
    }

    /**
     * Set selected item position.
     */
    private void setSelectedPosition(int position) {
        HorizontalGridView grid = this.getScrollView(focusedRow);
        if (grid != null) {
            grid.setSelectedPositionSmooth(position);
        }
    }

    /**
     * Saves page state and forwards to categories page.
     * @param obj
     * @throws Exception
     */
    private void toCategoriesPage(JSONObject obj) throws Exception {
        if (obj != null) {
            sharedCacheViewModel.setSelectedCategory(obj);
            sharedCacheViewModel.setPageToHistory(ARCHIVE_MAIN_FRAGMENT);

            this.cachePageState();

            Utils.toPage(CATEGORIES_FRAGMENT, getActivity(), true, false, null);
        }
    }

    private void cachePageState() {
        sharedCacheViewModel.setArchiveMainPageStateItem(
                new ArchiveMainPageStateItem(
                        focusedRow,
                        this.getSelectedPosition(),
                        colFocusWas,
                        visibleSubCategories));
    }

    /**
     * Aff back button item to sub categories array.
     * @param jsonArray
     * @return
     * @throws Exception
     */
    private JSONArray addBackItem(JSONArray jsonArray) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put(BACK_TEXT, getText(R.string.back));
        jsonArray.put(obj);

        return jsonArray;
    }

    /**
     * Gets category object by index and set category row title.
     * @param index
     * @throws Exception
     */
    private void setCategoriesTextByIndex(int index) throws Exception {
        JSONObject obj = archiveViewModel.getParentCategoryByIndex(index);
        if (obj != null) {
            String name = Utils.getJsonStringValue(obj, NAME);
            if (name != null) {
                this.setCategoriesText(name);
            }
        }
    }

    /**
     * Sets category row title text.
     * @param text
     */
    private void setCategoriesText(String text) {
        TextView categoriesText = root.findViewById(R.id.categoriesText);
        if (categoriesText != null) {
            categoriesText.setText(text);
        }
    }

    /**
     * Returns focused row program by index.
     * @param index
     * @throws Exception
     */
    private JSONObject getProgramByIndex(int index) throws Exception {
        JSONObject program = null;

        if (focusedRow == RECOMMENDATIONS_ROW_ID) {
            program = archiveViewModel.getRecommendedByIndex(index);
        }
        else if (focusedRow == MOST_VIEWED_ROW_ID) {
            program = archiveViewModel.getMostViewedByIndex(index);
        }
        else if (focusedRow == NEWEST_ROW_ID) {
            program = archiveViewModel.getNewestByIndex(index);
        }
        else if (focusedRow == DYNAMIC_ROW_ONE_ID) {
            program = archiveViewModel.getDynamicRowElementByIndex(index, DYNAMIC_ROW_ONE);
        }
        else if (focusedRow == DYNAMIC_ROW_TWO_ID) {
            program = archiveViewModel.getDynamicRowElementByIndex(index, DYNAMIC_ROW_TWO);
        }
        else if (focusedRow == DYNAMIC_ROW_THREE_ID) {
            program = archiveViewModel.getDynamicRowElementByIndex(index, DYNAMIC_ROW_THREE);
        }
        else if (focusedRow == DYNAMIC_ROW_FOUR_ID) {
            program = archiveViewModel.getDynamicRowElementByIndex(index, DYNAMIC_ROW_FOUR);
        }
        else if (focusedRow == DYNAMIC_ROW_FIVE_ID) {
            program = archiveViewModel.getDynamicRowElementByIndex(index, DYNAMIC_ROW_FIVE);
        }

        return program;
    }
}
