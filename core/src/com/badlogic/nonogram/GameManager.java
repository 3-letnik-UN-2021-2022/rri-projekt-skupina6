package com.badlogic.nonogram;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

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
    private final NonogramTiles nonogramTiles;
    private final Json json = new Json();
    Random r = new Random();

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

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }
    public Array<Array<Float>> getTile()
    {
        int rand = r.nextInt(nonogramTiles.shapes.size);
        return nonogramTiles.shapes.get(rand);
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
