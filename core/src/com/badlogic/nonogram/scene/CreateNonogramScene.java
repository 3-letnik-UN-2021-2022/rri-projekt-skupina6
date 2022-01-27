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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.badlogic.nonogram.GalleryOpener;
import com.badlogic.nonogram.GameManager;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.assets.RegionNames;
import com.badlogic.nonogram.config.GameConfig;
import com.badlogic.nonogram.dialog.GameDialog;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CreateNonogramScene extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private GalleryOpener galleryOpener;

    Drawable whiteTileDrawable;
    Drawable blackTileDrawable;

    private final Sound buttonClickSound;
    private final Sound tileClickSound;



    final Image[][] tiles = new Image[5][5];
    Array<Array<Float>> tileValues = new Array<>();

    public CreateNonogramScene(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
        buttonClickSound = assetManager.get(AssetDescriptors.BUTTON_CLICK_SOUND);
        tileClickSound = assetManager.get(AssetDescriptors.TILE_CLICK_SOUND);
        galleryOpener = game.getGalleryOpener();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        atlas = assetManager.get(AssetDescriptors.SCENE2D);

        whiteTileDrawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.WHITE_TILE));
        blackTileDrawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.BLACK_TILE));

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

        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                final int finalI = i, finalJ = j;
                tiles[i][j] = new Image(whiteTileDrawable);
                tiles[i][j].setName("0.0");
                tileTable.add(tiles[i][j]).size(55);
                tiles[i][j].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        long id = tileClickSound.play();
                        tileClickSound.setVolume(id,0.2f);
                        switchTileState(finalI,finalJ);
                    }
                });
            }
            tileTable.row();
        }

        tileTable.center();
        table.add(tileTable).colspan(2).row();

        final Label fileLabel = new Label("No image selected!",skin.get("white", Label.LabelStyle.class));
        fileLabel.setFontScale(2);
        table.add(fileLabel).colspan(2).row();

        TextButton saveButton = new TextButton("Save", skin);
        saveButton.setOrigin(Align.center);
        saveButton.setTransform(true);
        saveButton.setScale(0.6f);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                for(int i = 0; i < 5;i++)
                {
                    tileValues.add(new Array<Float>());
                    for(int j = 0; j < 5;j++)
                        tileValues.get(i).add(tiles[i][j].getName().equals("0.0") ? 0f : 1f);
                }
                GameManager.INSTANCE.saveNonogram(tileValues);
                game.setScreen(new MenuScreen(game));
            }
        });

        TextButton selectImageButton = new TextButton("Load Image", skin);
        selectImageButton.setOrigin(Align.center);
        selectImageButton.setTransform(true);
        selectImageButton.setScale(0.6f);
        selectImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                try {
                    galleryOpener.getGalleryImagePath();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                fileLabel.setText("Image selected");
            }
        });

        TextButton evaluateButton = new TextButton("Recognize", skin);
        evaluateButton.setOrigin(Align.center);
        evaluateButton.setTransform(true);
        evaluateButton.setScale(0.6f);
        evaluateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                if (galleryOpener.getSelectedFilePath() != null) {
                    String selectedImagePath = galleryOpener.getSelectedFilePath();
                    String result = GameManager.INSTANCE.evaluateImage(selectedImagePath);
                    if (result != "0000000000000000000000000") {
                        loadTilesFromImage(result);
                    }
                }
            }
        });

        TextButton backButton = new TextButton("Cancel", skin);
        backButton.setOrigin(Align.center);
        backButton.setTransform(true);
        backButton.setScale(0.6f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });

        table.add(backButton);
        table.add(saveButton).row();
        table.add(selectImageButton);
        table.add(evaluateButton);

        table.center();
        table.setFillParent(true);
        table.pack();
//        table.debug();
        return table;
    }

    void switchTileState(int i, int j)
    {
        if (tiles[i][j].getName().equals("1.0"))
        {
            tiles[i][j].setName("0.0");
            tiles[i][j].setDrawable(whiteTileDrawable);
        }
        else
        {
            tiles[i][j].setName("1.0");
            tiles[i][j].setDrawable(blackTileDrawable);
        }
    }

    void loadTilesFromImage(String result) {
        for (int i = 0; i < 5; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                final char c = result.charAt(i * 5 + j);
                if (c == '1' && tiles[i][j].getName().equals("0.0")) {
                    switchTileState(i, j);
                }
                else {
                    if (c == '0' && tiles[i][j].getName().equals("1.0")) {
                        switchTileState(i, j);
                    }
                }
            }
        }
    }
}