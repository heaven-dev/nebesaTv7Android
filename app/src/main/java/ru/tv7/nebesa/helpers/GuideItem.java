package ru.tv7.nebesa.helpers;

/**
 * Electronic program guide helper class.
 */
public class GuideItem {
    private String start = null;
    private String stop = null;
    private String imagePath = null;
    private String caption = null;
    private String startAndEndTime = null;
    private String startDate = null;
    private String endDate = null;
    private String startTime = null;
    private String endTime = null;
    private String broadcastDate = null;
    private String broadcastDateTime = null;
    private String duration = null;
    private String series = null;
    private String name = null;
    private String category = null;
    private Integer sid = null;
    private Integer cid = null;
    private Integer id = null;
    private Integer episodeNumber = null;
    private Integer isVisibleOnVod = null;
    private String seriesAndName = null;
    private Boolean isStartDateToday = null;
    private Integer dateIndex = null;
    private Integer ongoingProgress = null;

    public GuideItem(
            String start,
            String stop,
            String imagePath,
            String caption,
            String startAndEndTime,
            String startDate,
            String endDate,
            String startTime,
            String endTime,
            String broadcastDate,
            String broadcastDateTime,
            String duration,
            String series,
            String name,
            String category,
            Integer sid,
            Integer cid,
            Integer id,
            Integer episodeNumber,
            Integer isVisibleOnVod,
            String seriesAndName,
            Boolean isStartDateToday,
            Integer dateIndex) {
        this.start = start;
        this.stop = stop;
        this.imagePath = imagePath;
        this.caption = caption;
        this.startAndEndTime = startAndEndTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.broadcastDate = broadcastDate;
        this.broadcastDateTime = broadcastDateTime;
        this.duration = duration;
        this.series = series;
        this.name = name;
        this.category = category;
        this.sid = sid;
        this.cid = cid;
        this.id = id;
        this.episodeNumber = episodeNumber;
        this.isVisibleOnVod = isVisibleOnVod;
        this.seriesAndName = seriesAndName;
        this.isStartDateToday = isStartDateToday;
        this.dateIndex = dateIndex;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCaption() {
        return caption;
    }

    public String getStartAndEndTime() {
        return startAndEndTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getBroadcastDate() {
        return broadcastDate;
    }

    public String getBroadcastDateTime() {
        return broadcastDateTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getSeries() {
        return series;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Integer getSid() {
        return sid;
    }

    public Integer getCid() {
        return cid;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public Integer getIsVisibleOnVod() {
        return isVisibleOnVod;
    }

    public String getSeriesAndName() {
        return seriesAndName;
    }

    public Boolean getStartDateToday() {
        return isStartDateToday;
    }

    public Integer getOngoingProgress() {
        return ongoingProgress;
    }

    public Integer getDateIndex() {
        return dateIndex;
    }

    public void setOngoingProgress(Integer ongoingProgress) {
        this.ongoingProgress = ongoingProgress;
    }
}
