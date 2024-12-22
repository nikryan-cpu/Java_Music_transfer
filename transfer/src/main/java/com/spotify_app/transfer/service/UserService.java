package com.spotify_app.transfer.service;

import com.spotify_app.transfer.entity.UserDetails; 

import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Service
public class UserService {
    private UserDetails userDetails;
    // private static final String USERNAME = "user";
    // private static final String PASSWORD = "password";

    // public boolean authenticate(String username, String password) {
    //     return USERNAME.equals(username) && PASSWORD.equals(password);
    // }

    // public void setCurrentUser(UserDetails user) {
    //     this.userDetails = user;
    // }

    public UserDetails getCurrentUser() {
        return userDetails;
    }

    public UserDetails updateUser(User user, String accessToken, String refreshToken) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setRefId(user.getId());
            //userDetails.setId(Integer.parseInt(user.getId()));
        }

        userDetails.setAccessToken(accessToken);
        userDetails.setRefreshToken(refreshToken);

        Image[] images = user.getImages();
        if (images != null && images.length > 0) {
            userDetails.setAvatarUrl(images[0].getUrl());
        }

        return userDetails;
    }
}