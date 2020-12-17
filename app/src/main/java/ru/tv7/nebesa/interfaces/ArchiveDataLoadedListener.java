package ru.tv7.nebesa.interfaces;

import org.json.JSONArray;

/**
 * Archive data load interface.
 */
public interface ArchiveDataLoadedListener {
    void onArchiveDataLoaded(JSONArray jsonArray, String type);
    void onArchiveDataLoadError(String message, String type);
}
