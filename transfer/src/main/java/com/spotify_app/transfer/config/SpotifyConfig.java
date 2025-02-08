package com.spotify_app.transfer.config;

import java.net.URI;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
@NoArgsConstructor
@Service
public class SpotifyConfig {
	
	@Value("${redirect.server.ip}")
	private String redirectIP;

	@Value("${spotify.client-id}")
	private String clientID;

	@Value("${spotify.client-secret}")
	private String clientSecret;
	
	public SpotifyApi getSpotifyObject() {
		 URI redirectedURL =  SpotifyHttpManager.makeUri(redirectIP);
		 
		 return new SpotifyApi
				 .Builder()
				 .setClientId(clientID)
				 .setClientSecret(clientSecret)
				 .setRedirectUri(redirectedURL)
				 .build();
	}
}	