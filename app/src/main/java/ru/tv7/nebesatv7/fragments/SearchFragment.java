package ru.tv7.nebesatv7.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import ru.tv7.nebesatv7.BuildConfig;
import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.enums.SearchKeyboardType;
import ru.tv7.nebesatv7.helpers.KeyboardChars;
import ru.tv7.nebesatv7.helpers.Sidebar;
import ru.tv7.nebesatv7.helpers.Utils;
import ru.tv7.nebesatv7.model.SharedCacheViewModel;

import static ru.tv7.nebesatv7.helpers.Constants.ARCHIVE_MAIN_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.BACKSPACE_BUTTON;
import static ru.tv7.nebesatv7.helpers.Constants.EMPTY;
import static ru.tv7.nebesatv7.helpers.Constants.LOG_TAG;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_RESULT_FRAGMENT;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_SPECIAL_CHAR_MODE_BUTTONS;
import static ru.tv7.nebesatv7.helpers.Constants.SEARCH_TEXT_MODE_BUTTONS;
import static ru.tv7.nebesatv7.helpers.Constants.SPACE;
import static ru.tv7.nebesatv7.helpers.Constants.SPACE_BUTTON;
import static ru.tv7.nebesatv7.helpers.Constants.SPECIAL_CHARS_BUTTON;
import static ru.tv7.nebesatv7.helpers.Constants.UPPERCASE_BUTTON;

/**
 * Search fragment. Show keys to write search string.
 */
public class SearchFragment extends Fragment {

    private View root = null;
    private SharedCacheViewModel sharedCacheViewModel = null;

    private List<TextView> menuTexts = null;
    private SearchKeyboardType searchKeyboardType = SearchKeyboardType.LOWERCASE;

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
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "SearchFragment.onCreate() called.");
        }


        sharedCacheViewModel = ViewModelProviders.of(requireActivity()).get(SharedCacheViewModel.class);
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

            LinearLayout contentContainer = root.findViewById(R.id.contentContainer);
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
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onCreateView(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
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
                else {
                    int id = focusedView.getId();
                    TextView searchText = root.findViewById(R.id.searchText);

                    if (id == R.id.searchKey) {
                        String searchString = searchText.getText().toString();
                        if (searchText != null && searchString.length() > 0) {
                            if (BuildConfig.DEBUG) {
                                Log.d(LOG_TAG, "SearchFragment.onKeyDown(): KEYCODE_DPAD_CENTER: Search text: " + searchString);
                            }

                            sharedCacheViewModel.setSearchString(searchString);
                            sharedCacheViewModel.setPageToHistory(SEARCH_FRAGMENT);

                            Utils.toPage(SEARCH_RESULT_FRAGMENT, getActivity(), true, false,null);
                        }
                    }
                    else if (id == R.id.clearKey) {
                        if (searchText != null) {
                            searchText.setText(EMPTY);
                        }
                    }
                    else {
                        if (focusedView instanceof TextView) {
                            TextView view = (TextView)focusedView;

                            String text = searchText.getText().toString();
                            if (text != null && view != null) {
                                String newChar = view.getText().toString();
                                if (newChar != null) {
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
                    sharedCacheViewModel.resetSearchString();
                    Utils.toPage(ARCHIVE_MAIN_FRAGMENT, getActivity(), true, false,null);
                }
            }
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "SearchFragment.onKeyDown(): Exception: " + e);
            }
            Utils.showErrorToast(getContext(), getString(R.string.toast_something_went_wrong));
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
}
