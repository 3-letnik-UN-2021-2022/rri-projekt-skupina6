package com.badlogic.nonogram;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class AndroidLauncher extends AndroidApplication {
	String userImagePath = null;
	AndroidGalleryOpener opener;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		opener = new AndroidGalleryOpener(this);
		initialize(new Nonogram(opener), config);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == AndroidGalleryOpener.SELECT_IMAGE_CODE) {
			InputStream is = null;
			try {
				is = getContentResolver().openInputStream(data.getData());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			String filePath = storeImage(bitmap);
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			opener.setImageResult(filePath);
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	private String storeImage(Bitmap image) {
		File pictureFile = getOutputMediaFile();
		if (pictureFile == null) {
			return "";
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (IOException ignored) {
		}
		return pictureFile.getAbsolutePath();
	}

	private  File getOutputMediaFile() {
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/"
				+ getApplicationContext().getPackageName()
				+ "/Files");

		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				return null;
			}
		}

		@SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		File mediaFile;
		String mImageName="MI_"+ timeStamp +".jpg";
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
		return mediaFile;
	}
}