package com.badlogic.nonogram.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
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


enum GameStatus {
    playing,gameOver,solved
}
enum GameMode{
    pattern,random
}
public class GameScreen extends ScreenAdapter {
    private final Nonogram game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;

    private Label statusLabel;
    Drawable whiteTileDrawable;
    Drawable blackTileDrawable;
    Drawable markedTileDrawable;

    final Image[][] tiles = new Image[8][8];
    Array<Array<Float>> tileValues;

    double timeRemaining;
    GameStatus gameStatus = GameStatus.playing;
    GameMode gameMode;

    private GameDialog gameOverDialog;
    private GameDialog gameSolvedDialog;

    private final Sound tileClickSound;
    private final Sound buttonClickSound;
    private final Sound solvedSound;
    private Sound gameOverSound;

    public GameScreen(Nonogram game, GameMode gameMode) {
        this.game = game;
        this.gameMode = gameMode;
        assetManager = game.getAssetManager();
        if (gameMode == GameMode.pattern)
            tileValues = GameManager.INSTANCE.getTile();
        else
            tileValues = generateNonogramTiles();
        timeRemaining = GameManager.INSTANCE.getTimeLimit();

        tileClickSound = assetManager.get(AssetDescriptors.TILE_CLICK_SOUND);
        buttonClickSound = assetManager.get(AssetDescriptors.BUTTON_CLICK_SOUND);
        solvedSound = assetManager.get(AssetDescriptors.SOLVED_SOUND);
        gameOverSound = assetManager.get(AssetDescriptors.GAME_OVER_SOUND);
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        atlas = assetManager.get(AssetDescriptors.SCENE2D);

        whiteTileDrawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.WHITE_TILE));
        blackTileDrawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.BLACK_TILE));
        markedTileDrawable = new TextureRegionDrawable(atlas.findRegion(RegionNames.MARKED_TILE));

        initGameOverDialog();
        initSolvedGameDialog();

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
        if (0 <= timeRemaining && gameStatus == GameStatus.playing)
        {
            timeRemaining -= delta;
            statusLabel.setText(String.valueOf((int)timeRemaining));

        }
        else if(timeRemaining < 0 && gameStatus == GameStatus.playing)
        {
            gameOverSound.play();
            gameStatus = GameStatus.gameOver;
            statusLabel.setText("GAME OVER");
            gameOverDialog.getContentTable().clearChildren(); //clear old
            gameOverDialog.text("You ran out of time. Do you want to try again or go to main menu?").padRight(20).padLeft(20);
            gameOverDialog.show(stage);
        }
        if(gameStatus == GameStatus.solved)
            statusLabel.setText("SOLVED");

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

        statusLabel = new Label(String.valueOf((int)timeRemaining),skin.get("white", Label.LabelStyle.class));
        statusLabel.setFontScale(3);
        table.add(statusLabel).row();


        Table tileTable = new Table();
        tileTable.defaults();

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if(i < 3 && j < 3)
                    tiles[i][j] = null;
                else if(j < 3 || i < 3)
                    tiles[i][j] = new Image(new TextureRegionDrawable(atlas.findRegion(String.valueOf(tileValues.get(i).get(j).intValue()))));
                else
                {
                    tiles[i][j] = new Image(whiteTileDrawable);
                    tiles[i][j].setName("0.0");
                    final int finalI = i;
                    final int finalJ = j;
                    tiles[i][j].addListener(new ActorGestureListener() {
                        @Override
                        public void tap(InputEvent event, float x, float y, int count, int button) {
                            long id = tileClickSound.play();
                            tileClickSound.setVolume(id,0.2f);
                            toogleTileState(finalI,finalJ);
                            checkIfSolved();
                        }

                        @Override
                        public boolean longPress(Actor actor, float x, float y) {
                            long id = tileClickSound.play();
                            tileClickSound.setVolume(id,0.2f);
                            tiles[finalI][finalJ].setName("0.0");
                            tiles[finalI][finalJ].setDrawable(markedTileDrawable);
                            checkIfSolved();
                            return true;
                        }
                    });
                }
                tileTable.add(tiles[i][j]).size(55);
            }
            tileTable.row();
        }

        tileTable.center();
        table.add(tileTable);

        table.row();
        TextButton backButton = new TextButton("Give Up", skin);
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

        table.center();
        table.setFillParent(true);
        table.pack();
        return table;
    }

    private void checkIfSolved()
    {
        for (int i = 3; i < tileValues.size; i++)
            for (int j = 3; j < tileValues.get(0).size; j++)
                    if (!tiles[i][j].getName().equals((tileValues.get(i).get(j)).toString()))
                        return;
        solvedSound.play();
        gameStatus = GameStatus.solved;
        gameSolvedDialog.getContentTable().clearChildren();
        gameSolvedDialog.text("You solved the puzzle in " + (GameManager.INSTANCE.getTimeLimit() - (int)timeRemaining) + " seconds.").padRight(20).padLeft(20);
        gameSolvedDialog.show(stage);
        for (int i = 3; i < tileValues.size; i++)
            for (int j = 3; j < tileValues.get(0).size; j++) {
                tiles[i][j].clearListeners();
            }
    }

    private void initGameOverDialog() {
        gameOverDialog = new GameDialog("GAME OVER",skin, "dialog") {
            protected void result(Object object) {
                switch ((GameDialog.Options)object) {
                    case back:
                        buttonClickSound.play();
                        game.setScreen(new MenuScreen(game));
                        break;
                    case retry:
                        buttonClickSound.play();
                        game.setScreen(new GameScreen(game,gameMode));
                        break;
                }
            }
        };
        gameOverDialog.setDebug(false);
        TextButton backButton = new TextButton("back",skin.get("small", TextButton.TextButtonStyle.class));
        TextButton retryButton = new TextButton("retry",skin.get("small", TextButton.TextButtonStyle.class));
        gameOverDialog.addButton(backButton,GameDialog.Options.back).expand().left();
        gameOverDialog.addButton(retryButton,GameDialog.Options.retry).expand().right();
    }

    private void initSolvedGameDialog() {
            final TextField textField = new TextField("",skin);
            final String nickname = GameManager.INSTANCE.getNickname();

            gameSolvedDialog = new GameDialog("CONGRATULATIONS!",skin, "dialog") {
                protected void result(Object object) {
                    switch ((GameDialog.Options)object) {
                        case back:
                            buttonClickSound.play();
                            game.setScreen(new MenuScreen(game));
                            break;
                        case enter:
                            buttonClickSound.play();
                            if(!nickname.equals(""))
                                GameManager.INSTANCE.setLeaderboard(GameManager.INSTANCE.getNickname(),String.valueOf(GameManager.INSTANCE.getTimeLimit() - (int)timeRemaining));
                            else if(!textField.getText().equals(""))
                                GameManager.INSTANCE.setLeaderboard(textField.getText(),String.valueOf(GameManager.INSTANCE.getTimeLimit() - (int)timeRemaining));
                            else
                                GameManager.INSTANCE.setLeaderboard("Anonymous",String.valueOf(GameManager.INSTANCE.getTimeLimit() - (int)timeRemaining));
                            game.setScreen(new MenuScreen(game));
                            break;
                    }
                }
            };
            gameSolvedDialog.setDebug(false);
            TextButton cancelButton = new TextButton("cancel",skin.get("small", TextButton.TextButtonStyle.class));
            TextButton enterButton = new TextButton("enter",skin.get("small", TextButton.TextButtonStyle.class));
            if(nickname.equals(""))
            {
                gameSolvedDialog.addTextField(textField).fill();
                gameSolvedDialog.getButtonTable().row();
            }
            else
            {
                cancelButton.setText("Dont save");
                enterButton.setText("Save score");
            }
            gameSolvedDialog.addButton(cancelButton,GameDialog.Options.back).expand().left();
            gameSolvedDialog.addButton(enterButton,GameDialog.Options.enter).expand().right();
    }

    private Array<Array<Float>> generateNonogramTiles() {
        Array<Array<Float>> nonogram = new Array<>();
        Random rand = new Random();

        nonogram.setSize(8);
        for(int i = 0; i < nonogram.size;i++)
        {
            nonogram.set(i,new Array<Float>());
            nonogram.get(i).setSize(8);
        }

        for(int i = 0; i < nonogram.size;i++)
            for(int j = 0; j < nonogram.get(0).size;j++)
                nonogram.get(i).set(j,0.0f);

        int leftIndex = 0;
        int[] topIndex = new int[5];
        Arrays.fill(topIndex, 0);

        for(int r = 3; r < nonogram.size;r++)
        {
            for(int c = 3; c < nonogram.get(0).size;c++)
            {
                nonogram.get(r).set(c, (float) rand.nextInt(2));
                if(nonogram.get(r).get(c) == 1)
                {
                    nonogram.get(r).set(leftIndex,nonogram.get(r).get(leftIndex) + 1);
                    nonogram.get(topIndex[c - 3]).set(c,nonogram.get(topIndex[c - 3]).get(c) + 1);
                }
                if(nonogram.get(r).get(c) == 0 && nonogram.get(r).get(leftIndex) != 0)
                    leftIndex++;
                if(nonogram.get(r).get(c) == 0 && nonogram.get(topIndex[c - 3]).get(c) != 0)
                    topIndex[c - 3]++;
            }
            leftIndex = 0;
        }
        return nonogram;
    }

    void toogleTileState(int i, int j)
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
}
