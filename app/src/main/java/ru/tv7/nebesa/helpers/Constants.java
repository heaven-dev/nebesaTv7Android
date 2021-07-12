package ru.tv7.nebesa.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.tv7.nebesa.R;

/**
 * Constants.
 */
abstract public class Constants {
    public static final String LOG_TAG = "tv7";

    public static final boolean VOLLEY_CACHE = false;
    public static final int VOLLEY_TIMEOUT_VALUE = 30000;

    public static final int SOMETHING_WENT_WRONG_ERROR = 100;
    public static final int NO_NETWORK_CONNECTION_ERROR = 101;
    public static final int NETWORK_REQUEST_FAILED_ERROR = 102;
    public static final int NETWORK_REQUEST_TIMEOUT_ERROR = 103;

    public static final int STREAM_ERROR_RETRY_DELAY = 3000;

    public static final String STREAM_URL = "https://vod.tv7.fi:443/tv7-ru/_definst_/smil:tv7-ru.smil/playlist.m3u8";
    public static final String ARCHIVE_BASE_URL = "https://www.nebesatv7.com/wp-json/tv7-api/v1/";
    public static final String ARCHIVE_VIDEO_URL = "https://vod.tv7.fi:443/vod/_definst_/mp4:_LINK_PATH_/playlist.m3u8";

    public static final String ARCHIVE_LANGUAGE = "RU1";
    public static final String TRANSLATION_LANG_ID = "ru";

    public static final String SUBTITLES_URL = "https://vod.tv7.fi:4430/vod/";
    public static final String SEARCH_URL = "https://edom.tv7.fi:8443/search1.2/SearchServlet";

    public static final float PROGRESS_BAR_SIZE = 0.85f;
    public static final int FADE_IN_ANIMATION_DURATION = 900;
    public static final float FADE_IN_ANIMATION_START = 0.0f;
    public static final float FADE_IN_ANIMATION_END = 1.0f;

    public static final int ONGOING_PROGRAM_ANIMATION_DURATION = 2000;

    public static final int FAVORITES_TEXT_ANIMATION_DURATION = 1500;
    public static final float FAVORITES_TEXT_ANIMATION_START = 1.0f;
    public static final float FAVORITES_TEXT_ANIMATION_END = 0.0f;
    public static final long FAVORITES_TEXT_ANIMATION_START_OFFSET = 500;

    public static final int PAUSE_START_ICON_ANIMATION_DURATION = 1100;
    public static final float PAUSE_START_ICON_ANIMATION_START = 1.0f;
    public static final float PAUSE_START_ICON_ANIMATION_END = 0.0f;
    public static final long PAUSE_START_ICON_ANIMATION_START_OFFSET = 250;

    public static final String SCROLL_Y = "scrollY";
    public static final long ARCHIVE_MAIN_SCROLL_Y_DURATION = 150;

    public static final String TV_MAIN_FRAGMENT = "tv_main_fragment";
    public static final String ARCHIVE_MAIN_FRAGMENT = "archive_main_fragment";
    public static final String TV_PLAYER_FRAGMENT = "tv_player_fragment";
    public static final String ARCHIVE_PLAYER_FRAGMENT = "archive_player_fragment";
    public static final String CATEGORIES_FRAGMENT = "categories_fragment";
    public static final String SERIES_FRAGMENT = "series_fragment";
    public static final String GUIDE_FRAGMENT = "guide_fragment";
    public static final String PROGRAM_INFO_FRAGMENT = "program_info_fragment";
    public static final String SERIES_INFO_FRAGMENT = "series_info_fragment";
    public static final String SEARCH_FRAGMENT = "search_fragment";
    public static final String SEARCH_RESULT_FRAGMENT = "search_result_fragment";
    public static final String FAVORITES_FRAGMENT = "favorites_fragment";
    public static final String CHANNEL_INFO_FRAGMENT = "channel_info_fragment";
    public static final String ABOUT_FRAGMENT = "about_fragment";
    public static final String ERROR_FRAGMENT = "error_fragment";
    public static final String EXIT_OVERLAY_FRAGMENT = "exit_overlay_fragment";

    public static final String CHANNEL_URL_PARAM = "channel_url";
    public static final String _LINK_PATH_ = "_LINK_PATH_";

    public static final boolean SHOW_ANIMATIONS = true;

    public static final int PROGRAM_VISIBLE_IMAGE_COUNT = 11;
    public static final int GUIDE_ELEMENT_COUNT = 10;
    public static final int GUIDE_TIMER_TIMEOUT = 10000;
    public static final int VIDEO_POSITION_TIMEOUT = 1000;
    public static final int VIDEO_CONTROLS_TIMEOUT = 6000;

    public static final float CUE_LINE_POSITION = 0.82f;
    public static final int SUBTITLE_BACKGROUND = 0x99000000;

    public static final int DATES_COUNT = 7;
    public static final int ARCHIVE_MAIN_NO_SEL_POS = -1;
    public static final int NO_MORE_PAGING_DATA = -1;

    public static final int PROGRAM_LIST_MIN_SIZE = 22;

    public static final String PNID_PARAM = "pnid";
    public static final String AUDIO_INDEX_PARAM = "audioindex";

    public static final String AUDIO_INDEX_ENABLE_LANG = "RU1";

    public static final String TV_BRAND = "AndroidTV";

    public static final String OS_VERSION = "os.version";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";

    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUAL = "=";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String PIPE_WITH_SPACES = " | ";
    public static final String DASH = "-";
    public static final String DASH_WITH_SPACES = " - ";
    public static final String SLASH_WITH_SPACES = " / ";
    public static final String COLON = ":";
    public static final String COLON_WITH_SPACE = ": ";
    public static final String DOT = ".";
    public static final String ZERO_STR = "0";
    public static final String NEGATIVE_ONE_STR = "-1";
    public static final String UTC = "UTC";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String UTF_8 = "UTF-8";
    public static final String ID_NULL = "id=null";

    public static final String ONE_STR = "1";
    public static final String TWO_STR = "2";
    public static final String POSITION = "position";
    public static final String PERCENT = "percent";

    public static final String FAVORITES_SP_TAG = "favorites_shared_pref";
    public static final String FAVORITES_SP_DEFAULT = "[]";

    public static final String VIDEO_STATUSES_SP_TAG = "video_statuses_shared_pref";
    public static final String VIDEO_STATUSES_SP_DEFAULT = "[]";

    public static final String SAVED_SEARCH_SP_TAG = "saved_search_shared_pref";
    public static final String SAVED_SEARCH_SP_DEFAULT = "[]";
    public static final int SAVED_SEARCH_MAX_COUNT = 12;

    public static final int TOOLBAR_HEIGHT = 60;
    public static final int ARCHIVE_MAIN_TITLE_HEIGHT = 30;

    public static final String GET_= "get_";
    public static final String RECOMMENDATIONS_METHOD = "tv7_vod_recommendations";
    public static final String BROADCAST_RECOMMENDATIONS_METHOD = "tv7_broadcast_recommendations";
    public static final String MOST_VIEWED_METHOD = "get_tv7_vod_previousweek_stats";
    public static final String NEWEST_METHOD = "tv7_vod_new";
    public static final String SERIES_METHOD = "tv7_series";
    public static final String PARENT_CATEGORIES_METHOD = "tv7_parent_categories";
    public static final String SUB_CATEGORIES_METHOD = "tv7_sub_categories";
    public static final String TRANSLATION_METHOD = "tv7_translation";
    public static final String CATEGORY_PROGRAMS_METHOD = "tv7_category_programs";
    public static final String PROGRAM_INFO_METHOD = "tv7_program_info";
    public static final String SERIES_INFO_METHOD = "tv7_series_info";
    public static final String SERIES_PROGRAMS_METHOD = "tv7_series_programs";
    public static final String GUIDE_DATE_METHOD = "tv7_tv_guide_date";
    public static final String SEARCH_METHOD = "search";

    public static final String ZERO_DATE_TIME = "00.00.0000 00:00";
    public static final String ZERO_DATE = "00.00.0000";
    public static final String ZERO_DURATION = "00:00:00";
    public static final String TIME_STAMP_FORMAT = "%02d:%02d:%02d";

    public static final String DATE_PARAM = "date";
    public static final String LIMIT_PARAM = "limit";
    public static final String OFFSET_PARAM = "offset";
    public static final String VOD_PARAM = "vod";
    public static final String PROGRAM_ID_PARAM = "program_id";
    public static final String CATEGORY_ID_PARAM = "category_id";
    public static final String SERIES_ID_PARAM = "series_id";
    public static final String QUERY_PARAM = "query";

    public static final int RECOMMENDED_PROGRAMS_LIMIT = 30;
    public static final int NEWEST_LIMIT = 30;
    public static final int CATEGORY_PROGRAMS_SEARCH_LIMIT = 30;
    public static final int SERIES_PROGRAMS_SEARCH_LIMIT = 30;

    public static final String NULL_VALUE = "null";
    public static final String ID = "id";
    public static final String IMAGE_PATH = "image_path";
    public static final String LINK_PATH = "link_path";
    public static final String EPISODE_NUMBER = "episode_number";
    public static final String SID = "sid";
    public static final String SERIES_ID = "series_id";
    public static final String FIRST_BROADCAST = "first_broadcast";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String BROADCAST_DATE_TIME = "broadcast_date_time";
    public static final String BROADCAST_DATE = "broadcast_date";
    public static final String DURATION = "duration";
    public static final String NAME = "name";
    public static final String SERIES_NAME = "series_name";
    public static final String SERIES = "series";
    public static final String SNAME = "sname";
    public static final String SERIES_AND_NAME = "series_and_name";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String IS_VISIBLE_ON_VOD = "is_visible_on_vod";
    public static final String VISIBLE_ON_VOD_SINCE = "visible_on_vod_since";
    public static final String TYPE = "type";
    public static final String PATH = "path";
    public static final String PLAY = "play";
    public static final String LANG_ID = "lang_id";
    public static final String IS_SUBTITLE = "is_subtitle";
    public static final String PARENT_ID = "parent_id";
    public static final String PARENT_NAME = "parent_name";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String TIME = "time";
    public static final String END_TIME = "end_time";
    public static final String FORMATTED_START_TIME = "formatted_start_time";
    public static final String FORMATTED_END_TIME = "formatted_end_time";
    public static final String START_END_TIME = "start_end_time";
    public static final String ONGOING_PROGRAM = "ongoing_program";
    public static final String ONGOING_PROGRAM_INDEX = "ongoing_program_index";
    public static final String DATE_INDEX = "date_index";
    public static final String GUIDE_DATA = "guide_data";
    public static final String BACK_TEXT = "back_text";
    public static final String RESULTS = "results";
    public static final String ASPECT_RATIO = "aspect_ratio";
    public static final String ASPECT_RATIO_16_9 = "16:9";
    public static final String IS_SERIES = "is_series";

    public static final long CACHE_EXPIRATION_TIME = 1800000;

    public static final int ARCHIVE_MAIN_ROW_COUNT = 5;
    public static final int VIDEO_SEEK_STEP_SECONDS = 10;

    public static final int RECOMMENDATIONS_ROW_ID = 0;
    public static final int MOST_VIEWED_ROW_ID = 1;
    public static final int NEWEST_ROW_ID = 2;
    public static final int CATEGORIES_ROW_ID = 3;
    public static final int SERIES_ROW_ID = 4;

    public static final float CATEGORY_IMAGE_SIZE_IN_PERCENT = 0.6f;

    public static final List<ComingProgramImageAndTextId> COMING_PROGRAM_IMAGE_AND_TEXT = new ArrayList<>(Arrays.asList(
        new ComingProgramImageAndTextId(R.id.image1, R.id.image1Text),
        new ComingProgramImageAndTextId(R.id.image2, R.id.image2Text),
        new ComingProgramImageAndTextId(R.id.image3, R.id.image3Text),
        new ComingProgramImageAndTextId(R.id.image4, R.id.image4Text),
        new ComingProgramImageAndTextId(R.id.image5, R.id.image5Text),
        new ComingProgramImageAndTextId(R.id.image6, R.id.image6Text),
        new ComingProgramImageAndTextId(R.id.image7, R.id.image7Text),
        new ComingProgramImageAndTextId(R.id.image8, R.id.image8Text),
        new ComingProgramImageAndTextId(R.id.image9, R.id.image9Text),
        new ComingProgramImageAndTextId(R.id.image10, R.id.image10Text)
    ));

    public static final List<GuideRowId> GUIDE_ROWS = new ArrayList<>(Arrays.asList(
        new GuideRowId(R.id.row1, R.id.row1Time, R.id.row1Title, R.id.row1Desc),
        new GuideRowId(R.id.row2, R.id.row2Time, R.id.row2Title, R.id.row2Desc),
        new GuideRowId(R.id.row3, R.id.row3Time, R.id.row3Title, R.id.row3Desc),
        new GuideRowId(R.id.row4, R.id.row4Time, R.id.row4Title, R.id.row4Desc),
        new GuideRowId(R.id.row5, R.id.row5Time, R.id.row5Title, R.id.row5Desc),
        new GuideRowId(R.id.row6, R.id.row6Time, R.id.row6Title, R.id.row6Desc),
        new GuideRowId(R.id.row7, R.id.row7Time, R.id.row7Title, R.id.row7Desc),
        new GuideRowId(R.id.row8, R.id.row8Time, R.id.row8Title, R.id.row8Desc),
        new GuideRowId(R.id.row9, R.id.row9Time, R.id.row9Title, R.id.row9Desc),
        new GuideRowId(R.id.row10, R.id.row10Time, R.id.row10Title, R.id.row10Desc)
    ));

    public static final int SIDEBAR_MENU_ICON_WIDTH = 38;

    public static final List<MenuItem> MENU_ITEM_IDS = new ArrayList<>(Arrays.asList(
        new MenuItem(R.id.tvMenuContainer, R.id.tvMenuText),
        new MenuItem(R.id.archiveMenuContainer, R.id.archiveMenuText),
        new MenuItem(R.id.guideMenuContainer, R.id.guideMenuText),
        new MenuItem(R.id.searchMenuContainer, R.id.searchMenuText),
        new MenuItem(R.id.favoritesMenuContainer, R.id.favoritesMenuText),
        new MenuItem(R.id.channelInfoMenuContainer, R.id.channelInfoMenuText),
        new MenuItem(R.id.aboutMenuContainer, R.id.aboutMenuText)
    ));

    public static final List<Integer> ARCHIVE_MAIN_CONTENT_ROW_IDS = new ArrayList<>(Arrays.asList(
        R.id.recommendContainer,
        R.id.mostViewedContainer,
        R.id.newestContainer,
        R.id.categoriesContainer,
        R.id.topicalSeriesContainer
    ));

    public static final List<Integer> GUIDE_DATE_IDS = new ArrayList<>(Arrays.asList(
        R.id.date_0,
        R.id.date_1,
        R.id.date_2,
        R.id.date_3,
        R.id.date_4,
        R.id.date_5,
        R.id.date_6
    ));

    public static final List<Integer> SAVED_SEARCH_IDS = new ArrayList<>(Arrays.asList(
        R.id.ss_0,
        R.id.ss_1,
        R.id.ss_2,
        R.id.ss_3,
        R.id.ss_4,
        R.id.ss_5,
        R.id.ss_6,
        R.id.ss_7,
        R.id.ss_8,
        R.id.ss_9,
        R.id.ss_10,
        R.id.ss_11
    ));

    public static final List<Integer> CLEAR_MENU_IDS = new ArrayList<>(Arrays.asList(
        R.id.cm_0,
        R.id.cm_1,
        R.id.cm_2
    ));

    public static final int SPACE_BUTTON = 1;
    public static final int BACKSPACE_BUTTON = 2;
    public static final int UPPERCASE_BUTTON = 3;
    public static final int SPECIAL_CHARS_BUTTON = 4;

    public static final Map<Integer , Integer> SEARCH_TEXT_MODE_BUTTONS = new HashMap<Integer , Integer>() {{
        put(R.id.k_2_8, SPACE_BUTTON);
        put(R.id.k_2_9, BACKSPACE_BUTTON);
        put(R.id.k_2_10, UPPERCASE_BUTTON);
        put(R.id.k_2_11, SPECIAL_CHARS_BUTTON);
    }};

    public static final Map<Integer , Integer> SEARCH_SPECIAL_CHAR_MODE_BUTTONS = new HashMap<Integer , Integer>() {{
        put(R.id.k_2_8, SPACE_BUTTON);
        put(R.id.k_2_9, BACKSPACE_BUTTON);
        put(R.id.k_2_10, SPECIAL_CHARS_BUTTON);
    }};
}
