package ru.tv7.taivastv7.helpers;

/**
 * Helper class.
 */
public class ProgramRowId {
    private int rowId;
    private int timeId;
    private int titleId;
    private int descId;

    public ProgramRowId(int rowId, int timeId, int titleId, int descId) {
        this.rowId = rowId;
        this.timeId = timeId;
        this.titleId = titleId;
        this.descId = descId;
    }

    public int getRowId() {
        return rowId;
    }

    public int getTimeId() {
        return timeId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getDescId() {
        return descId;
    }
}
