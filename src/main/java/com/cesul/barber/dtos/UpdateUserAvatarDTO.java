package com.cesul.barber.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserAvatarDTO {

    private String avatarUrl;
    private boolean avatarUrlPresent;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.avatarUrlPresent = true;
    }

    public boolean hasAvatarUrl() {
        return avatarUrlPresent;
    }
}
