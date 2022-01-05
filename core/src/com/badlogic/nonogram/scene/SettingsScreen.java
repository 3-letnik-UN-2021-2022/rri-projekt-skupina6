package com.badlogic.nonogram.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.nonogram.GameManager;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.config.GameConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class SettingsScreen extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas scene2dAtlas;

    private ButtonGroup<CheckBox> checkBoxGroup;
    private CheckBox timelimit1;
    private CheckBox timelimit2;
    private CheckBox timelimit3;
    private CheckBox timelimit4;

    private TextField nicknameField;
    private TextButton nicknameButton;
    private TextButton clearNicknameButton;
    private String nickname;


    public SettingsScreen(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
        nickname = GameManager.INSTANCE.getNickname();
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

        TypingLabel settingsLabel = new TypingLabel("{EASE}Settings",skin.get("white", Label.LabelStyle.class));
        settingsLabel.setFontScale(4);


        //TextField timeLimitTextField = new TextField("", skin);

        timelimit1 = new CheckBox("30", skin);
        timelimit1.setTransform(true);
        timelimit1.setScale(1.5f);
        timelimit2 = new CheckBox("60", skin);
        timelimit2.setTransform(true);
        timelimit2.setScale(1.5f);
        timelimit3 = new CheckBox("120", skin);
        timelimit3.setTransform(true);
        timelimit3.setScale(1.5f);
        timelimit4 = new CheckBox("240", skin);
        timelimit4.setTransform(true);
        timelimit4.setScale(1.5f);

        timelimit1.addListener(listener);
        timelimit2.addListener(listener);
        timelimit3.addListener(listener);
        timelimit4.addListener(listener);

        checkBoxGroup = new ButtonGroup<>(timelimit1,timelimit2,timelimit3,timelimit4);
        checkBoxGroup.setChecked(String.valueOf(GameManager.INSTANCE.getTimeLimit()));


        TextButton backButton = new TextButton("Back", skin);
        backButton.setOrigin(Align.center);
        backButton.setTransform(true);
        backButton.setScale(0.5f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });


        nicknameField = new TextField(nickname,skin);

        nicknameButton = new TextButton("Set NickName",skin.get("small", TextButton.TextButtonStyle.class));
        nicknameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.INSTANCE.setNickname(nicknameField.getText());
                nickname = nicknameField.getText();
                nicknameField.setText(nickname);
                System.out.println("Nickname: " + nickname);
            }
        });

        clearNicknameButton = new TextButton("Clear NickName",skin.get("small", TextButton.TextButtonStyle.class));
        clearNicknameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameManager.INSTANCE.setNickname("");
                nicknameField.setText("");
                nickname = "";
                System.out.println("Nickname: " + nickname);
            }
        });


        table.add(settingsLabel).align(Align.center).colspan(4);
        table.row().align(Align.left).padLeft(70);
        table.add(new Label("Choose Time Limit: ",skin.get("white", Label.LabelStyle.class))).colspan(4);
        table.row();
        table.add(timelimit1);
        table.add(timelimit2);
        table.add(timelimit3);
        table.add(timelimit4);
        table.row();
        table.add(nicknameField).colspan(2).fillX();
        table.add(nicknameButton).fillX();
        table.add(clearNicknameButton).fillX();
        table.row().expand().align(Align.bottom).colspan(4);
        table.add(backButton);


        table.top();
        table.setFillParent(true);
        table.pack();
        return table;
    }

    ChangeListener listener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            CheckBox checked = checkBoxGroup.getChecked();
            if (checked == timelimit1)
                GameManager.INSTANCE.setTimeLimit(30);
            else if (checked == timelimit2)
                GameManager.INSTANCE.setTimeLimit(60);
            else if (checked == timelimit3)
                GameManager.INSTANCE.setTimeLimit(120);
            else if (checked == timelimit4)
                GameManager.INSTANCE.setTimeLimit(240);
        }
    };
}
