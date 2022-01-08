package com.badlogic.nonogram;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class NonogramTiles
{
    Array<Array<Array<Float>>> shapes;
}

public class GameManager {

    public static final GameManager INSTANCE = new GameManager();

    private static final String TIME_LIMIT = "timeLimit";
    private static final String NICKNAME = "nickname";
    private static final String LEADERBOARD = "data/leaderboard.json";
    private static final String TILES = "data/tiles.json";

    private final Preferences PREFS;
    private int timeLimit;
    private String nickname;
    private LeaderBoard leaderBoard;
    private NonogramTiles nonogramTiles;
    private final Json json = new Json();
    Random random = new Random();

    private GameManager() {
        PREFS = Gdx.app.getPreferences(Nonogram.class.getSimpleName());
        timeLimit = PREFS.getInteger(TIME_LIMIT,60);
        nickname = PREFS.getString(NICKNAME,"");
        leaderBoard = json.fromJson(LeaderBoard.class, Gdx.files.internal(LEADERBOARD));
        nonogramTiles = json.fromJson(NonogramTiles.class, Gdx.files.internal(TILES));
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int limit) {
        timeLimit = limit;
        PREFS.putInteger(TIME_LIMIT, timeLimit);
        PREFS.flush();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String name) {
        nickname = name;
        PREFS.putString(NICKNAME, nickname);
        PREFS.flush();
    }

    public void saveNonogram(Array<Array<Float>> tile)
    {
        nonogramTiles.shapes.add(tile);
        String stringTiles = json.toJson(nonogramTiles);
        FileHandle file = Gdx.files.local(TILES);
        file.writeString(stringTiles, false);
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }
    public Array<Array<Float>> getTile()
    {
        int rand = random.nextInt(nonogramTiles.shapes.size);
        int size = nonogramTiles.shapes.get(rand).size;
        Array<Array<Float>> tiles = new Array<>();

        for(int i = 0; i < size;i++)
        {
            tiles.add(new Array<Float>());
            for(int j = 0; j < size;j++)
                tiles.get(i).add(nonogramTiles.shapes.get(rand).get(i).get(j));
        }

        for(int i = 0; i < 5;i++)
            for(int j = 0; j < 3;j++)
                tiles.get(i).insert(0,0f);

        for(int i = 0; i < 3;i++)
        {
            tiles.insert(0,new Array<Float>());
            for(int j = 0; j < 8;j++)
                tiles.first().add(0f);
        }

        int leftIndex = 0;
        int[] topIndex = new int[5];
        Arrays.fill(topIndex, 0);

        for(int r = 3; r < tiles.size;r++)
        {
            for(int c = 3; c < tiles.get(0).size;c++)
            {
                if(tiles.get(r).get(c) == 1)
                {
                    tiles.get(r).set(leftIndex,tiles.get(r).get(leftIndex) + 1);
                    tiles.get(topIndex[c - 3]).set(c,tiles.get(topIndex[c - 3]).get(c) + 1);
                }
                if(tiles.get(r).get(c) == 0 && tiles.get(r).get(leftIndex) != 0)
                    leftIndex++;
                if(tiles.get(r).get(c) == 0 && tiles.get(topIndex[c - 3]).get(c) != 0)
                    topIndex[c - 3]++;
            }
            leftIndex = 0;
        }

        return tiles;
    }

    public void setLeaderboard(String name, String time) {

        if(Integer.parseInt(leaderBoard.times.lastElement()) < Integer.parseInt(time))
        {
            leaderBoard.times.add(time);
            leaderBoard.names.add(name);
        }
        else
        {
            for (int i = 0; i < leaderBoard.names.size(); i++)
            {
                if(Integer.parseInt(leaderBoard.times.get(i)) > Integer.parseInt(time))
                {
                    leaderBoard.times.add(i,time);
                    leaderBoard.names.add(i,name);
                    break;
                }
            }
        }

        if(leaderBoard.names.size() > 10)
        {
            leaderBoard.names.removeElementAt(10);
            leaderBoard.times.removeElementAt(10);
        }

        String stringLeaderBoard = json.toJson(leaderBoard);
        FileHandle file = Gdx.files.local(LEADERBOARD);
        file.writeString(stringLeaderBoard, false);
    }
}
