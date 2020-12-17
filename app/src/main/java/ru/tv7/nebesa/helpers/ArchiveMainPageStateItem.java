package ru.tv7.nebesa.helpers;

import org.json.JSONArray;

import java.util.Map;

public class ArchiveMainPageStateItem {
    private int activeRow = 0;
    private int selectedPos = 0;
    private Map<Integer, Integer> colFocus = null;
    private JSONArray visibleSubCategories = null;

    public ArchiveMainPageStateItem(int activeRow, int selectedPos, Map<Integer, Integer> colFocus, JSONArray visibleSubCategories) {
        this.activeRow = activeRow;
        this.selectedPos = selectedPos;
        this.colFocus = colFocus;
        this.visibleSubCategories = visibleSubCategories;
    }

    public int getActiveRow() {
        return activeRow;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public Map<Integer, Integer> getColFocus() {
        return colFocus;
    }

    public JSONArray getVisibleSubCategories() {
        return visibleSubCategories;
    }
}
