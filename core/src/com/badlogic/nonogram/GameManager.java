package com.badlogic.nonogram;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
    private static final String URL_GET = "https://blockchain-nonogram.herokuapp.com/getChain";
    private static final String URL_POST = "https://blockchain-nonogram.herokuapp.com/mineBlock";
    private static final String URL_POST_EVALUATE = "https://flask-monogram.herokuapp.com/solve";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Preferences PREFS;
    private final OkHttpClient httpClient;
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
        httpClient = new OkHttpClient();
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
        leaderBoard = getLeaderBoardFromBlockChain();
        return this.leaderBoard;
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
        try {
            RequestBody body = RequestBody.create(JSON, "{\"nickname\":\"" + name + "\", \"time\":\"" + time + "\"}");
            Request request = new Request.Builder()
                    .url(URL_POST)
                    .post(body)
                    .build();
            Response response = httpClient.newCall(request).execute();
        } catch (Exception e) {

        }
    }

    public String evaluateImage(String path) {
        File f = new File(path);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", f.getName(),
                        RequestBody.create(MediaType.parse("image/jpg"), f))
                .build();

        Request request = new Request.Builder()
                .url(URL_POST_EVALUATE)
                .post(body)
                .build();

        String result = "0000000000000000000000000";

        try {
            result = httpClient.newCall(request).execute().body().string();
            Gdx.app.log("Response", result);
        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.log("Response", String.valueOf(e));
        }

        return result;
    }

    private Response makeHttpRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = this.httpClient.newCall(request).execute();
        return response;
    }

    private LeaderBoard getLeaderBoardFromBlockChain() {
        LeaderBoard leaderBoard = new LeaderBoard();
        try {
            JSONObject jsObject = new JSONObject(makeHttpRequest(URL_GET).body().string());
            JSONArray chain = jsObject.getJSONArray("chain");

            Vector<String> names = new Vector<String>();
            Vector<String> times = new Vector<String>();
            for (int i = 1; i < chain.length(); i++) {
                JSONObject block = chain.getJSONObject(i);
                JSONObject data = block.getJSONObject("data");
                String nickname = data.getString("nickname");
                String time = data.getString("time");
                names.add(nickname);
                times.add(time);
            }
            leaderBoard.names = names;
            leaderBoard.times = times;
        } catch (Exception e) {
            return json.fromJson(LeaderBoard.class, Gdx.files.internal(LEADERBOARD));
        }
        leaderBoard.sort();
        return leaderBoard;
    }
}
