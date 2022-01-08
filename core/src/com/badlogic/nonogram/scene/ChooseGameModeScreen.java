package com.badlogic.nonogram.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.config.GameConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class ChooseGameModeScreen extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas scene2dAtlas;

    public ChooseGameModeScreen(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {

        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        scene2dAtlas = assetManager.get(AssetDescriptors.SCENE2D);

        Gdx.input.setInputProcessor(stage);
        stage.addActor(createUi());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(181f / 252, 181f / 252, 181f / 252, 0f);

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

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        TypingLabel gameLabel = new TypingLabel("{EASE}Select Gamemode",skin.get("white", Label.LabelStyle.class));
        gameLabel.setFontScale(4);

        TextButton patternGameButton = new TextButton("Pattern", skin);
        patternGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, GameMode.pattern));
            }
        });

        TextButton randomGameButton = new TextButton("Random", skin);
        randomGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game,GameMode.random));
            }
        });

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });


        Table buttonTable = new Table();
        buttonTable.add(gameLabel).padBottom(50).row();
        buttonTable.add(patternGameButton).padBottom(15).fillX().row();
        buttonTable.add(randomGameButton).padBottom(15).fillX().row();
        buttonTable.add(backButton).padBottom(15).fillX().row();
        buttonTable.center();
        table.add(buttonTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
