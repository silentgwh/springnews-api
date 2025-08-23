package by.mosquitto.service;

import by.mosquitto.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getById(Long id);
    List<UserDto> getAll();
    UserDto create(UserDto userDto);
    UserDto update(Long id, UserDto userDto);
    void delete(Long id);
}
