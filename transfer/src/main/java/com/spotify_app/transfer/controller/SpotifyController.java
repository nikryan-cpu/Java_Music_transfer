package com.spotify_app.transfer.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify_app.transfer.config.SpotifyConfig;
import com.spotify_app.transfer.entity.UserDetails;
import com.spotify_app.transfer.service.UserService;

import se.michaelthelin.spotify.model_objects.specification.User;

import jakarta.servlet.http.HttpServletResponse;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
                                // "playlist-read-private " +
                                // "playlist-read-collaborative " +
                                // "playlist-modify-public " +
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
}