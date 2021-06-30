package ru.tv7.nebesa.helpers;

/**
 * Electronic program guide helper class.
 */
public class EpgItem {
    private String start = null;
    private String stop = null;
    private String localStartTime = null;
    private String localEndTime = null;
    private String localStartDate = null;
    private String localEndDate = null;
    private String localStartDateShort = null;
    private Boolean isStartDateToday = null;
    private String title = null;
    private String desc = null;
    private String category = null;
    private String icon = null;
    private Integer ongoingProgress = null;

    public EpgItem(String start, String stop, String localStartTime, String localEndTime, String localStartDate, String localeEndDate,
                   String localStartDateShort, Boolean isStartDateToday, String title, String desc, String category, String icon) {
        this.start = start;
        this.stop = stop;
        this.localStartTime = localStartTime;
        this.localEndTime = localEndTime;
        this.localStartDate = localStartDate;
        this.localEndDate = localeEndDate;
        this.localStartDateShort = localStartDateShort;
        this.isStartDateToday = isStartDateToday;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.icon = icon;
    }

    public EpgItem(EpgItem e) {
        this.start = e.getStart();
        this.stop = e.getStop();
        this.localStartTime = e.getLocalStartTime();
        this.localEndTime = e.getLocalEndTime();
        this.localStartDate = e.getLocalStartDate();
        this.localEndDate = e.getLocalEndDate();
        this.localStartDateShort = e.getLocalStartDateShort();
        this.isStartDateToday = e.getStartDateToday();
        this.title = e.getTitle();
        this.desc = e.getDesc();
        this.category = e.getCategory();
        this.icon = e.getIcon();
        this.ongoingProgress = e.getOngoingProgress();
    }


    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getLocalStartTime() {
        return localStartTime;
    }

    public String getLocalEndTime() {
        return localEndTime;
    }

    public String getLocalStartDate() {
        return localStartDate;
    }

    public String getLocalEndDate() {
        return localEndDate;
    }

    public String getLocalStartDateShort() {
        return localStartDateShort;
    }

    public Boolean getStartDateToday() {
        return isStartDateToday;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getOngoingProgress() {
        return ongoingProgress;
    }

    public void setOngoingProgress(Integer ongoingProgress) {
        this.ongoingProgress = ongoingProgress;
    }
}
