package com.spotify_app.transfer.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Data
public class UserDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 3937414011943770889L;

    private String accessToken;
    private String refreshToken;
    private String refId;
    private String avatarUrl;
}
