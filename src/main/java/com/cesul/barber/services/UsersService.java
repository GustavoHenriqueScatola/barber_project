package com.cesul.barber.services;

import com.cesul.barber.dtos.UserCreationDTO;
import com.cesul.barber.entities.User;
import com.cesul.barber.mappers.UserMapper;
import com.cesul.barber.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public UserCreationDTO create(UserCreationDTO user) {
        User created = usersRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDto(created);
    }

    public List<UserCreationDTO> findAll() {
        return usersRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public Optional<UserCreationDTO> findById(UUID id) {
        return usersRepository.findById(id).map(UserMapper::toDto);
    }

    @Transactional
    public Optional<UserCreationDTO> update(UUID id, UserCreationDTO updateData) {
        return usersRepository.findForUpdateById(id).map(user -> {
            user.setName(updateData.getName());
            user.setEmail(updateData.getEmail());
            user.setAvatarUrl(updateData.getAvatarUrl());
            return UserMapper.toDto(user);
        });
    }

    @Transactional
    public Optional<UserCreationDTO> updateAvatar(UUID id, String avatarUrl) {
        return usersRepository.findForUpdateById(id).map(user -> {
            user.setAvatarUrl(avatarUrl);
            return UserMapper.toDto(user);
        });
    }

    @Transactional
    public boolean delete(UUID id) {
        Optional<User> user = usersRepository.findForUpdateById(id);
        user.ifPresent(usersRepository::delete);
        return user.isPresent();
    }

    @Transactional
    public boolean clear() {
        usersRepository.deleteAllInBatch();
        return true;
    }
}
