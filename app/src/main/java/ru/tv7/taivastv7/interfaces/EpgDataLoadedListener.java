package ru.tv7.taivastv7.interfaces;

/**
 * Epg data load interface.
 */
public interface EpgDataLoadedListener {
    void onEpgDataLoaded();
    void onEpgDataLoadError(String message);
}


