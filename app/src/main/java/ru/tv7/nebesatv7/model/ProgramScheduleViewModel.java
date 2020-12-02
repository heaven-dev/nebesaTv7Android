package ru.tv7.nebesatv7.model;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.NebesaTv7;
import ru.tv7.nebesatv7.helpers.EpgItem;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.interfaces.EpgDataLoadedListener;

import static ru.tv7.nebesatv7.helpers.Constants.AMPERSAND;
import static ru.tv7.nebesatv7.helpers.Constants.CATEGORY;
import static ru.tv7.nebesatv7.helpers.Constants.COLON;
import static ru.tv7.nebesatv7.helpers.Constants.DASH;
import static ru.tv7.nebesatv7.helpers.Constants.DESC;
import static ru.tv7.nebesatv7.helpers.Constants.DOT;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_CHANNEL;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_CHANNEL_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_DURATION;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_DURATION_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_LANG;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_LANG_PARAM;
import static ru.tv7.nebesatv7.helpers.Constants.EPG_URL;
import static ru.tv7.nebesatv7.helpers.Constants.EQUAL;
import static ru.tv7.nebesatv7.helpers.Constants.ICON;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.MS_STR;
import static ru.tv7.nebesatv7.helpers.Constants.PROGRAMME;
import static ru.tv7.nebesatv7.helpers.Constants.QUESTION_MARK;
import static ru.tv7.nebesatv7.helpers.Constants.SRC;
import static ru.tv7.nebesatv7.helpers.Constants.START;
import static ru.tv7.nebesatv7.helpers.Constants.STOP;
import static ru.tv7.nebesatv7.helpers.Constants.STR_DATE_FORMAT;
import static ru.tv7.nebesatv7.helpers.Constants.TITLE;
import static ru.tv7.nebesatv7.helpers.Constants.T_CHAR;
import static ru.tv7.nebesatv7.helpers.Constants.UTC;
import static ru.tv7.nebesatv7.helpers.Constants.VOLLEY_TIMEOUT_VALUE;

public class ProgramScheduleViewModel extends ViewModel {

    private List<EpgItem> programmeList = new ArrayList<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        this.clearCache();
    }

    /**
     * Get epg item from the list by index (ongoing program + given index).
     * @param index
     * @return
     */
    public EpgItem getEpgItemByIndex(int index) {
        EpgItem epgItem = null;
        try {
            int idx = this.getOngoingProgramIndex() + index;
            if (this.isListItemInIndex(idx)) {
                epgItem = programmeList.get(idx);
                if (index == 0) {
                    int progressValue = getOngoingProgramProgressValue(epgItem.getStart(), epgItem.getStop());
                    epgItem.setOngoingProgress(progressValue);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgItemByIndex(): Exception: " + e);
            }
        }
        return epgItem;
    }

    /**
     * Check is item in list by index.
     * @param index
     * @return boolean
     */
    public boolean isListItemInIndex(int index) {
        return index >= 0 && index < programmeList.size();
    }

    /**
     * Returns count (from ongoing program) of next programs from the program list.
     * @return
     */
    public int getCountOfNextPrograms() {
        int size = 0;

        try {
            if (programmeList != null) {
                size = programmeList.size() - this.getOngoingProgramIndex();
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getCountOfNextPrograms(): Exception: " + e);
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
            programmeList.subList(0, index).clear();
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.removePastProgramItems(): Exception: " + e);
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

        for(int i = 0; i < programmeList.size(); i++) {
            EpgItem e = programmeList.get(i);

            if (e != null && this.isOngoingProgram(e.getStart(), e.getStop()) || Utils.stringToLong(e.getStop()) < now) {
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
    public List<EpgItem> getOngoingAndComingPrograms(int count) {
        List<EpgItem> epgDataList = null;

        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getOngoingAndComingPrograms(): called. Count: " + count);
            }

            int index = this.getOngoingProgramIndex();

            epgDataList = programmeList.subList(index, index + count);
            if (epgDataList.size() > 0) {
                EpgItem e = epgDataList.get(0);
                if (e != null) {
                    int progressValue = getOngoingProgramProgressValue(e.getStart(), e.getStop());
                    e.setOngoingProgress(progressValue);
                    epgDataList.set(0, e);
                }
            }
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getOngoingAndComingPrograms(): Exception: " + e);
            }
        }

        return epgDataList;
    }

    /**
     * Get guide data from between given (count and startIndex) values.
     * @param startIndex
     * @param count
     * @return
     */
    public List<EpgItem> getGuideData(int startIndex, int count) {
        List<EpgItem> epgDataList = null;

        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getOngoingAndComingPrograms(): called. Count: " + count + " Start index: " + startIndex);
            }

            int index = this.getOngoingProgramIndex() + startIndex;

            epgDataList = programmeList.subList(index, index + count);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getGuideData(): Exception: " + e);
            }
        }
        return epgDataList;
    }

    /**
     * Reads epg xml data from server.
     * @param epgDataLoadedListener
     */
    public void getEpgData(final EpgDataLoadedListener epgDataLoadedListener) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): called.");
            }

            this.clearCache();

            final String url = EPG_URL +
                    QUESTION_MARK + EPG_CHANNEL_PARAM + EQUAL + EPG_CHANNEL +
                    AMPERSAND + EPG_LANG_PARAM + EQUAL + EPG_LANG +
                    AMPERSAND + EPG_DURATION_PARAM + EQUAL + EPG_DURATION;

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): Epg URL: " + url);
            }

            StringRequest jsonRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): onResponse()");
                            }

                            processEpgXmlData(response);
                            epgDataLoadedListener.onEpgDataLoaded();
                        }
                        catch(Exception e) {
                            epgDataLoadedListener.onEpgDataLoadError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): ErrorListener(): Error fetching json: " + error.toString());
                        }
                        epgDataLoadedListener.onEpgDataLoadError(error.getMessage());
                    }
                }
            );

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    VOLLEY_TIMEOUT_VALUE,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            NebesaTv7.getInstance().addToRequestQueue(jsonRequest);
        }
        catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): Error downloading EPG data. Exception: " + e);
            }
            epgDataLoadedListener.onEpgDataLoadError(e.getMessage());
        }
    }

    /**
     * Parses xml data.
     * @param xmlStr
     * @throws Exception
     */
    private void processEpgXmlData(String xmlStr) throws Exception{
        try {
            String errorText = "Error parsing xml data!";

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlStr));
            Document document = builder.parse(is);

            NodeList programmes = document.getElementsByTagName(PROGRAMME);
            if (programmes != null) {
                for (int i = 0; i < programmes.getLength(); i++) {
                    Node programmeItem = programmes.item(i);
                    if (programmeItem == null) {
                        throw new Exception(errorText);
                    }

                    NamedNodeMap namedNodeMap = programmeItem.getAttributes();
                    if (namedNodeMap == null) {
                        throw new Exception(errorText);
                    }

                    Node nodeStart = namedNodeMap.getNamedItem(START);
                    Node nodeStop = namedNodeMap.getNamedItem(STOP);
                    if (nodeStart == null || nodeStop == null) {
                        throw new Exception(errorText);
                    }

                    String start = nodeStart.getNodeValue();
                    String stop = nodeStop.getNodeValue();
                    if (start == null || stop == null) {
                        throw new Exception(errorText);
                    }

                    start = this.getUtcTimeInMillisecondsByDate(this.createUtcDateTimeString(start));
                    stop = this.getUtcTimeInMillisecondsByDate(this.createUtcDateTimeString(stop));

                    NodeList programmeChilds = programmeItem.getChildNodes();
                    if (programmeChilds == null) {
                        throw new Exception(errorText);
                    }

                    String title = null;
                    String desc = null;
                    String category = null;
                    String icon = null;
                    for (int j = 0; j < programmeChilds.getLength(); j++) {
                        Node childItem = programmeChilds.item(j);
                        if (childItem == null) {
                            throw new Exception(errorText);
                        }

                        if (childItem.getNodeName().equals(TITLE)) {
                            title = childItem.getTextContent();
                        }
                        else if (childItem.getNodeName().equals(DESC)) {
                            desc = childItem.getTextContent();
                        }
                        else if (childItem.getNodeName().equals(CATEGORY)) {
                            category = childItem.getTextContent();
                        }
                        else if (childItem.getNodeName().equals(ICON)) {
                            NamedNodeMap iconNodeMap = childItem.getAttributes();
                            if (iconNodeMap == null) {
                                throw new Exception(errorText);
                            }

                            Node nodeSrc = iconNodeMap.getNamedItem(SRC);
                            if (nodeSrc == null) {
                                throw new Exception(errorText);
                            }

                            icon = nodeSrc.getNodeValue();
                        }
                    }

                    EpgItem epgItem = new EpgItem(start, stop, Utils.createLocalTimeString(start),
                                Utils.createLocalTimeString(stop), Utils.createLocalDateString(start) , Utils.createLocalDateString(stop),
                                this.isStartDateToday(start), title, desc, category, icon);

                    programmeList.add(epgItem);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.getEpgData(): Exception: " + e);
            }

            throw new Exception(e);
        }
    }

    /**
     * Creates UTC date time string.
     * @param value
     * @return
     */
    private String createUtcDateTimeString(String value) {
        return value.substring(0, 4) + DASH + value.substring(4, 6) + DASH + value.substring(6, 8) + T_CHAR +
                value.substring(8, 10) + COLON + value.substring(10, 12)  + COLON + value.substring(12, 14) + DOT + MS_STR;
    }

    /**
     * Get UTC time in ms by given UTC date time string.
     * @param utcStr
     * @return
     * @throws Exception
     */
    public String getUtcTimeInMillisecondsByDate(String utcStr) throws Exception {
        DateFormat format = new SimpleDateFormat(STR_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(UTC));

        Calendar calendar = Utils.getUtcCalendar();
        calendar.setTime(format.parse(utcStr));
        return String.valueOf(calendar.getTimeInMillis());
    }

    /**
     * Check is start date time on today.
     * @param time
     * @return
     * @throws Exception
     */
    private boolean isStartDateToday(String time) throws Exception {
        Calendar today = Utils.getLocalCalendar();
        today.setTime(new Date());

        Calendar calendar = Utils.getLocalCalendar();
        calendar.setTimeInMillis(Utils.stringToLong(time));

        return today.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                && today.get(Calendar.MONTH) + 1 == calendar.get(Calendar.MONTH) + 1
                && today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }

    /**
     * Check is now time between start and stop.
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    private boolean isOngoingProgram(String start, String end) throws Exception {
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
     * @throws Exception
     */
    private int getOngoingProgramProgressValue(String start, String stop) throws Exception {
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

    private void clearCache() {
        if (programmeList != null) {
            programmeList.clear();

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "ProgramScheduleViewModel.clearCache(): Cache cleared.");
            }
        }
    }
}
