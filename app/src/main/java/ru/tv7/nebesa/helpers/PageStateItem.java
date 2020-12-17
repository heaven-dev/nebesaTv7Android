package ru.tv7.nebesa.helpers;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class PageStateItem {
    public static final String DATA = "data";
    public static final String SELECTED_POS = "selectedPos";
    public static final String DATA_LENGTH = "dataLength";
    public static final String OFFSET = "offset";
    public static final String SELECTED_DATE_ID = "selectedDateId";

    Map<String, Object> parameters = new HashMap<>();

    public PageStateItem(JSONArray data, Integer selectedPos) {
        parameters.put(DATA, data);
        parameters.put(SELECTED_POS, selectedPos);
    }

    public PageStateItem(JSONArray data, Integer selectedPos, Integer selectedDateId) {
        parameters.put(DATA, data);
        parameters.put(SELECTED_POS, selectedPos);
        parameters.put(SELECTED_DATE_ID, selectedDateId);
    }

    public PageStateItem(JSONArray data, Integer selectedPos, Integer dataLength, Integer offset) {
        parameters.put(DATA, data);
        parameters.put(SELECTED_POS, selectedPos);
        parameters.put(DATA_LENGTH, dataLength);
        parameters.put(OFFSET, offset);
    }

    public Object getValue(String id) {
        if (parameters != null) {
            return parameters.get(id);
        }
        return null;
    }

    public boolean isDataAvailable() {
        if (parameters != null) {
            return parameters.get(DATA) != null;
        }
        return false;
    }

    public void reset() {
        if (parameters != null) {
            parameters.clear();
            parameters = null;
        }
    }
}
