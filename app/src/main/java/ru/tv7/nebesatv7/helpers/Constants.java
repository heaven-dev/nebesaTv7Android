package ru.tv7.nebesatv7.helpers;

import java.util.ArrayList;
import java.util.Arrays;

import ru.tv7.nebesatv7.R;


/**
 * Constants.
 */
abstract public class Constants {
    final static public String LOG_TAG = "tv7";

    final static public int VOLLEY_TIMEOUT_VALUE = 60000;

    final static public String STREAM_URL = "https://vod.tv7.fi:443/tv7-ru/_definst_/smil:tv7-ru.smil/playlist.m3u8";

    final static public float PROGRESS_BAR_SIZE = 0.85f;
    final static public int FADE_ANIMATION_DURATION = 900;
    final static public float FADE_ANIMATION_START = 0.0f;
    final static public float FADE_ANIMATION_END = 1.0f;
    final static public String MAIN_FRAGMENT = "main_fragment";
    final static public String VIDEO_PLAYER_FRAGMENT = "video_player_fragment";
    final static public String EXIT_OVERLAY_FRAGMENT = "exit_overlay_fragment";

    final static public String URL_PARAM = "url";
    final static public String TYPE_PARAM = "type";

    final static public boolean SHOW_ANIMATIONS = true;

    final static public int PROGRAM_VISIBLE_IMAGE_COUNT = 11;
    final static public int GUIDE_ELEMENT_COUNT = 10;
    final static public int TIMER_TIMEOUT = 10000;

    final static public String EPG_URL = "https://helsinki.tv7.fi/exodus-interfaces/xmltv.xml";
    final static public String EPG_CHANNEL = "RU1";
    final static public String EPG_LANG = "ru";
    final static public String EPG_DURATION = "3d";
    final static public String EPG_CHANNEL_PARAM = "channel";
    final static public String EPG_LANG_PARAM = "lang";
    final static public String EPG_DURATION_PARAM = "duration";

    final static public String QUESTION_MARK = "?";
    final static public String AMPERSAND = "&";
    final static public String EQUAL = "=";
    final static public String SPACE = " ";
    final static public String PIPE_WITH_SPACES = " | ";
    final static public String DASH = "-";
    final static public String DASH_WITH_SPACES = " - ";
    final static public String T_CHAR = "T";
    final static public String COLON = ":";
    final static public String DOT = ".";
    final static public String MS_STR = "000";
    final static public String ZERO_STR = "0";
    final static public String STR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    final static public String UTC = "UTC";
    final static public String HTTP = "http";
    final static public String HTTPS = "https";

    final static public String PROGRAMME = "programme";
    final static public String START = "start";
    final static public String STOP = "stop";
    final static public String TITLE = "title";
    final static public String DESC = "desc";
    final static public String CATEGORY = "category";
    final static public String ICON = "icon";
    final static public String SRC = "src";

    final static public ArrayList<ComingProgramImageAndTextId> COMING_PROGRAM_IMAGE_AND_TEXT = new ArrayList<>(Arrays.asList(
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

    final static public ArrayList<ProgramRowId> PROGRAM_ROW = new ArrayList<>(Arrays.asList(
        new ProgramRowId(R.id.row1, R.id.row1Time, R.id.row1Title, R.id.row1Desc),
        new ProgramRowId(R.id.row2, R.id.row2Time, R.id.row2Title, R.id.row2Desc),
        new ProgramRowId(R.id.row3, R.id.row3Time, R.id.row3Title, R.id.row3Desc),
        new ProgramRowId(R.id.row4, R.id.row4Time, R.id.row4Title, R.id.row4Desc),
        new ProgramRowId(R.id.row5, R.id.row5Time, R.id.row5Title, R.id.row5Desc),
        new ProgramRowId(R.id.row6, R.id.row6Time, R.id.row6Title, R.id.row6Desc),
        new ProgramRowId(R.id.row7, R.id.row7Time, R.id.row7Title, R.id.row7Desc),
        new ProgramRowId(R.id.row8, R.id.row8Time, R.id.row8Title, R.id.row8Desc),
        new ProgramRowId(R.id.row9, R.id.row9Time, R.id.row9Title, R.id.row9Desc),
        new ProgramRowId(R.id.row10, R.id.row10Time, R.id.row10Title, R.id.row10Desc)
    ));
}
