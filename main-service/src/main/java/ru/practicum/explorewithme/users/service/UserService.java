package ru.practicum.explorewithme.users.service;

import ru.practicum.explorewithme.users.dto.NewUserRequest;
import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);
    List<UserDto> getUsers(List<Long> ids, int from, int size);
    void deleteUser(Long userId);
}