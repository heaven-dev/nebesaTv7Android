package ru.tv7.nebesa.model;

import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.Stack;

import ru.tv7.nebesa.helpers.ArchiveMainPageStateItem;
import ru.tv7.nebesa.helpers.PageStateItem;

/**
 * Shared cache view model. Caches some data.
 */
public class SharedCacheViewModel extends ViewModel {

    private Stack<String> pageHistory = new Stack<>();

    private JSONObject selectedProgram = null;
    private JSONObject selectedCategory = null;

    private PageStateItem seriesPage = null;
    private PageStateItem categoriesPage = null;
    private PageStateItem guidePage = null;
    private PageStateItem searchResultPage = null;
    private PageStateItem favoritesPage = null;

    private ArchiveMainPageStateItem archiveMainPage = null;

    private String searchString = null;
    private String exitFragment = null;

    public String getPageFromHistory() {
        if (pageHistory.size() > 0) {
            return pageHistory.pop();
        }
        return null;
    }

    public void setPageToHistory(String page) {
        if (pageHistory != null) {
            pageHistory.push(page);
        }
    }

    public void clearPageHistory() {
        if (pageHistory != null) {
            pageHistory.clear();
        }
    }

    public JSONObject getSelectedProgram() {
        return selectedProgram;
    }

    public void setSelectedProgram(JSONObject selectedProgram) {
        this.selectedProgram = selectedProgram;
    }

    public void removeSelectedProgram() {
        selectedProgram = null;
    }

    public JSONObject getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(JSONObject selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void removeSelectedCategory() {
        selectedCategory = null;
    }


    public PageStateItem getSeriesPageStateItem() {
        return seriesPage;
    }

    public void setSeriesPageStateItem(PageStateItem pageStateItem) {
        this.seriesPage = pageStateItem;
    }

    public void resetSeriesPageStateItem() {
        seriesPage = null;
    }


    public PageStateItem getCategoriesPageStateItem() {
        return categoriesPage;
    }

    public void setCategoriesPageStateItem(PageStateItem pageStateItem) {
        this.categoriesPage = pageStateItem;
    }

    public void resetCategoriesPageStateItem() {
        categoriesPage = null;
    }


    public PageStateItem getGuidePageStateItem() {
        return guidePage;
    }

    public void setGuidePageStateItem(PageStateItem pageStateItem) {
        this.guidePage = pageStateItem;
    }

    public void resetGuidePageStateItem() {
        guidePage = null;
    }


    public PageStateItem getSearchResultPageStateItem() {
        return searchResultPage;
    }

    public void setSearchResultPageStateItem(PageStateItem pageStateItem) {
        this.searchResultPage = pageStateItem;
    }

    public void resetSearchResultPageStateItem() {
        searchResultPage = null;
    }


    public PageStateItem getFavoritesPageStateItem() {
        return favoritesPage;
    }

    public void setFavoritesPageStateItem(PageStateItem pageStateItem) {
        this.favoritesPage = pageStateItem;
    }

    public void resetFavoritesPageStateItem() {
        favoritesPage = null;
    }


    public ArchiveMainPageStateItem getArchiveMainPageStateItem() {
        return archiveMainPage;
    }

    public void setArchiveMainPageStateItem(ArchiveMainPageStateItem pageStateItem) {
        this.archiveMainPage = pageStateItem;
    }

    public void resetArchiveMainPageStateItem() {
        archiveMainPage = null;
    }


    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void resetSearchString() {
        this.searchString = null;
    }


    public String getExitFragment() {
        return exitFragment;
    }

    public void setExitFragment(String exitFragment) {
        this.exitFragment = exitFragment;
    }


    public void resetAll() {
        selectedProgram = null;
        selectedCategory = null;

        seriesPage = null;
        categoriesPage = null;
        guidePage = null;
        searchResultPage = null;
        favoritesPage = null;

        archiveMainPage = null;

        searchString = null;
    }
}
