package ru.tv7.nebesa.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.tv7.nebesa.BuildConfig;
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

import static ru.tv7.nebesa.helpers.Constants.ABOUT_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_PLAYER_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.CHANNEL_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.COLON;
import static ru.tv7.nebesa.helpers.Constants.DASH;
import static ru.tv7.nebesa.helpers.Constants.DOT;
import static ru.tv7.nebesa.helpers.Constants.ERROR_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.FADE_IN_ANIMATION_DURATION;
import static ru.tv7.nebesa.helpers.Constants.FADE_IN_ANIMATION_END;
import static ru.tv7.nebesa.helpers.Constants.FADE_IN_ANIMATION_START;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.FAVORITES_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.GUIDE_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.ID;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_RESULT_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SERIES_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SHOW_ANIMATIONS;
import static ru.tv7.nebesa.helpers.Constants.TIME_STAMP_FORMAT;
import static ru.tv7.nebesa.helpers.Constants.TV_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.TV_PLAYER_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.UTC;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.VIDEO_STATUSES_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.ZERO_DURATION;
import static ru.tv7.nebesa.helpers.Constants.ZERO_STR;

/**
 * Util methods.
 */
public abstract class Utils {

    public static void fadePageAnimation(ViewGroup viewGroup) {
        if (SHOW_ANIMATIONS) {
            viewGroup.startAnimation(createAnimation());
        }
    }

    public static String prependZero(long value) {
        if (value < 10) {
            return ZERO_STR + String.valueOf(value);
        }
        return String.valueOf(value);
    }

    public static int getScreenWidthPx() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeightPx() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float getScreenWidthDp() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return getScreenWidthPx() / dm.density;
    }

    public static float getScreenHeightDp() {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return getScreenHeightPx() / dm.density;
    }

    public static int dpToPx(double dpValue) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return (int)Math.round(dpValue * dm.density);
    }

    private static Animation createAnimation() {
        Animation animation = new AlphaAnimation(FADE_IN_ANIMATION_START, FADE_IN_ANIMATION_END);
        animation.setDuration(FADE_IN_ANIMATION_DURATION);
        return animation;
    }

    public static void showProgressBar(View root, int id) {
        if (root != null) {
            ProgressBar pb = root.findViewById(id);
            if (pb != null) {
                pb.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void hideProgressBar(View root, int id) {
        if (root != null) {
            ProgressBar pb = root.findViewById(id);
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
        }
    }

    public static void requestFocus(View view) {
        if (view != null) {
            view.requestFocus();
        }
    }

    public static void requestFocusById(View root, int id) {
        if (root != null) {
            requestFocus(root.findViewById(id));
        }
    }

    public static View getFocusedView(Activity activity) {
        return activity.getCurrentFocus();
    }

    public static void toErrorPage(FragmentActivity activity) {
        toPage(ERROR_FRAGMENT, activity, true, false, null);
    }

    public static void toPage(String page, FragmentActivity activity, boolean replace, boolean addToBackStack, Bundle bundle) {
        FragmentManager fragmentManager = getFragmentManager(activity);
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentByTag(page);
            if (fragment == null) {
                if (page.equals(TV_MAIN_FRAGMENT)) {
                    fragment = TvMainFragment.newInstance();
                }
                else if (page.equals(ARCHIVE_MAIN_FRAGMENT)) {
                    fragment = ArchiveMainFragment.newInstance();
                }
                else if (page.equals(TV_PLAYER_FRAGMENT)) {
                    fragment = TvPlayerFragment.newInstance();
                }
                else if (page.equals(ARCHIVE_PLAYER_FRAGMENT)) {
                    fragment = ArchivePlayerFragment.newInstance();
                }
                else if (page.equals(PROGRAM_INFO_FRAGMENT)) {
                    fragment = ProgramInfoFragment.newInstance();
                }
                else if (page.equals(CATEGORIES_FRAGMENT)) {
                    fragment = CategoriesFragment.newInstance();
                }
                else if (page.equals(GUIDE_FRAGMENT)) {
                    fragment = GuideFragment.newInstance();
                }
                else if (page.equals(SERIES_FRAGMENT)) {
                    fragment = SeriesFragment.newInstance();
                }
                else if (page.equals(SEARCH_FRAGMENT)) {
                    fragment = SearchFragment.newInstance();
                }
                else if (page.equals(SEARCH_RESULT_FRAGMENT)) {
                    fragment = SearchResultFragment.newInstance();
                }
                else if (page.equals(FAVORITES_FRAGMENT)) {
                    fragment = FavoritesFragment.newInstance();
                }
                else if (page.equals(CHANNEL_INFO_FRAGMENT)) {
                    fragment = ChannelInfoFragment.newInstance();
                }
                else if (page.equals(ABOUT_FRAGMENT)) {
                    fragment = AboutFragment.newInstance();
                }
                else if (page.equals(ERROR_FRAGMENT)) {
                    fragment = ErrorFragment.newInstance();
                }
                else if (page.equals(EXIT_OVERLAY_FRAGMENT)) {
                    fragment = ExitFragment.newInstance();
                }
            }

            if (bundle != null) {
                fragment.setArguments(bundle);
            }

            FragmentTransaction fragmentTransaction = null;

            if (replace) {
                fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, page);
            }
            else {
                fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragment_container, fragment, page);
            }

            if (addToBackStack) {
                fragmentTransaction = fragmentTransaction.addToBackStack(page);
            }

            if (fragmentTransaction != null) {
                fragmentTransaction.commit();
            }
        }
    }

    public static FragmentManager getFragmentManager(FragmentActivity activity) {
        if (activity != null) {
            return activity.getSupportFragmentManager();
        }
        return null;
    }

    public static String getTimeStampByDurationMs(String duration) {
        if (duration != null) {
            long ms = Long.parseLong(duration);

            long s = (ms / 1000) % 60;
            long m = (ms / (1000 * 60)) % 60;
            long h = (ms / (1000 * 60 * 60)) % 24;

            return String.format(Locale.getDefault(), TIME_STAMP_FORMAT, h, m, s);
        }
        else {
            return ZERO_DURATION;
        }
    }

    public static String getTodayUtcFormattedLocalDate() {
        Calendar today = getLocalCalendar();
        today.setTime(new Date());

        return today.get(Calendar.YEAR) + DASH + prependZero(today.get(Calendar.MONTH) + 1) + DASH + prependZero(today.get(Calendar.DATE));
    }

    public static String getDateByCalendar(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + DASH + prependZero(calendar.get(Calendar.MONTH) + 1) + DASH + prependZero(calendar.get(Calendar.DATE));
    }

    public static String getLocalDateByCalendar(Calendar calendar) {
        return calendar.get(Calendar.DATE) + DOT + (calendar.get(Calendar.MONTH) + 1) + DOT + calendar.get(Calendar.YEAR);
    }

    public static long getTimeInMilliseconds() {
        Calendar calendar = getLocalCalendar();
        return calendar.getTimeInMillis();
    }

    public static long getUtcTimeInMilliseconds() {
        Calendar calendar = getUtcCalendar();
        return calendar.getTimeInMillis();
    }

    public static String getValue(JSONObject obj, String key) throws Exception {
        if (obj != null && key != null && obj.has(key)) {
            String value = obj.getString(key);
            if (value != null && !value.equals(NULL_VALUE)) {
                return value;
            }
            return null;
        }
        return null;
    }

    public static String createLocalTimeString(String time) {
        Calendar calendar = getLocalCalendar();
        calendar.setTimeInMillis(stringToLong(time));

        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return prependZero(hours) + COLON + prependZero(minutes);
    }

    public static String createLocalDateString(String time) {
        Calendar calendar = getLocalCalendar();
        calendar.setTimeInMillis(stringToLong(time));

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return prependZero(date) + DOT + prependZero(month) + DOT + year;
    }

    public static String createLocalDateStringShort(String time) {
        Calendar calendar = getLocalCalendar();
        calendar.setTimeInMillis(stringToLong(time));

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return date + DOT + month + DOT + year;
    }

    public static long getLocalTimeInMilliseconds(String time) {
        Calendar calendar = getLocalCalendar();
        calendar.setTimeInMillis(stringToLong(time));

        return calendar.getTimeInMillis();
    }

    public static Calendar getLocalCalendar() {
        return GregorianCalendar.getInstance(TimeZone.getDefault());
    }

    public static Calendar getUtcCalendar() {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone(UTC));
    }

    public static int stringToInt(String value) {
        return Integer.parseInt(value);
    }

    public static long stringToLong(String value) {
        return Long.parseLong(value);
    }

    public static JSONArray getSavedPrefs(String tag, String defaultValue, Context context) throws Exception {
        JSONArray jsonArray = null;

        if (context != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPref == null) {
                return null;
            }

            String jsonStr = sharedPref.getString(tag, defaultValue);
            if (jsonStr != null) {
                jsonArray = new JSONArray(jsonStr);

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "Utils.getSavedPrefs(): Tag: " + tag);
                    Log.d(LOG_TAG, "Utils.getSavedPrefs(): Shared prefs: " + jsonStr);
                }
            }
        }

        return jsonArray;
    }

    public static void savePrefs(String tag, Context context, JSONArray jsonArray) throws Exception {
        if (context != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPref != null) {
                SharedPreferences.Editor editor = sharedPref.edit();

                String jsonStr = jsonArray.toString();

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "Utils.savePrefs(): Tag: " + tag);
                    Log.d(LOG_TAG, "Utils.savePrefs(): Shared prefs: " + jsonStr);
                }

                editor.putString(tag, jsonStr);
                editor.commit();
            }
        }
    }

    public static int isProgramInFavorites(Context context, String programId) throws Exception {
        int notFound = -1;

        if (context != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPref != null) {
                String jsonStr = sharedPref.getString(FAVORITES_SP_TAG, FAVORITES_SP_DEFAULT);
                if (jsonStr == null) {
                    return notFound;
                }

                JSONArray jsonArray = new JSONArray(jsonStr);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj == null) {
                        continue;
                    }

                    String id = Utils.getValue(obj, ID);
                    if (id == null) {
                        continue;
                    }

                    if (id.equals(programId)) {
                        return i;
                    }
                }
            }
        }

        return notFound;
    }

    public static JSONObject getVideoStatus(int programId, Context context) throws Exception {
        JSONObject result = null;

        JSONArray jsonArray = getSavedPrefs(VIDEO_STATUSES_SP_TAG, VIDEO_STATUSES_SP_DEFAULT, context);
        if (jsonArray != null) {
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj == null) {
                    continue;
                }

                String value = getValue(obj, ID);
                if (value == null) {
                    continue;
                }

                int id = Utils.stringToInt(value);
                if (id == programId) {
                    result = jsonArray.getJSONObject(i);
                    break;
                }
            }
        }

        return result;
    }
}
