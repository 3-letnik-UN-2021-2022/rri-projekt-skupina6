package com.badlogic.nonogram.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<BitmapFont> UI_FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    public static final AssetDescriptor<BitmapFont> UI_FONT_BIG =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT_BIG, BitmapFont.class);

    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<TextureAtlas> SCENE2D =
            new AssetDescriptor<TextureAtlas>(AssetPaths.SCENE2D, TextureAtlas.class);

    public static final AssetDescriptor<Sound> BUTTON_CLICK_SOUND =
            new AssetDescriptor<Sound>(AssetPaths.BUTTON_CLICK_SOUND, Sound.class);

    public static final AssetDescriptor<Sound> TILE_CLICK_SOUND =
            new AssetDescriptor<Sound>(AssetPaths.TILE_CLICK_SOUND, Sound.class);

    public static final AssetDescriptor<Sound> SOLVED_SOUND =
            new AssetDescriptor<Sound>(AssetPaths.SOLVED_SOUND, Sound.class);

    public static final AssetDescriptor<Sound> GAME_OVER_SOUND =
            new AssetDescriptor<Sound>(AssetPaths.GAME_OVER_SOUND, Sound.class);

    private AssetDescriptors() {
    }
}
