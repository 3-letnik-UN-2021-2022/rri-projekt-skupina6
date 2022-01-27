package com.badlogic.nonogram;

import android.app.Activity;
import android.content.Intent;

public class AndroidGalleryOpener implements GalleryOpener  {

    Activity activity;
    public static final int SELECT_IMAGE_CODE = 1;

    private String currentImagePath;

    public AndroidGalleryOpener(Activity activity){
        this.activity = activity;
    }

    @Override
    public void getGalleryImagePath() throws InterruptedException {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_CODE);
    }

    public void setImageResult(String path){
        currentImagePath = path;
    }

    public String getSelectedFilePath(){
        return currentImagePath;
    }

}