package com.badlogic.nonogram;

public interface GalleryOpener {
    public void getGalleryImagePath() throws InterruptedException;

    String getSelectedFilePath();
}
