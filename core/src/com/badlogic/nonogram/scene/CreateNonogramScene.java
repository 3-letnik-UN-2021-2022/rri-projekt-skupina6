package com.badlogic.nonogram.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.nonogram.GameManager;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.assets.RegionNames;
import com.badlogic.nonogram.config.GameConfig;
import com.badlogic.nonogram.dialog.GameDialog;

import java.util.Arrays;
import java.util.Random;

public class CreateNonogramScene extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private final Sound buttonClickSound;
    private final Sound tileClickSound;



    final ImageButton[][] tiles = new ImageButton[5][5];
    Array<Array<Float>> tileValues = new Array<>();

    public CreateNonogramScene(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
        buttonClickSound = assetManager.get(AssetDescriptors.BUTTON_CLICK_SOUND);
        tileClickSound = assetManager.get(AssetDescriptors.TILE_CLICK_SOUND);
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        atlas = assetManager.get(AssetDescriptors.SCENE2D);

        Gdx.input.setInputProcessor(stage);
        stage.addActor(createGame());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(65 / 255f, 159 / 255f, 221 / 255f, 0f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private Actor createGame() {
        Table table = new Table();
        table.defaults().pad(-0.5f);

        Label createNonogramLabel = new Label("Create Your Own Nonogram",skin.get("white", Label.LabelStyle.class));
        createNonogramLabel.setFontScale(3);
        table.add(createNonogramLabel).colspan(2).row();


        Table tileTable = new Table();
        tileTable.defaults();
        Drawable drawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.WHITE_TILE));
        Drawable drawable2 = new TextureRegionDrawable(atlas.findRegion(RegionNames.BLACK_TILE));

        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                tiles[i][j] = new ImageButton(drawable,drawable,drawable2);
                tileTable.add(tiles[i][j]).size(55);
                tiles[i][j].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        tileClickSound.play();
                    }
                });
            }
            tileTable.row();
        }

        for (int i = 0; i < tileValues.size; i++)
            for (int j = 0; j < tileValues.get(0).size; j++) {
                final int finalI = i, finalJ = j;
                tiles[i][j].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        tiles[finalI][finalJ].toggle();
                    }
                });
            }

        tileTable.center();
        table.add(tileTable).colspan(2).row();

        TextButton saveButton = new TextButton("Save", skin);
        saveButton.setOrigin(Align.center);
        saveButton.setTransform(true);
        saveButton.setScale(0.5f);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                for(int i = 0; i < 5;i++)
                {
                    tileValues.add(new Array<Float>());
                    for(int j = 0; j < 5;j++)
                        tileValues.get(i).add(tiles[i][j].isChecked() ? 1f : 0f);
                }
                GameManager.INSTANCE.saveNonogram(tileValues);
                game.setScreen(new MenuScreen(game));
            }
        });

        TextButton backButton = new TextButton("Cancel", skin);
        backButton.setOrigin(Align.center);
        backButton.setTransform(true);
        backButton.setScale(0.5f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(backButton);
        table.add(saveButton);

        table.center();
        table.setFillParent(true);
        table.pack();
        return table;
    }
}