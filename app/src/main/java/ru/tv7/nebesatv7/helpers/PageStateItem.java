package ru.tv7.nebesatv7.helpers;

import org.json.JSONArray;

public class PageStateItem {
    private JSONArray data = null;
    private Integer selectedPos = null;
    private Integer dataLength = 0;
    private Integer offset = 0;

    public PageStateItem(JSONArray data, Integer selectedPos, Integer dataLength, Integer offset) {
        this.data = data;
        this.selectedPos = selectedPos;
        this.dataLength = dataLength;
        this.offset = offset;
    }

    public JSONArray getData() {
        return data;
    }

    public Integer getSelectedPos() {
        return selectedPos;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public Integer getOffset() {
        return offset;
    }

    public boolean isDataAvailable() {
        return data != null;
    }

    public void reset() {
        data = null;
        selectedPos = null;
        dataLength = null;
        offset = null;
    }
}
