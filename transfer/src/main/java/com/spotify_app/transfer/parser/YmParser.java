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
            // getting URL of GET request for playlist or album (they are different( )
            Pattern playlistPattern = Pattern.compile("users/([^/]+)/playlists/(\\d+)");
            Pattern albumPattern = Pattern.compile("album/(\\d+)");
            Matcher playlistMatcher = playlistPattern.matcher(url);
            Matcher albumMatcher = albumPattern.matcher(url);

            String newUrl;
            boolean isPlaylist = false;
            if (playlistMatcher.find()) {
                String owner = playlistMatcher.group(1);
                String kinds = playlistMatcher.group(2);
                newUrl = "https://music.yandex.ru/handlers/playlist.jsx?owner=" + owner + "&kinds=" + kinds
                        + "&light=true";
                isPlaylist = true;
            } else if (albumMatcher.find()) {
                String albumId = albumMatcher.group(1);
                newUrl = "https://music.yandex.com/handlers/album.jsx?album=" + albumId + "&light=true";
            } else {
                return data;
            }

            Connection.Response response = Jsoup.connect(newUrl)
                    .ignoreContentType(true)
                    .execute();

            String jsonResponse = response.body();
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // JSONArray tracks =
            // jsonObject.getJSONObject("playlist").getJSONArray("tracks"); можно сделать
            // если всего треков меньше 100
            JSONArray ids;
            if (isPlaylist) {
                ids = jsonObject.getJSONObject("playlist").getJSONArray("trackIds");
            } else {
                ids = jsonObject.getJSONArray("trackIds");
            }
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
            /*
             * for (String key : data.keySet()) {
             * System.out.println(key + " " + data.get(key));
             * }
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private StringBuilder getStringForGetRequest(JSONArray ids, int i, int size) {
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
            data.computeIfAbsent(artistName, c -> new ArrayList<>()).add(title);
        }
        return data;
    }

    private static final String YANDEX_MUSIC_URL_REGEX = "https?://music\\.yandex\\.ru/(playlist|album)/[0-9a-zA-Z_-]+";

    private static final Pattern YANDEX_MUSIC_PATTERN = Pattern.compile(YANDEX_MUSIC_URL_REGEX);

    /**
     * Validates if the given URL is a valid Yandex Music playlist or album link.
     *
     * @param url the URL to validate
     * @return true if the URL is a valid Yandex Music link, false otherwise
     */
    private static boolean isValidYandexMusicLink(String url) {
        return YANDEX_MUSIC_PATTERN.matcher(url).matches();
    }
}
