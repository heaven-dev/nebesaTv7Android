package ru.tv7.nebesatv7.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.fragments.ArchiveMainFragment;
import ru.tv7.nebesatv7.fragments.ArchivePlayerFragment;
import ru.tv7.nebesatv7.fragments.CategoriesFragment;
import ru.tv7.nebesatv7.fragments.ExitFragment;
import ru.tv7.nebesatv7.fragments.GuideFragment;
import ru.tv7.nebesatv7.fragments.ProgramInfoFragment;
import ru.tv7.nebesatv7.fragments.SearchFragment;
import ru.tv7.nebesatv7.fragments.SearchResultFragment;
import ru.tv7.nebesatv7.fragments.SeriesFragment;
import ru.tv7.nebesatv7.fragments.TvMainFragment;
import ru.tv7.nebesatv7.fragments.TvPlayerFragment;

import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_PLAYER_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORIES_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.COLON;
import static ru.tv7.nebesatv7.helpers.Constants.DASH;
import static ru.tv7.nebesatv7.helpers.Constants.DOT;
import static ru.tv7.nebesatv7.helpers.Constants.EXIT_OVERLAY_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_END;
import static ru.tv7.nebesatv7.helpers.Constants.FADE_ANIMATION_START;
import static ru.tv7.nebesatv7.helpers.Constants.GUIDE_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAM_INFO_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_RESULT_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SERIES_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SHOW_ANIMATIONS;
import static ru.tv7.nebesatv7.helpers.Constants.TIME_STAMP_FORMAT;
import static ru.tv7.nebesatv7.helpers.Constants.TV_MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.TV_PLAYER_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.UTC;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.ZERO_STR;

/**
 * Util methods.
 */
public abstract class Utils {

    public static void showErrorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

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
        Animation animation = new AlphaAnimation(FADE_ANIMATION_START, FADE_ANIMATION_END);
        animation.setDuration(FADE_ANIMATION_DURATION);
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

    public static void setSelectedById(View root, int id, boolean selected) {
        if (root != null) {
            View view = root.findViewById(id);
            if (view != null) {
                view.setSelected(selected);
            }
        }
    }

    public static View getFocusedView(Activity activity) {
        return activity.getCurrentFocus();
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
        Calendar today = Calendar.getInstance(TimeZone.getDefault());
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
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone(UTC));
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

    public static long getLocalTimeInMilliseconds(String time) {
        Calendar calendar = getLocalCalendar();
        calendar.setTimeInMillis(stringToLong(time));

        return calendar.getTimeInMillis();
    }

    public static Calendar getLocalCalendar() {
        return GregorianCalendar.getInstance(TimeZone.getDefault());
    }

    public static int stringToInt(String value) {
        return Integer.parseInt(value);
    }

    public static long stringToLong(String value) {
        return Long.parseLong(value);
    }
}
