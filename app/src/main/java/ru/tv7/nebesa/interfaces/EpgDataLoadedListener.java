package ru.tv7.nebesa.interfaces;

/**
 * Epg data load interface.
 */
public interface EpgDataLoadedListener {
    void onEpgDataLoaded();
    void onEpgDataLoadError(String message);
    void onNetworkError();
}
