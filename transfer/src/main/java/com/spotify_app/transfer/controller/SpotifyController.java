package com.spotify_app.transfer.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify_app.transfer.config.SpotifyConfig;
import com.spotify_app.transfer.entity.UserDetails;
import com.spotify_app.transfer.service.UserService;

import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.spotify_app.transfer.parser.YmParser;


@RestController
@RequestMapping("/api")
@PropertySource("classpath:application.properties")
public class SpotifyController {

    @Value("${spotify.client-id}")
    private String clientID;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${custom.server.ip}")
    private String customIP;

    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private UserService userService;

    private String code = "";

    private String name = "";

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spotify API";
    }

    @GetMapping("/login")
    @ResponseBody
    public String login() {
        SpotifyApi spotifyApi = spotifyConfig.getSpotifyObject();

        AuthorizationCodeUriRequest request = spotifyApi.authorizationCodeUri()
                .scope(// "user-read-email " +
                        "user-read-private " +
                        // "user-read-playback-state " +
                        // "user-modify-playback-state " +
                        // "user-read-currently-playing " +
                        // "user-read-recently-played " +
                                "user-top-read " +
                                // "user-follow-read " +
                                // "user-follow-modify " +
                                "user-library-read " +
                                "user-library-modify " +
                                // "streaming " +
                                // "app-remote-control " +
                                "playlist-read-private " +
                                "playlist-read-collaborative " +
                                "playlist-modify-public " +
                                "playlist-modify-private "
                // "ugc-image-upload"
                )
                .show_dialog(true)
                .build();
        URI uri = request.execute();

        return uri.toString();
    }

    @GetMapping("/callback")
    public void getCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {

        SpotifyApi spotifyApi = spotifyConfig.getSpotifyObject();

        code = userCode;
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        User user = null;

        try {
            final AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            final GetCurrentUsersProfileRequest getCurrentUsersProfile = spotifyApi.getCurrentUsersProfile().build();
            user = getCurrentUsersProfile.execute();

            name = user.getDisplayName();

            userService.updateUser(user, credentials.getAccessToken(), credentials.getRefreshToken());

            System.out.println("Expires in: " + credentials.getExpiresIn());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        response.sendRedirect(customIP + "/api/home?id=" + user.getId());
    }

    @GetMapping(value = "home")
    public String home(@RequestParam("id") String userId) {
        UserDetails userDetails = userService.getCurrentUser();
        try {
            return "Welcome " + name + "(" + userDetails.getRefId() + ")" + " to home page";
        } catch (Exception e) {
            System.out.println("Exception occurred while landing to home page: " + e);
        }

        return null;
    }

    /**
     * Transfers Yandex playlist to newly created Spotify playlist.
     *
     * @param playlistName the name of the playlist we want to create
     * @param playlistLink the URL of the Yandex playlist
     */
    @PostMapping("add-playlist") // adding from playlist we get form yandex music
    public void addPlaylist(@RequestParam("playlistName") String playlistName,
            @RequestParam("playlistLink") String playlistLink) {
        String accessToken = userService.getCurrentUser().getAccessToken();
        String userId = userService.getCurrentUser().getRefId();

        YmParser ymParser = new YmParser();
        HashMap<String, List<String>> songs = ymParser.parsing(playlistLink);

        if (songs.isEmpty()) {
            System.out.println("No songs available");
            return;
        }

        List<String> urisList = getTracksURI(songs);

        String[] uris = new String[] {};
        uris = urisList.toArray(new String[0]);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();

        CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, playlistName)
                .build();

        String playlistId = "";
        try {
            final Playlist playlist = createPlaylistRequest.execute();
            playlistId = playlist.getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (playlistId.isEmpty()) {
            return;
        }

        AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .build();
        try {
            final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();

            System.out.println("Snapshot ID: " + snapshotResult.getSnapshotId());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private List<String> getTracksURI(HashMap<String, List<String>> songs) {
        List<String> uris = new ArrayList<>();

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(userService.getCurrentUser()
                        .getAccessToken())
                .build();

        for (Map.Entry<String, List<String>> entry : songs.entrySet()) {
            String artist = entry.getKey();
            List<String> songList = entry.getValue();

            for (String song : songList) {
                String q = artist + " " + song;
                String uri = "";
                Track track = null;

                SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(q)
                        .limit(1)
                        .build();

                try {
                    final Paging<Track> trackPaging = searchTracksRequest.execute();

                    track = trackPaging.getItems()[0];
                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    System.out.println("Error: " + e.getMessage());
                }

                if (track != null) {
                    uri = track.getUri();
                    uris.add(uri);
                }
            }
        }

        return uris;

    }

    /**
     * Adds songs from a Yandex playlist to an existing Spotify playlist.
     *
     * @param yandexPlaylistLink  the URL of the Yandex playlist
     * @param spotifyPlaylistLink the URL of the Spotify playlist
     */
    @PostMapping("add-to-existing")
    public void add_to_existing(@RequestParam("yandexLink") String yandexPlaylistLink,
            @RequestParam("spotifyLink") String spotifyPlaylistLink) {
        String accessToken = userService.getCurrentUser().getAccessToken();

        YmParser ymParser = new YmParser();
        HashMap<String, List<String>> songs = ymParser.parsing(yandexPlaylistLink);

        if (songs.isEmpty()) {
            System.out.println("No songs available");
            return;
        }

        List<String> urisList = getTracksURI(songs);

        String[] uris = new String[] {};
        uris = urisList.toArray(new String[0]);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();

        String playlistId = extractPlaylistId(spotifyPlaylistLink);

        if (playlistId.isEmpty()) {
            return;
        }

        AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
                .addItemsToPlaylist(playlistId, uris)
                .build();
        try {
            final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();

            System.out.println("Snapshot ID: " + snapshotResult.getSnapshotId());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private String extractPlaylistId(String url) {
        String prefix = "/playlist/";
        int startIndex = url.indexOf(prefix) + prefix.length();
        int endIndex = url.indexOf("?", startIndex);

        if (endIndex == -1) {
            endIndex = url.length();
        }

        return url.substring(startIndex, endIndex);
    }
}