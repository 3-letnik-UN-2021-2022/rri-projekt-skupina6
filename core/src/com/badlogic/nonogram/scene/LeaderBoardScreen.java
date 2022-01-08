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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.nonogram.GameManager;
import com.badlogic.nonogram.LeaderBoard;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.config.GameConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.List;

public class LeaderBoardScreen extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas scene2dAtlas;

    private LeaderBoard leaderBoard;

    public LeaderBoardScreen(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
        leaderBoard = GameManager.INSTANCE.getLeaderBoard();
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
        ScreenUtils.clear(181f/252, 181f/252, 181f/252, 0f);
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
        table.defaults().pad(2);
        table.pad(20);
        TypingLabel topLabel = new TypingLabel("{EASE}LeaderBoard",skin.get("white", Label.LabelStyle.class));
        topLabel.setAlignment(Align.center);
        topLabel.setFontScale(4);
        table.row().colspan(2);
        table.add(topLabel);
        table.row().padBottom(10);
        table.add(new Label("Name", skin.get("white", Label.LabelStyle.class))).align(Align.left);
        table.add(new Label("Time", skin.get("white", Label.LabelStyle.class))).align(Align.right);
        table.row();
        for (int i = 0; i < leaderBoard.names.size();i++) {
            table.add(new Label(leaderBoard.names.get(i), skin.get("white", Label.LabelStyle.class))).align(Align.left);
            table.add(new Label(leaderBoard.times.get(i), skin.get("white", Label.LabelStyle.class))).align(Align.right);
            table.row();
        }
        table.row().colspan(2);
        TextButton backButton = new TextButton("Back",skin);
        backButton.setOrigin(Align.center);
        backButton.setTransform(true);
        backButton.setScale(0.5f);
        table.add(backButton).align(Align.bottom).expandY();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });


        table.top();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
