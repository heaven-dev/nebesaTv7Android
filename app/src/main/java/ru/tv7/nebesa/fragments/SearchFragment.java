package ru.tv7.nebesa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;

import java.util.List;

import ru.tv7.nebesa.BuildConfig;
import ru.tv7.nebesa.R;
import ru.tv7.nebesa.enums.SearchKeyboardType;
import ru.tv7.nebesa.helpers.KeyboardChars;
import ru.tv7.nebesa.helpers.Sidebar;
import ru.tv7.nebesa.helpers.Utils;
import ru.tv7.nebesa.model.SharedCacheViewModel;

import static ru.tv7.nebesa.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.BACKSPACE_BUTTON;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_IDS;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_MAX_COUNT;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_SP_DEFAULT;
import static ru.tv7.nebesa.helpers.Constants.SAVED_SEARCH_SP_TAG;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_RESULT_FRAGMENT;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_SPECIAL_CHAR_MODE_BUTTONS;
import static ru.tv7.nebesa.helpers.Constants.SEARCH_TEXT_MODE_BUTTONS;
import static ru.tv7.nebesa.helpers.Constants.SPACE;
import static ru.tv7.nebesa.helpers.Constants.SPACE_BUTTON;
import static ru.tv7.nebesa.helpers.Constants.SPECIAL_CHARS_BUTTON;
import static ru.tv7.nebesa.helpers.Constants.UPPERCASE_BUTTON;

/**
 * Search fragment. Show keys to write search string.
 */
public class SearchFragment extends Fragment {

    private View root = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;
    private SearchKeyboardType searchKeyboardType = SearchKeyboardType.LOWERCASE;

    private boolean savedSearchContainerVisible = false;

    /**
     * Default constructor.
     */
    public SearchFragment() {

    }

    /**
     * Creates and returns a new instance of this search fragment.
     * @return
     */
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    /**
     * onCreate() - Android lifecycle method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onCreate() called.");
            }

            sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onCreate(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
    }

    /**
     * onCreateView() - Android lifecycle method.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            root = inflater.inflate(R.layout.fragment_search, container, false);

            RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
            if (contentContainer != null) {
                Utils.fadePageAnimation(contentContainer);
            }

            menuTexts = Sidebar.getMenuTextItems(root);
            Sidebar.setSelectedMenuItem(root, R.id.searchMenuContainer);

            searchKeyboardType = SearchKeyboardType.LOWERCASE;

            this.initializeButtons();
            this.initializeSearchAndClearButtons();

            Utils.requestFocusById(root, R.id.k_0_0);

            String searchString = sharedCacheViewModel.getSearchString();
            if (searchString != null) {
                TextView searchText = root.findViewById(R.id.searchText);
                if (searchText != null) {
                    searchText.setText(searchString);
                }
            }

            JSONArray jsonArray = this.getSavedSearches();
            if (jsonArray != null && jsonArray.length() > 0) {
                LinearLayout savedSearchKey = root.findViewById(R.id.savedSearchKey);
                if (savedSearchKey != null) {
                    savedSearchKey.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onCreateView(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }
        return root;
    }

    /**
     * Handles key down events - remote control events.
     * @param keyCode
     * @param events
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent events) {
        try {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onKeyDown(): keyCode: " + keyCode);
            }

            View focusedView = Utils.getFocusedView(getActivity());
            if (focusedView == null) {
                return false;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_CENTER: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    int focusedMenu = Sidebar.getFocusedMenuItem(root);
                    if (focusedMenu == R.id.searchMenuContainer) {
                        this.focusOutFromSideMenu();
                    }
                    else {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "SearchFragment.onKeyDown(): Selected sidebar menu: " + focusedMenu);
                        }

                        Sidebar.menuItemSelected(focusedMenu, getActivity(), sharedCacheViewModel);
                    }
                }
                else if (this.savedSearchContainerVisible) {
                    TextView tv = root.findViewById(focusedView.getId());
                    if (tv != null) {
                        String savedSearchText = tv.getText().toString();
                        tv = root.findViewById(R.id.searchText);
                        if (tv != null) {
                            tv.setText(savedSearchText);
                            this.search(tv);
                        }
                    }
                }
                else {
                    int id = focusedView.getId();
                    TextView searchText = root.findViewById(R.id.searchText);

                    if (id == R.id.searchKey) {
                        this.search(searchText);
                    }
                    else if (id == R.id.clearKey) {
                        if (searchText != null) {
                            searchText.setText(EMPTY);
                        }
                    }
                    else if (id == R.id.savedSearchKey) {
                        this.showHideSavedSearch();
                    }
                    else {
                        if (focusedView instanceof TextView) {
                            TextView view = (TextView)focusedView;

                            String text = searchText.getText().toString();
                            if (text != null && view != null) {
                                String newChar = view.getText().toString();
                                if (newChar != null) {
                                    searchText.setBackgroundResource(R.drawable.search_box);

                                    text += newChar;
                                    searchText.setText(text);
                                }
                            }
                        }
                        else if (focusedView instanceof LinearLayout) {
                            LinearLayout view = (LinearLayout)focusedView;

                            int buttonId = view.getId();

                            if (searchKeyboardType == SearchKeyboardType.LOWERCASE || searchKeyboardType == SearchKeyboardType.UPPERCASE) {
                                buttonId = SEARCH_TEXT_MODE_BUTTONS.get(buttonId);
                            }
                            else if (searchKeyboardType == SearchKeyboardType.SPECIAL) {
                                buttonId = SEARCH_SPECIAL_CHAR_MODE_BUTTONS.get(buttonId);
                            }

                            if (buttonId == SPACE_BUTTON) {
                                String text = searchText.getText().toString();
                                if (text != null) {
                                    searchText.setBackgroundResource(R.drawable.search_box);

                                    text += SPACE;
                                    searchText.setText(text);
                                }
                            }
                            else if (buttonId == BACKSPACE_BUTTON) {
                                String text = searchText.getText().toString();
                                if (text != null && text.length() > 0) {
                                    text = text.substring(0, text.length() - 1);
                                    searchText.setText(text);
                                }
                            }
                            else if (buttonId == UPPERCASE_BUTTON) {
                                if (searchKeyboardType == SearchKeyboardType.LOWERCASE) {
                                    searchKeyboardType = SearchKeyboardType.UPPERCASE;
                                }
                                else {
                                    searchKeyboardType = SearchKeyboardType.LOWERCASE;
                                }

                                this.initializeButtons();

                                Utils.requestFocusById(root, id);
                            }
                            else if (buttonId == SPECIAL_CHARS_BUTTON) {
                                int focusedId = 0;
                                if (searchKeyboardType == SearchKeyboardType.LOWERCASE || searchKeyboardType == SearchKeyboardType.UPPERCASE) {
                                    searchKeyboardType = SearchKeyboardType.SPECIAL;
                                    focusedId = R.id.k_2_10;
                                }
                                else {
                                    searchKeyboardType = SearchKeyboardType.LOWERCASE;
                                    focusedId = R.id.k_2_11;
                                }

                                this.initializeButtons();

                                Utils.requestFocusById(root, focusedId);
                            }
                        }
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_LEFT: keyCode: " + keyCode);
                }

                int focusedId = focusedView.getId();

                if (focusedId == R.id.k_0_0 || focusedId == R.id.k_1_0 || focusedId == R.id.k_2_0) {
                    Sidebar.showMenuTexts(menuTexts, root);
                    Sidebar.setFocusToMenu(root, R.id.searchMenuContainer);
                }
                else {
                    int nextFocusId = focusedView.getNextFocusLeftId();
                    if (nextFocusId != -1) {
                        Utils.requestFocusById(root, nextFocusId);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_RIGHT: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    int nextFocusId = focusedView.getNextFocusRightId();
                    if (nextFocusId != -1) {
                        Utils.requestFocusById(root, nextFocusId);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_DOWN: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusDown(root, R.id.searchMenuContainer);
                }
                else {
                    int nextFocusId = focusedView.getNextFocusDownId();
                    if (nextFocusId != -1) {
                        Utils.requestFocusById(root, nextFocusId);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_UP: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    Sidebar.menuFocusUp(root, R.id.searchMenuContainer);
                }
                else {
                    int nextFocusId = focusedView.getNextFocusUpId();
                    if (nextFocusId != -1) {
                        Utils.requestFocusById(root, nextFocusId);
                    }
                }
            }
            else if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_BACK: keyCode: " + keyCode);
                }

                if (Sidebar.isSideMenuOpen(menuTexts)) {
                    this.focusOutFromSideMenu();
                }
                else {
                    if (this.savedSearchContainerVisible) {
                        this.showHideSavedSearch();
                    }
                    else {
                        sharedCacheViewModel.resetSearchString();
                        Utils.toPage(ARCHIVE_MAIN_FRAGMENT, getActivity(), true, false, null);
                    }
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onKeyDown(): Exception: " + e);
            }
            Utils.toErrorPage(getActivity());
        }

        return true;
    }

    /**
     * Handles focus out from side menu.
     */
    private void focusOutFromSideMenu() {
        Sidebar.hideMenuTexts(menuTexts, root);
        Sidebar.setSelectedMenuItem(root, R.id.searchMenuContainer);

        Utils.requestFocusById(root, R.id.k_0_0);
    }

    /**
     * Initialize keys.
     */
    private void initializeButtons() {
        boolean visibility = true;
        if (searchKeyboardType == SearchKeyboardType.SPECIAL) {
            visibility = false;
            this.hideColumns(true);
        }
        else {
            this.hideColumns(false);
        }

        this.setRow3Visibility(visibility);

        for(int i = 0; i < KeyboardChars.SEARCH_CHARACTER_ROW_1.size(); i++) {
            KeyboardChars.SearchCharacter sc = KeyboardChars.SEARCH_CHARACTER_ROW_1.get(i);
            String character = this.getChar(sc);
            if (character != null) {
                TextView tv = root.findViewById(sc.getId());
                if (tv != null) {
                    tv.setText(character);
                }
            }
        }

        for(int i = 0; i < KeyboardChars.SEARCH_CHARACTER_ROW_2.size(); i++) {
            KeyboardChars.SearchCharacter sc = KeyboardChars.SEARCH_CHARACTER_ROW_2.get(i);
            String character = this.getChar(sc);
            if (character != null) {
                TextView tv = root.findViewById(sc.getId());
                if (tv != null) {
                    tv.setText(character);
                }
            }
        }

        for(int i = 0; i < KeyboardChars.SEARCH_CHARACTER_ROW_3.size(); i++) {
            KeyboardChars.SearchCharacter sc = KeyboardChars.SEARCH_CHARACTER_ROW_3.get(i);
            String character = this.getChar(sc);
            if (character != null) {
                TextView tv = root.findViewById(sc.getId());
                if (tv != null) {
                    tv.setText(character);
                }
            }
        }
    }

    /**
     * Hide/show some buttons.
     */
    private void hideColumns(boolean hide) {
        int operation = hide ? View.GONE : View.VISIBLE;

        TextView tv = root.findViewById(R.id.k_0_11);
        if (tv != null) {
            tv.setVisibility(operation);
        }

        tv = root.findViewById(R.id.k_0_12);
        if (tv != null) {
            tv.setVisibility(operation);
        }

        tv = root.findViewById(R.id.k_1_11);
        if (tv != null) {
            tv.setVisibility(operation);
        }
    }

    /**
     * Returns key char (lowercase, uppercase or special char).
     * @param sc
     * @return
     */
    private String getChar(KeyboardChars.SearchCharacter sc) {
        String result = null;
        Character c = null;
        if (searchKeyboardType == SearchKeyboardType.LOWERCASE) {
            c = sc.getLowercase();
        }
        else if (searchKeyboardType == SearchKeyboardType.UPPERCASE) {
            c = sc.getUppercase();
        }
        else if (searchKeyboardType == SearchKeyboardType.SPECIAL) {
            c = sc.getSpecial();
        }

        if (c != null) {
            result = c.toString();
        }

        return result;
    }

    /**
     * Set texts to search and clear buttons.
     */
    private void initializeSearchAndClearButtons() {
        TextView tv = root.findViewById(R.id.searchKey);
        if (tv != null) {
            tv.setText(R.string.do_search);
        }

        tv = root.findViewById(R.id.clearKey);
        if (tv != null) {
            tv.setText(R.string.clear);
        }
    }

    /**
     * Switch row 3 to text mode or special chars mode.
     * @param isTextKeyboard
     */
    private void setRow3Visibility(boolean isTextKeyboard) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout rowContainer = root.findViewById(R.id.row3Container);
        if (rowContainer != null) {
            View row = null;
            if (isTextKeyboard) {
                row = inflater.inflate(R.layout.keyboard_row_3_text, null);
            }
            else {
                row = inflater.inflate(R.layout.keyboard_row_3_special, null);
            }

            rowContainer.removeAllViews();
            rowContainer.addView(row);
        }
    }

    /**
     * Saves search string and navigates to the search result page which makes the search.
     * @param searchText
     * @throws Exception
     */
    private void search(TextView searchText) throws Exception {
        String searchString = searchText.getText().toString();
        if (searchString.length() > 0) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.search(): Search text: " + searchString);
            }

            this.saveSearchValue(searchString);

            sharedCacheViewModel.setSearchString(searchString);
            sharedCacheViewModel.setPageToHistory(SEARCH_FRAGMENT);

            Utils.toPage(SEARCH_RESULT_FRAGMENT, getActivity(), true, false,null);
        }
        else {
            searchText.setBackgroundResource(R.drawable.search_box_invalid);
        }
    }

    /**
     * Shows and hides saved search container.
     */
    private void showHideSavedSearch() throws Exception {
        RelativeLayout contentContainer = root.findViewById(R.id.contentContainer);
        RelativeLayout savedSearchContainer = root.findViewById(R.id.savedSearchContainer);
        if (contentContainer != null && savedSearchContainer != null) {
            if (this.savedSearchContainerVisible) {
                savedSearchContainer.setVisibility(View.GONE);
                savedSearchContainer.getLayoutParams().width = 0;
                Utils.requestFocusById(root, R.id.savedSearchKey);
            }
            else {
                int widthPx = contentContainer.getWidth();
                widthPx = Math.round((float)widthPx / (float)2);

                savedSearchContainer.getLayoutParams().width = widthPx;
                savedSearchContainer.setVisibility(View.VISIBLE);

                final JSONArray jsonArray = getSavedSearches();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String str = jsonArray.getString(i);
                        if (str == null) {
                            continue;
                        }

                        TextView tv = root.findViewById(SAVED_SEARCH_IDS.get(i));
                        if (tv == null) {
                            continue;
                        }

                        tv.setText(str);
                        tv.setVisibility(View.VISIBLE);
                    }

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.requestFocusById(root, R.id.ss_0);
                        }
                    }, 0);
                }
            }

            this.savedSearchContainerVisible = !this.savedSearchContainerVisible;
        }
    }

    /**
     * Saves saved searches to shared preferences as a JSON string.
     * @param searchText
     * @throws Exception
     */
    private void saveSearchValue(String searchText) throws Exception {
        Context context = getContext();
        if (context != null) {
            JSONArray jsonArray = Utils.getSavedPrefs(SAVED_SEARCH_SP_TAG, SAVED_SEARCH_SP_DEFAULT, context);
            if (jsonArray != null) {
                // Remove existing value if found
                for (int i = 0; i < jsonArray.length(); i++) {
                    String str = jsonArray.getString(i);
                    if (str == null) {
                        continue;
                    }

                    if (str.equals(searchText)) {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "SearchFragment.saveSearchValue(): Remove same string from JSON array: " + searchText);
                        }

                        jsonArray.remove(i);
                        break;
                    }
                }

                if (BuildConfig.DEBUG) {
                    Log.d(LOG_TAG, "SearchFragment.saveSearchValue(): Add search string to JSON array: " + searchText);
                }

                JSONArray jsonFinal = new JSONArray();
                jsonFinal.put(searchText);

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonFinal.put(jsonArray.get(i));
                }

                if (jsonFinal.length() > SAVED_SEARCH_MAX_COUNT) {
                    if (BuildConfig.DEBUG) {
                        Log.d(LOG_TAG, "SearchFragment.saveSearchValue(): Too big list (more than 12). Remove the last item!");
                    }

                    jsonFinal.remove(jsonFinal.length() - 1);
                }

                Utils.savePrefs(SAVED_SEARCH_SP_TAG, context, jsonFinal);
            }
        }
    }

    /**
     * Returns saved searched from shared preferences.
     * @return JSONArray
     * @throws Exception
     */
    private JSONArray getSavedSearches() throws Exception {
        return Utils.getSavedPrefs(SAVED_SEARCH_SP_TAG, SAVED_SEARCH_SP_DEFAULT, getContext());
    }
}
