package com.badlogic.nonogram;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.assets.RegionNames;
import com.badlogic.nonogram.scene.IntroScreen;
import com.badlogic.nonogram.scene.MenuScreen;

public class Nonogram extends Game {
	private AssetManager assetManager;
	private SpriteBatch batch;
	private GalleryOpener galleryOpener;

	public Nonogram(GalleryOpener opener) {
		this.galleryOpener = opener;
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		assetManager = new AssetManager();
		assetManager.getLogger().setLevel(Logger.DEBUG);

		batch = new SpriteBatch();
		setScreen(new IntroScreen(this));
		//setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public GalleryOpener getGalleryOpener() { return galleryOpener; }

	public SpriteBatch getBatch() {
		return batch;
	}
}
