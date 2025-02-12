package com.spotify_app.transfer.service;



import com.spotify_app.transfer.entity.UserDetails;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Service
public class UserService {
    private UserDetails userDetails;

    public UserDetails getCurrentUser() {
        return userDetails;
    }

    public void updateUser(User user, String accessToken, String refreshToken) {
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setRefId(user.getId());
        }

        userDetails.setAccessToken(accessToken);
        userDetails.setRefreshToken(refreshToken);

        Image[] images = user.getImages();
        if (images != null && images.length > 0) {
            userDetails.setAvatarUrl(images[0].getUrl());
        }

    }
}