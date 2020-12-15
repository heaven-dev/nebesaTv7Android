package ru.tv7.nebesatv7.helpers;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import ru.tv7.nebesatv7.R;
import ru.tv7.nebesatv7.model.SharedCacheViewModel;

import static ru.tv7.nebesatv7.helpers.Constants.MENU_ITEM_IDS;

public class Sidebar {
    public static boolean isSideMenuOpen(List<TextView> menuTexts) {
        return menuTexts.get(0).getVisibility() == View.VISIBLE;
    }

    public static List<TextView> getMenuTextItems(View root) {
        List<TextView> menuTexts = new ArrayList<>();

        for(int i = 0; i < MENU_ITEM_IDS.size(); i++) {
            MenuItem menuItem = MENU_ITEM_IDS.get(i);
            TextView tv = root.findViewById(menuItem.getMenuTextId());
            menuTexts.add(tv);
        }

        return menuTexts;
    }

    public static void showMenuTexts(List<TextView> menuTexts) {
        for (TextView tv: menuTexts) {
            tv.setVisibility(View.VISIBLE);
        }
    }

    public static void hideMenuTexts(List<TextView> menuTexts) {
        for (TextView tv: menuTexts) {
            tv.setVisibility(View.GONE);
        }
    }

    public static int getFocusedMenuItem(View root) {
        int id = 0;
        View view = root.findFocus();
        if (view != null) {
            id = view.getId();
        }

        return id;
    }

    public static int setFocusToMenu(View root, int menuId) {
        LinearLayout menuItemContainer = root.findViewById(menuId);
        if (menuItemContainer != null) {
            menuItemContainer.requestFocus();

            removeSelectionFromMenu(root, menuId);
        }
        return menuId;
    }

    public static void removeSelectionFromMenu(View root, int menuId) {
        LinearLayout menuItemContainer = root.findViewById(menuId);
        if (menuItemContainer != null) {
            menuItemContainer.setBackgroundResource(0);
            menuItemContainer.setBackgroundResource(R.drawable.menu_item_container);
        }
    }

    public static void setSelectionToMenu(View root, int menuId) {
        LinearLayout menuItemContainer = root.findViewById(menuId);
        if (menuItemContainer != null) {
            menuItemContainer.setBackgroundResource(R.drawable.menu_item_container_selected);
        }
    }

    public static void menuFocusDown(View root, int selectedMenuId) {
        int id = getFocusedMenuItem(root);

        int focusedMenuId = 0;
        if (id == R.id.tvMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.archiveMenuContainer);
        }
        else if (id == R.id.archiveMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.guideMenuContainer);
        }
        else if (id == R.id.guideMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.searchMenuContainer);
        }
        else if (id == R.id.searchMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.favoritesMenuContainer);
        }
        else if (id == R.id.favoritesMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.aboutMenuContainer);
        }

        if (focusedMenuId == 0) {
            return;
        }

        if (focusedMenuId == selectedMenuId) {
            removeSelectionFromMenu(root, selectedMenuId);
        }
        else {
            setSelectionToMenu(root, selectedMenuId);
        }
    }

    public static void menuFocusUp(View root, int selectedMenuId) {
        int id = getFocusedMenuItem(root);

        int focusedMenuId = 0;
        if (id == R.id.aboutMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.favoritesMenuContainer);
        }
        else if (id == R.id.favoritesMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.searchMenuContainer);
        }
        else if (id == R.id.searchMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.guideMenuContainer);
        }
        else if (id == R.id.guideMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.archiveMenuContainer);
        }
        else if (id == R.id.archiveMenuContainer) {
            focusedMenuId = setFocusToMenu(root, R.id.tvMenuContainer);
        }

        if (focusedMenuId == 0) {
            return;
        }

        if (focusedMenuId == selectedMenuId) {
            removeSelectionFromMenu(root, selectedMenuId);
        }
        else {
            setSelectionToMenu(root, selectedMenuId);
        }
    }

    public static void setSelectedMenuItem(View root, int menuId) {
        for (MenuItem menuItem: MENU_ITEM_IDS) {
            int menuContainerId = menuItem.getMenuContainerId();
            if (menuContainerId == menuId) {
                setSelectionToMenu(root, menuId);
            }
            else {
                removeSelectionFromMenu(root, menuContainerId);
            }
        }
    }

    public static void menuItemSelected(int menuId, FragmentActivity activity, SharedCacheViewModel sharedCacheViewModel) {
        if (menuId > 0 && activity != null) {

            if (sharedCacheViewModel != null) {
                sharedCacheViewModel.resetAll();
            }

            String fragmentTag = null;
            if (menuId == R.id.tvMenuContainer) {
                fragmentTag = Constants.TV_MAIN_FRAGMENT;
            }
            else if (menuId == R.id.archiveMenuContainer) {
                fragmentTag = Constants.ARCHIVE_MAIN_FRAGMENT;
            }
            else if (menuId == R.id.guideMenuContainer) {
                fragmentTag = Constants.GUIDE_FRAGMENT;
            }
            else if (menuId == R.id.searchMenuContainer) {
                fragmentTag = Constants.SEARCH_FRAGMENT;
            }
            else if (menuId == R.id.favoritesMenuContainer) {
                fragmentTag = Constants.FAVORITES_FRAGMENT;
            }
            else if (menuId == R.id.aboutMenuContainer) {
                fragmentTag = Constants.ABOUT_FRAGMENT;
            }

            Utils.toPage(fragmentTag, activity, true, false,null);
        }
    }
}
