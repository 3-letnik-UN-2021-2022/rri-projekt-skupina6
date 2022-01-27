package com.badlogic.nonogram.scene;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.nonogram.Nonogram;
import com.badlogic.nonogram.assets.AssetDescriptors;
import com.badlogic.nonogram.assets.RegionNames;
import com.badlogic.nonogram.config.GameConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class IntroScreen extends ScreenAdapter {
    public static final float INTRO_DURATION_IN_SEC = 5f;

    private Viewport viewport;

    private final Nonogram game;
    private final AssetManager assetManager;
    private TextureAtlas atlas;
    private Skin skin;

    private float duration;

    private Stage stage;

    public IntroScreen(Nonogram game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        duration = 0f;

        assetManager.load(AssetDescriptors.UI_FONT);
        assetManager.load(AssetDescriptors.UI_FONT_BIG);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.SCENE2D);
        assetManager.load(AssetDescriptors.TILE_CLICK_SOUND);
        assetManager.load(AssetDescriptors.BUTTON_CLICK_SOUND);
        assetManager.load(AssetDescriptors.GAME_OVER_SOUND);
        assetManager.load(AssetDescriptors.SOLVED_SOUND);
        assetManager.finishLoading();

        atlas = assetManager.get(AssetDescriptors.SCENE2D);
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage.addActor(createTable());
        stage.addActor(createAnimation());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(65 / 255f, 159 / 255f, 221 / 255f, 0f);

        duration += delta;

        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen(game));
        }

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


    private Actor createTable() {
        Table table = new Table();


        TypingLabel nonogramLabel = new TypingLabel(" {SPEED=0.38}{WAIT=0.5}Nonogram {WAIT=1}",skin.get("white", Label.LabelStyle.class));
        nonogramLabel.setFontScale(4);

        table.row().colspan(3);
        table.add(nonogramLabel);
        table.row().padTop(40).colspan(3);
        TypingLabel madeByLabel = new TypingLabel(" {EASE}{SLOW}{WAIT=2}Made By Nejc Podvratnik, Jus Osojnik and Grega Rubin",skin.get("white", Label.LabelStyle.class));
        madeByLabel.setFontScale(1.5f);
        table.add(madeByLabel);
        table.row().expand();


        table.center();
        table.setFillParent(true);
        table.pack();
        return table;
    }

    private Actor createAnimation() {
        Image key = new Image(atlas.findRegion(RegionNames.BLACK_TILE));

        key.setOrigin(Align.center);
        key.setPosition(-100,290);
        key.setScale(1.2f);
        key.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.rotateBy(-720, 2f),
                                Actions.moveTo(viewport.getWorldWidth() + 50, 290, 2f)
                        ),
                        Actions.removeActor()
                )
        );

        return key;
    }

}
