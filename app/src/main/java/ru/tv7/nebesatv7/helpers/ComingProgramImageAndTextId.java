package ru.tv7.nebesatv7.helpers;

/**
 * Helper class.
 */
public class ComingProgramImageAndTextId {
    private int imageId = 0;
    private int textId = 0;

    public ComingProgramImageAndTextId(int imageId, int textId) {
        this.imageId = imageId;
        this.textId = textId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getTextId() {
        return textId;
    }
}
