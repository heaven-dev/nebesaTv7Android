package ru.tv7.nebesatv7.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.NebesaTv7;
import ru.tv7.nebesatv7.helpers.ArchiveDataCacheItem;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.interfaces.ArchiveDataLoadedListener;

import static ru.tv7.nebesatv7.helpers.Constants.AMPERSAND;
import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_BASE_URL;
import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_LANGUAGE;
import static ru.tv7.nebesatv7.helpers.Constants.BROADCAST_DATE;
import static ru.tv7.nebesatv7.helpers.Constants.BROADCAST_DATE_TIME;
import static ru.tv7.nebesatv7.helpers.Constants.BROADCAST_RECOMMENDATIONS_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.CAPTION;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORY_ID;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORY_ID_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORY_NAME;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORY_PROGRAMS_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.COLON;
import static ru.tv7.nebesatv7.helpers.Constants.DASH_WITH_SPACES;
import static ru.tv7.nebesatv7.helpers.Constants.DATE_INDEX;
import static ru.tv7.nebesatv7.helpers.Constants.DATE_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.DOT;
import static ru.tv7.nebesatv7.helpers.Constants.DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.END_DATE;
import static ru.tv7.nebesatv7.helpers.Constants.END_TIME;
import static ru.tv7.nebesatv7.helpers.Constants.EPISODE_NUMBER;
import static ru.tv7.nebesatv7.helpers.Constants.EQUAL;
import static ru.tv7.nebesatv7.helpers.Constants.FIRST_BROADCAST;
import static ru.tv7.nebesatv7.helpers.Constants.GET_;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_DATA;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_DATE_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.ID;
import static ru.tv7.nebesatv7.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesatv7.helpers.Constants.IS_VISIBLE_ON_VOD;
import static ru.tv7.nebesatv7.helpers.Constants.LIMIT_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.LINK_PATH;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.MOST_VIEWED_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.NAME;
import static ru.tv7.nebesatv7.helpers.Constants.NEGATIVE_ONE_STR;
import static ru.tv7.nebesatv7.helpers.Constants.NEWEST_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesatv7.helpers.Constants.OFFSET_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.ONE_STR;
import static ru.tv7.nebesatv7.helpers.Constants.ONGOING_PROGRAM;
import static ru.tv7.nebesatv7.helpers.Constants.ONGOING_PROGRAM_INDEX;
import static ru.tv7.nebesatv7.helpers.Constants.PARENT_CATEGORIES_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.PARENT_ID;
import static ru.tv7.nebesatv7.helpers.Constants.PARENT_NAME;
import static ru.tv7.nebesatv7.helpers.Constants.PATH;
import static ru.tv7.nebesatv7.helpers.Constants.PIPE_WITH_SPACES;
import static ru.tv7.nebesatv7.helpers.Constants.PLAY;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAM_ID_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAM_INFO_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.QUERY_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.QUESTION_MARK;
import static ru.tv7.nebesatv7.helpers.Constants.RECOMMENDATIONS_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.RESULTS;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_URL;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_AND_NAME;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_ID;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_ID_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_INFO_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_NAME;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_PROGRAMS_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.SID;
import static ru.tv7.nebesatv7.helpers.Constants.SNAME;
import static ru.tv7.nebesatv7.helpers.Constants.SPACE;
import static ru.tv7.nebesatv7.helpers.Constants.START_DATE;
import static ru.tv7.nebesatv7.helpers.Constants.START_END_TIME;
import static ru.tv7.nebesatv7.helpers.Constants.SUB_CATEGORIES_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.TIME;
import static ru.tv7.nebesatv7.helpers.Constants.TRANSLATION_METHOD;
import static ru.tv7.nebesatv7.helpers.Constants.TYPE;
import static ru.tv7.nebesatv7.helpers.Constants.UTF_8;
import static ru.tv7.nebesatv7.helpers.Constants.VISIBLE_ON_VOD_SINCE;
import static ru.tv7.nebesatv7.helpers.Constants.VOD_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.VOLLEY_TIMEOUT_VALUE;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_DATE;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_DATE_TIME;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_STR;

/**
 * Archive view model. Makes queries and cache some data.
 */
public class ArchiveViewModel extends ViewModel {

    private ArchiveDataCacheItem recommendations = null;
    private ArchiveDataCacheItem mostViewed = null;
    private ArchiveDataCacheItem newest = null;

    private ArchiveDataCacheItem parentCategories = null;
    private ArchiveDataCacheItem subCategories = null;

    public JSONObject getRecommendationsByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (recommendations != null) {
            if (!recommendations.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getRecommendationsByIndex(): Not recommend data in index!");
                }
                throw new Exception("Not recommend data in index!");
            }

            JSONArray jsonArray = recommendations.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONObject getMostViewedByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (mostViewed != null) {
            if (!mostViewed.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getMostViewedByIndex(): Not most viewed data in index!");
                }
                throw new Exception("Not most viewed data in index!");
            }

            JSONArray jsonArray = mostViewed.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONObject getNewestByIndex(int index) throws Exception {
        JSONObject jsonObject = null;

        if (newest != null) {
            if (!newest.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "*** ArchiveViewModel.getNewestByIndex(): Not newest data in index!");
                }
                throw new Exception("Not newest data in index!");
            }

            JSONArray jsonArray = newest.getData();
            if (jsonArray != null) {
                jsonObject = jsonArray.getJSONObject(index);
            }
        }

        return jsonObject;
    }

    public JSONArray hasSubCategories(int index) throws Exception {
        JSONArray result = new JSONArray();

        if (parentCategories != null) {
            JSONObject obj = parentCategories.getData().getJSONObject(index);
            if (obj != null) {
                String id = Utils.getValue(obj, ID);
                if (id != null) {
                    result = this.getSubCategoriesByParentId(id);
                }
            }
        }
        return result;
    }

    public JSONArray getSubCategoriesByParentId(String id) throws Exception {
        JSONArray result = new JSONArray();

        if (subCategories == null || id == null) {
            return result;
        }

        JSONArray sc = subCategories.getData();

        for( int i = 0; sc != null && i < sc.length(); i++) {
            JSONObject obj = sc.getJSONObject(i);
            if (obj != null) {
                String parentId = Utils.getValue(obj, PARENT_ID);
                if (parentId != null && parentId.equals(id)) {
                    result.put(obj);
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSubCategoriesByParentId(): Parent id: " + id + " has " + result.length() + " sub categories.");
        }

        return result;
    }

    public JSONObject getParentCategoryByIndex(int index) throws Exception {
        JSONObject parentCategory = null;

        if (parentCategories != null) {
            if (!parentCategories.isDataInIndex(index)) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "ArchiveViewModel.getParentCategoryByIndex(): Not parent categories data in index!");
                }
                throw new Exception("Not parent categories data in index!");
            }

            JSONArray pc = parentCategories.getData();
            if (pc != null) {
                parentCategory = pc.getJSONObject(index);
            }
        }
        return parentCategory;
    }

    public JSONArray getParentCategories() {
        if (parentCategories != null) {
            return parentCategories.getData();
        }
        return null;
    }

    /**
     * View model clear lifecycle method.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
    }


    /**
     * Broadcast recommendations programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getBroadcastRecommendationPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = BROADCAST_RECOMMENDATIONS_METHOD;

        if (recommendations != null && recommendations.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getBroadcastRecommendationPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(recommendations.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getBroadcastRecommendationPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Recommend programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getRecommendPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = RECOMMENDATIONS_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getRecommendPrograms(): URL" + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Most viewed programs query.
     * @param archiveLang
     * @param archiveDataLoadedListener
     */
    public void getMostViewedPrograms(final String archiveLang, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = MOST_VIEWED_METHOD;

        if (mostViewed != null && mostViewed.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getMostViewedPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(mostViewed.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + type + QUESTION_MARK + VOD_PARAM + EQUAL + archiveLang;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getMostViewedPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Newest programs query.
     * @param date
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getNewestPrograms(final String date, int limit, int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = NEWEST_METHOD;

        if (newest != null && newest.isCacheValid()) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getNewestPrograms(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(newest.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getNewestPrograms(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Parent categories query.
     * @param archiveDataLoadedListener
     */
    public void getParentCategories(final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = PARENT_CATEGORIES_METHOD;

        if (parentCategories != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getParentCategories(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(parentCategories.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getParentCategories(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Sub categories query.
     * @param archiveDataLoadedListener
     */
    public void getSubCategories(final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SUB_CATEGORIES_METHOD;

        if (subCategories != null) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "*** ArchiveViewModel.getSubCategories(): Return data from cache.");
            }

            archiveDataLoadedListener.onArchiveDataLoaded(subCategories.getData(), type);
        }
        else {
            String url = ARCHIVE_BASE_URL + GET_ + type;
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.getSubCategories(): URL" + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
    }

    /**
     * Translation query.
     * @param archiveDataLoadedListener
     */
    public void getTranslation(final String programId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = TRANSLATION_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + PROGRAM_ID_PARAM + EQUAL + programId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getTranslation(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }


    /**
     * Category programs query.
     * @param categoryId
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getCategoryPrograms(final int categoryId, final int limit, final int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = CATEGORY_PROGRAMS_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + CATEGORY_ID_PARAM + EQUAL + categoryId
                + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getCategoryPrograms(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Program info query.
     * @param programId
     * @param archiveDataLoadedListener
     */
    public void getProgramInfo(final String programId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = PROGRAM_INFO_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + PROGRAM_ID_PARAM + EQUAL + programId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getProgramInfo(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Series info query.
     * @param seriesId
     * @param archiveDataLoadedListener
     */
    public void getSeriesInfo(final int seriesId, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SERIES_INFO_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + SERIES_ID_PARAM + EQUAL + seriesId;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSeriesInfo(): URL: " + url);
        }

        this.runQuery(url, type, null,  archiveDataLoadedListener);
    }

    /**
     * Series programs query.
     * @param seriesId
     * @param limit
     * @param offset
     * @param archiveDataLoadedListener
     */
    public void getSeriesPrograms(final int seriesId, final int limit, final int offset, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = SERIES_PROGRAMS_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + SERIES_ID_PARAM + EQUAL + seriesId
                + AMPERSAND + LIMIT_PARAM + EQUAL + limit + AMPERSAND + OFFSET_PARAM + EQUAL + offset;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getSeriesPrograms(): URL: " + url);
        }

        this.runQuery(url, type, null, archiveDataLoadedListener);
    }

    /**
     * Guide by date query.
     * @param date
     * @param archiveDataLoadedListener
     */
    public void getGuideByDate(final String date, final Integer dateIndex, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        String type = GUIDE_DATE_METHOD;

        String url = ARCHIVE_BASE_URL + GET_ + type + QUESTION_MARK + DATE_PARAM + EQUAL + date;
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.getGuideByDate(): URL: " + url);
        }

        this.runQuery(url, type, dateIndex, archiveDataLoadedListener);
    }

    /**
     * Search items by search string.
     * @param searchString
     * @param archiveDataLoadedListener
     */
    public void searchItemsByString(String searchString, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        try {
            String type = SEARCH_METHOD;

            searchString = URLEncoder.encode(searchString, UTF_8);

            String url = SEARCH_URL + QUESTION_MARK + VOD_PARAM + EQUAL + ARCHIVE_LANGUAGE + AMPERSAND + QUERY_PARAM + EQUAL + searchString;

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.searchItemsByString(): URL: " + url);
            }

            this.runQuery(url, type, null, archiveDataLoadedListener);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ArchiveViewModel.searchItemsByString(): Exception: " + e);
            }

            archiveDataLoadedListener.onArchiveDataLoadError(e.getMessage(), SEARCH_METHOD);
        }
    }

    /**
     * Runs REST query.
     * @param url
     * @param type
     * @param archiveDataLoadedListener
     */
    private void runQuery(final String url, final String type, final Object data, final ArchiveDataLoadedListener archiveDataLoadedListener) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "ArchiveViewModel.runQuery(): called.");
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "ArchiveViewModel.runQuery(): onResponse(): " + response.toString());
                            }

                            if (archiveDataLoadedListener != null) {
                                if (type.equals(PARENT_CATEGORIES_METHOD) || type.equals(SUB_CATEGORIES_METHOD)) {
                                    JSONArray filtered = filterCategoryResponse(response, type);
                                    if (type.equals(PARENT_CATEGORIES_METHOD)) {
                                        parentCategories = new ArchiveDataCacheItem(filtered);
                                    }
                                    else {
                                        subCategories = new ArchiveDataCacheItem(filtered);
                                    }

                                    archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                }
                                else if (type.equals(TRANSLATION_METHOD) || type.equals(SERIES_INFO_METHOD)) {
                                    JSONArray filtered = response.getJSONArray(type);

                                    archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                }
                                else if (type.equals(GUIDE_DATE_METHOD)) {
                                    JSONArray filtered = filterGuideByDateResponse(response, type, (Integer)data);

                                    archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                }
                                else if (type.equals(SEARCH_METHOD)) {
                                    JSONArray filtered = filterResponse(response, RESULTS);

                                    archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                }
                                else {
                                    JSONArray filtered = filterResponse(response, type);

                                    if (type.equals(BROADCAST_RECOMMENDATIONS_METHOD) || type.equals(RECOMMENDATIONS_METHOD)) {
                                        recommendations = new ArchiveDataCacheItem(filtered);
                                    }
                                    else if (type.equals(MOST_VIEWED_METHOD)) {
                                        mostViewed = new ArchiveDataCacheItem(filtered);
                                    }
                                    else if (type.equals(NEWEST_METHOD)) {
                                        newest = new ArchiveDataCacheItem(filtered);
                                    }

                                    archiveDataLoadedListener.onArchiveDataLoaded(filtered, type);
                                }
                            }
                        }
                        catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "ArchiveViewModel.onResponse(): Error fetching json: " + e.getMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "ArchiveViewModel.onErrorResponse(): Error fetching json: " + error.toString());
                            }

                            if (archiveDataLoadedListener != null) {
                                archiveDataLoadedListener.onArchiveDataLoadError(error.getMessage(), type);
                            }
                        }
                        catch(Exception e) {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "ArchiveViewModel.onErrorResponse(): Error fetching json: " + e.getMessage());
                            }
                        }
                    }
                }
        );

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                VOLLEY_TIMEOUT_VALUE,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        NebesaTv7.getInstance().addToRequestQueue(jsonRequest);
    }

    /**
     * Filters JSON response.
     * @param jsonObject
     * @param objectName
     * @return
     * @throws Exception
     */
    private JSONArray filterResponse(JSONObject jsonObject, String objectName) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(objectName);

        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            setValue(respObj, ID, this.getValue(sourceObj, ID), true);
            setValue(respObj, IMAGE_PATH, this.getValue(sourceObj, IMAGE_PATH), false);
            setValue(respObj, LINK_PATH, this.getValue(sourceObj, LINK_PATH), false);
            setValue(respObj, EPISODE_NUMBER, this.getValue(sourceObj, EPISODE_NUMBER), true);
            setValue(respObj, SID, this.getValue(sourceObj, SID), true);
            setValue(respObj, SERIES_ID, this.getValue(sourceObj, SERIES_ID), true);
            setValue(respObj, NAME, this.getValue(sourceObj, NAME), false);
            setValue(respObj, SERIES_NAME, this.getValue(sourceObj, SERIES_NAME), false);
            setValue(respObj, SNAME, this.getValue(sourceObj, SNAME), false);

            String firstBroadcast = getValue(sourceObj, FIRST_BROADCAST);
            if (firstBroadcast == null) {
                firstBroadcast = getValue(sourceObj, START_DATE);
            }

            setValue(respObj, BROADCAST_DATE_TIME, getDateTimeByTimeInMs(firstBroadcast), false);
            setValue(respObj, BROADCAST_DATE, getDateByTimeInMs(firstBroadcast), false);
            setValue(respObj, DURATION, Utils.getTimeStampByDurationMs(this.getValue(sourceObj, DURATION)), false);

            String seriesName = this.getValue(respObj, SERIES_NAME);
            if (seriesName == null) {
                seriesName = this.getValue(respObj, SNAME);
            }

            String seriesAndName = null;
            if (seriesName != null) {
                seriesAndName = seriesName;
            }

            String name = this.getValue(respObj, NAME);
            if (seriesName != null && name != null && name.length() > 0) {
                seriesAndName += (PIPE_WITH_SPACES + name);
            }

            if (seriesAndName == null) {
                seriesAndName = name;
            }

            setValue(respObj, SERIES_AND_NAME, seriesAndName, false);

            setValue(respObj, CAPTION, this.getValue(sourceObj, CAPTION), false);
            setValue(respObj, TYPE, this.getValue(sourceObj, TYPE), false);
            setValue(respObj, PATH, this.getValue(sourceObj, PATH), false);

            if (objectName.equals(BROADCAST_RECOMMENDATIONS_METHOD) || objectName.equals(PROGRAM_INFO_METHOD)) {
                String isVisibleOnVod = this.getValue(sourceObj, IS_VISIBLE_ON_VOD);
                if (isVisibleOnVod != null) {
                    setValue(respObj, IS_VISIBLE_ON_VOD, isVisibleOnVod, true);

                    String visibleOnVodSince = this.getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                    if (visibleOnVodSince != null && isVisibleOnVod.equals(ONE_STR)) {
                        String isVisibleValue = isPastTime(visibleOnVodSince) ? ONE_STR : ZERO_STR;
                        setValue(respObj, IS_VISIBLE_ON_VOD, isVisibleValue, true);
                    }
                }
                else {
                    String visibleOnVodSince = this.getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                    if (visibleOnVodSince == null) {
                        setValue(respObj, IS_VISIBLE_ON_VOD, NEGATIVE_ONE_STR, true);
                    }
                }

                firstBroadcast = getValue(sourceObj, VISIBLE_ON_VOD_SINCE);
                if (firstBroadcast != null) {
                    String duration = getValue(sourceObj, DURATION);
                    if (duration != null) {
                        String startTime = String.valueOf(Long.parseLong(firstBroadcast) - Long.parseLong(duration));
                        setValue(respObj, BROADCAST_DATE_TIME, getDateTimeByTimeInMs(startTime), false);
                    }
                }
            }
            else {
                setValue(respObj, IS_VISIBLE_ON_VOD, ONE_STR, true);
            }

            if (objectName.equals(CATEGORY_PROGRAMS_METHOD)) {
                String play = this.getValue(sourceObj, PLAY);
                if (play != null) {
                    setValue(respObj, PLAY, Boolean.parseBoolean(play) ? ONE_STR : ZERO_STR, true);
                }
            }

            respArray.put(respObj);
        }

        return respArray;
    }

    /**
     * Filters parent category and sub category responses.
     * @param jsonObject
     * @param type
     * @return
     * @throws Exception
     */
    private JSONArray filterCategoryResponse(JSONObject jsonObject, String type) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(type);

        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            if (type.equals(PARENT_CATEGORIES_METHOD)) {
                setValue(respObj, ID, this.getValue(sourceObj, ID), true);
                setValue(respObj, NAME, this.getValue(sourceObj, NAME), false);
            }
            else {
                setValue(respObj, PARENT_ID, this.getValue(sourceObj, PARENT_ID), true);
                setValue(respObj, PARENT_NAME, this.getValue(sourceObj, PARENT_NAME), false);
                setValue(respObj, CATEGORY_ID, this.getValue(sourceObj, CATEGORY_ID), true);
                setValue(respObj, CATEGORY_NAME, this.getValue(sourceObj, CATEGORY_NAME), false);
            }

            respArray.put(respObj);
        }

        return respArray;
    }

    private JSONArray filterGuideByDateResponse(JSONObject jsonObject, String type, Integer dateIndex) throws Exception {
        JSONArray respArray = new JSONArray();

        JSONArray array = jsonObject.getJSONArray(type);
        long currentTime = Utils.getTimeInMilliseconds();

        int ongoingProgramIndex = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject respObj = new JSONObject();
            JSONObject sourceObj = array.getJSONObject(i);

            setValue(respObj, ID, this.getValue(sourceObj, ID), true);
            setValue(respObj, PATH, this.getValue(sourceObj, PATH), false);

            setValue(respObj, IMAGE_PATH, this.getValue(sourceObj, IMAGE_PATH), false);
            setValue(respObj, CAPTION, this.getValue(sourceObj, CAPTION), false);

            String start = this.getValue(sourceObj, TIME);
            String end = this.getValue(sourceObj, END_TIME);

            if (start == null || end == null) {
                throw new Exception("Invalid response in guide by date query!");
            }

            if (currentTime >= Long.parseLong(start) && currentTime <= Long.parseLong(end) || Long.parseLong(end) < currentTime) {
                ongoingProgramIndex = i;
            }

            // Start and end time (hh:mm - hh:mm)
            String startTime = this.createLocalTimeString(start);
            String endTime = this.createLocalTimeString(end);

            setValue(respObj, START_END_TIME, startTime + DASH_WITH_SPACES + endTime, false);

            // Start and end date (dd.mm.yyyy)
            String startDate = this.createLocalDateString(start);
            String endDate = this.createLocalDateString(end);

            setValue(respObj, START_DATE, startDate, false);
            setValue(respObj, END_DATE, endDate, false);

            setValue(respObj, BROADCAST_DATE_TIME, getDateTimeByTimeInMs(start), false);
            setValue(respObj, BROADCAST_DATE, getDateByTimeInMs(start), false);

            String duration = String.valueOf(Long.parseLong(end) - Long.parseLong(start));
            setValue(respObj, DURATION, Utils.getTimeStampByDurationMs(duration), false);

            setValue(respObj, EPISODE_NUMBER, this.getValue(sourceObj, EPISODE_NUMBER), true);
            setValue(respObj, IS_VISIBLE_ON_VOD, ZERO_STR, true);

            // series and name
            String series = this.getValue(sourceObj, SERIES);

            String seriesName = null;
            if (series != null) {
                seriesName = series;
            }

            String name = this.getValue(sourceObj, NAME);
            if (series != null && name != null && name.length() > 0) {
                seriesName += (PIPE_WITH_SPACES + name);
            }

            if (seriesName == null) {
                seriesName = name;
            }

            setValue(respObj, SERIES_AND_NAME, seriesName, false);

            respArray.put(respObj);
        }

        JSONObject responseObj = new JSONObject();
        responseObj.put(ONGOING_PROGRAM_INDEX, ongoingProgramIndex);
        responseObj.put(DATE_INDEX, dateIndex);
        responseObj.put(GUIDE_DATA, dateIndex == 0 ? this.addOngoingProgramToIndex(respArray, ongoingProgramIndex) : respArray);

        return new JSONArray().put(responseObj);
    }

    /**
     * Adds ongoing program flag to index.
     * @param respArray
     * @param index
     * @return
     * @throws Exception
     */
    private JSONArray addOngoingProgramToIndex(JSONArray respArray, int index) throws Exception {
        JSONObject obj = respArray.getJSONObject(index);
        if (obj != null) {
            setValue(obj, ONGOING_PROGRAM, ONE_STR, true);
            respArray.put(index, obj);
        }

        return respArray;
    }

    /**
     * Checks and get value of property from the source object.
     * @param obj
     * @param id
     * @return
     * @throws Exception
     */
    private String getValue(JSONObject obj, String id) throws Exception {
        if (obj.has(id)) {
            String value = obj.getString(id);
            return value.equals(NULL_VALUE) ? null : value;
        }
        return null;
    }

    /**
     * Validates and sets property to response object.
     * @param obj
     * @param id
     * @param value
     * @param isInteger
     * @throws Exception
     */
    private void setValue(JSONObject obj, String id, String value, boolean isInteger) throws Exception {
        if (value != null && !value.equals(NULL_VALUE)) {
            if (isInteger) {
                obj.put(id, Long.valueOf(value));
            }
            else {
                obj.put(id, value);
            }
        }
    }

    /**
     * Returns date (dd.mm.yyyy hh:mm) by given time ms.
     * @param time
     * @return
     */
    private String getDateTimeByTimeInMs(String time) {
        if (time != null) {
            Calendar calendar = Utils.getLocalCalendar();
            calendar.setTimeInMillis(Long.valueOf(time));

            return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR)
                    + SPACE + Utils.prependZero(calendar.get(Calendar.HOUR_OF_DAY)) + COLON + Utils.prependZero(calendar.get(Calendar.MINUTE));
        }
        else {
            return ZERO_DATE_TIME;
        }
    }

    /**
     * Returns date (dd.mm.yyyy) by given time ms.
     * @param time
     * @return
     */
    private String getDateByTimeInMs(String time) {
        if (time != null) {
            Calendar calendar = Utils.getLocalCalendar();
            calendar.setTimeInMillis(Long.valueOf(time));

            return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR);
        }
        else {
            return ZERO_DATE;
        }

    }

    /**
     * Checks if given time is in the past.
     * @param time
     * @return
     */
    private boolean isPastTime(String time) {
        if (time != null) {
            Calendar now = Utils.getUtcCalendar();
            now.setTime(new Date());

            return now.getTimeInMillis() > Long.valueOf(time);
        }

        return false;
    }

    private String createLocalTimeString(String time) {
        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTimeInMillis(Long.parseLong(time));

        return Utils.prependZero(calendar.get(Calendar.HOUR_OF_DAY)) + COLON + Utils.prependZero(calendar.get(Calendar.MINUTE));
    }

    private String createLocalDateString(String time) {
        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTimeInMillis(Long.parseLong(time));

        return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR);
    }
}
