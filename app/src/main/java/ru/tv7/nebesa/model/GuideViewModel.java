package ru.tv7.nebesa.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.helpers.GuideItem;
import ru.tv7.nebesa.helpers.Utils;

import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;

public class GuideViewModel extends ViewModel {

    private List<GuideItem> guide = new ArrayList<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        this.clearCache();
    }

    /**
     * Returns guide.
     * @return
     */
    synchronized public List<GuideItem> getGuide() {
        return guide;
    }

    /**
     * Adds item to guide.
     * @param g
     */
    synchronized public void addItemToGuide(GuideItem g) {
        if (this.guide == null) {
            this.guide = new ArrayList<>();
        }
        this.guide.add(g);
    }

    /**
     * Clears guide cache.
     */
    synchronized public void clearCache() {
        if (guide != null) {
            guide.clear();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.clearCache(): Cache cleared.");
            }
        }
    }

    /**
     * Get guide item from the list by index (ongoing program + given index).
     * @param index
     * @return
     */
    public GuideItem getEpgItemByIndex(int index) {
        GuideItem guideItem = null;
        try {
            int idx = this.getOngoingProgramIndex() + index;
            if (this.isListItemInIndex(idx)) {
                guideItem = guide.get(idx);
                if (index == 0) {
                    int progressValue = getOngoingProgramProgressValue(guideItem.getStart(), guideItem.getStop());
                    guideItem.setOngoingProgress(progressValue);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getEpgItemByIndex(): Exception: " + e);
            }
        }
        return guideItem;
    }

    /**
     * Check is item in list by index.
     * @param index
     * @return boolean
     */
    public boolean isListItemInIndex(int index) {
        return index >= 0 && index < guide.size();
    }

    /**
     * Returns count (from ongoing program) of next programs from the program list.
     * @return
     */
    public int getCountOfNextPrograms() {
        int size = 0;

        try {
            if (guide != null) {
                size = guide.size() - this.getOngoingProgramIndex();
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getCountOfNextPrograms(): Exception: " + e);
            }

            size = 0;
        }

        return size;
    }

    /**
     * Remove past epg program items from the epg list.
     * @return int - removed count
     */
    public int removePastProgramItems() {
        int index = 0;
        try {
            index = this.getOngoingProgramIndex();
            guide.subList(0, index).clear();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.removePastProgramItems(): Exception: " + e);
            }
        }
        return index;
    }

    /**
     * Returns and stores index of ongoing program.
     * @return
     * @throws Exception
     */
    public int getOngoingProgramIndex() throws Exception {
        int index = 0;
        long now = Utils.getUtcTimeInMilliseconds();

        for(int i = 0; i < guide.size(); i++) {
            GuideItem g = guide.get(i);

            if (g != null && this.isOngoingProgram(g.getStart(), g.getStop()) || Utils.stringToLong(g.getStop()) < now) {
                index = i;
            }
        }

        return index;
    }

    /**
     * Get epg ongoing and coming programs data.
     * @param count
     * @return
     */
    public List<GuideItem> getOngoingAndComingPrograms(int count) {
        List<GuideItem> guideDataList = null;

        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getOngoingAndComingPrograms(): called. Count: " + count);
            }

            int index = this.getOngoingProgramIndex();

            guideDataList = guide.subList(index, index + count);
            if (guideDataList.size() > 0) {
                GuideItem g = guideDataList.get(0);
                if (g != null) {
                    int progressValue = getOngoingProgramProgressValue(g.getStart(), g.getStop());
                    g.setOngoingProgress(progressValue);
                    guideDataList.set(0, g);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getOngoingAndComingPrograms(): Exception: " + e);
            }
        }

        return guideDataList;
    }

    /**
     * Get guide data from between given (count and startIndex) values.
     * @param startIndex
     * @param count
     * @return
     */
    public List<GuideItem> getGuideData(int startIndex, int count) {
        List<GuideItem> guideDataList = null;

        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getOngoingAndComingPrograms(): called. Count: " + count + " Start index: " + startIndex);
            }

            int index = this.getOngoingProgramIndex() + startIndex;

            guideDataList = guide.subList(index, index + count);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "GuideViewModel.getGuideData(): Exception: " + e);
            }
        }
        return guideDataList;
    }

    /**
     * Check is now time between start and stop.
     * @param start
     * @param end
     * @return
     */
    private boolean isOngoingProgram(String start, String end) {
        long now = Utils.getUtcTimeInMilliseconds();
        long s = Utils.stringToLong(start);
        long e = Utils.stringToLong(end);

        return now >= s && now <= e;
    }

    /**
     * Get ongoing program progress value.
     * @param start
     * @param stop
     * @return
     */
    private int getOngoingProgramProgressValue(String start, String stop) {
        Calendar today = Utils.getLocalCalendar();
        today.setTime(new Date());
        long now = today.getTimeInMillis();

        long startTime = Utils.getLocalTimeInMilliseconds(start);
        long stopTime = Utils.getLocalTimeInMilliseconds(stop);

        float duration = (float) (stopTime - startTime);
        float passed = (float) (now - startTime);

        int value = (int) (passed / duration * 100);
        if (value < 0) {
            value = 0;
        }

        if (value > 100) {
            value = 100;
        }

        return value;
    }
}
