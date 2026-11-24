package com.cesul.barber.mappers;

import com.cesul.barber.dtos.UserCreationDTO;
import com.cesul.barber.entities.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(UserCreationDTO dto) {
        return new User(dto.getName(), dto.getEmail(), dto.getAvatarUrl());
    }

    public static UserCreationDTO toDto(User user) {
        UserCreationDTO dto = new UserCreationDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}
