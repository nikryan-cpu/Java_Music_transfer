package com.spotify_app.transfer.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YmParser {
    public HashMap<String, List<String>> parsing(String url) {
        HashMap<String, List<String>> data = new HashMap<>();
        try {
            //getting URL of GET request for playlist
            Pattern pattern = Pattern.compile("users/([^/]+)/playlists/(\\d+)");
            Matcher matcher = pattern.matcher(url);

            String newUrl = "";
            if (matcher.find()) {
                String owner = matcher.group(1);
                String kinds = matcher.group(2);

                newUrl = "https://music.yandex.ru/handlers/playlist.jsx?owner=" + owner + "&kinds=" + kinds + "&light=true";
                System.out.println("New URL: " + newUrl);
            } else {
                System.out.println("Invalid playlist URL format.");
            }

            Connection.Response response = Jsoup.connect(newUrl)
                    .ignoreContentType(true)
                    .execute();

            String jsonResponse = response.body();
            JSONObject jsonObject = new JSONObject(jsonResponse);

            //JSONArray tracks = jsonObject.getJSONObject("playlist").getJSONArray("tracks"); можно сделать если всего треков меньше 100
            JSONArray ids = jsonObject.getJSONObject("playlist").getJSONArray("trackIds");
            int len = ids.length();
            for (int i = 0; i < ids.length() - 100; i += 100) {
                StringBuilder ans = getStringForGetRequest(ids, i, 100);
                Connection.Response postResponse = Jsoup.connect("https://music.yandex.ru/handlers/track-entries.jsx")
                        .method(Connection.Method.POST)
                        .data("entries", ans.toString())
                        .ignoreContentType(true)
                        .execute();

                String postJsonResponse = postResponse.body();
                JSONArray postJsonArray = new JSONArray(postJsonResponse);
                HashMap<String, List<String>> data1 = makeMapOfTracks(postJsonArray);
                data.putAll(data1);
                len -= 100;
            }


            StringBuilder ans = getStringForGetRequest(ids, ids.length() - len, len);
            Connection.Response postResponse = Jsoup.connect("https://music.yandex.ru/handlers/track-entries.jsx")
                    .method(Connection.Method.POST)
                    .data("entries", ans.toString())
                    .ignoreContentType(true)
                    .execute();

            String postJsonResponse = postResponse.body();
            JSONArray postJsonArray = new JSONArray(postJsonResponse);

            HashMap<String, List<String>> data1 = makeMapOfTracks(postJsonArray);
            data.putAll(data1);
            for (String key : data.keySet()) {
                System.out.println(key + " " + data.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }


    private StringBuilder getStringForGetRequest(JSONArray ids, int i, int size){
        StringBuilder ans = new StringBuilder();
        for (int j = 0; j < size; j++) {
            if (ids.get(j + i) instanceof Integer) {
                int trackId = ids.getInt(j + i);
                ans.append(",").append(trackId);
            } else {
                String trackId = ids.getString(j + i);
                ans.append(",").append(trackId);
            }
        }
        ans = new StringBuilder(ans.substring(1));
        return ans;
    }


    private HashMap<String, List<String>> makeMapOfTracks(JSONArray postJsonArray) {
        HashMap<String, List<String>> data = new HashMap<>();
        for (int k = 0; k < postJsonArray.length(); k++) {
            JSONObject track = postJsonArray.getJSONObject(k);
            String title = track.getString("title");
            StringBuilder artistNames = new StringBuilder();
            JSONArray artists = track.getJSONArray("artists");
            for (int j = 0; j < artists.length(); j++) {
                if (j > 0) {
                    artistNames.append(", ");
                }
                artistNames.append(artists.getJSONObject(j).getString("name"));
            }
            String artistName = artistNames.toString();
            data.computeIfAbsent(artistName, key -> new ArrayList<>()).add(title);
        }
        return data;
    }
}
