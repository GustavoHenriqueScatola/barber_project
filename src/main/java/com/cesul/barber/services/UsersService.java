
package com.cesul.barber.services;

import com.cesul.barber.dtos.UserCreationDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsersService {

    private List<UserCreationDTO> users = new ArrayList<>();

    public UserCreationDTO create(UserCreationDTO user) {
        user.setId(UUID.randomUUID());
        this.users.add(user);

        return user;
    }

    public List<UserCreationDTO> findAll() {
        return this.users;
    }

    public Optional<UserCreationDTO> findById(UUID id) {
        return this.users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<UserCreationDTO> update(UUID id, UserCreationDTO updateData) {
        Optional<UserCreationDTO> userOptional = findById(id);

        if (userOptional.isPresent()) {
            UserCreationDTO existingUser = userOptional.get();

            existingUser.setName(updateData.getName());
            existingUser.setEmail(updateData.getEmail());
            existingUser.setAvatarUrl(updateData.getAvatarUrl());
        }

        return userOptional;
    }

    public Optional<UserCreationDTO> updateAvatar(UUID id, Map<String, String> request) {
        Optional<UserCreationDTO> userOptional = findById(id);

        if (userOptional.isPresent()) {
            UserCreationDTO existingUser = userOptional.get();

            existingUser.setAvatarUrl(request.get("avatarUrl"));
        }

        return userOptional;
    }

    public boolean delete(UUID id) {
        Optional<UserCreationDTO> userOptional = findById(id);

        if (userOptional.isPresent()) {
            UserCreationDTO existingUser = userOptional.get();
            users.remove(existingUser);
            return true;
        }

        return false;
    }

    public boolean clear() {
        users.clear();
        return true;
    }
}
