package com.badlogic.nonogram.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
    private final Sound buttonClickSound;

    private LeaderBoard leaderBoard;



    public LeaderBoardScreen(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
        leaderBoard = GameManager.INSTANCE.getLeaderBoard();
        buttonClickSound = assetManager.get(AssetDescriptors.BUTTON_CLICK_SOUND);
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

        TypingLabel topTitle = new TypingLabel("{EASE}LeaderBoard",skin.get("white", Label.LabelStyle.class));
        Label nameTitle = new Label("Name", skin.get("white", Label.LabelStyle.class));
        Label timeTitle = new Label("Time", skin.get("white", Label.LabelStyle.class));

        topTitle.setAlignment(Align.center);
        topTitle.setFontScale(4);
        nameTitle.setFontScale(1.5f);
        timeTitle.setFontScale(1.5f);


        table.row().colspan(2);
        table.add(topTitle);
        table.row().padBottom(10);
        table.add(nameTitle).align(Align.left);
        table.add(timeTitle).align(Align.right);
        table.row();
        for (int i = 0; i < leaderBoard.names.size();i++) {
            Label nameLabel = new Label(leaderBoard.names.get(i), skin.get("white", Label.LabelStyle.class));
            Label timeLabel = new Label(leaderBoard.times.get(i), skin.get("white", Label.LabelStyle.class));

            nameLabel.setFontScale(1.5f);
            timeLabel.setFontScale(1.5f);

            table.add(nameLabel).align(Align.left);
            table.add(timeLabel).align(Align.right);
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
                buttonClickSound.play();
                game.setScreen(new MenuScreen(game));
            }
        });


        table.top();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
