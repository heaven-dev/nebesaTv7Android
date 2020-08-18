package ru.tv7.nebesatv7.interfaces;

/**
 * Epg data load interface.
 */
public interface EpgDataLoadedListener {
    void onEpgDataLoaded();
    void onEpgDataLoadError(String message);
}


