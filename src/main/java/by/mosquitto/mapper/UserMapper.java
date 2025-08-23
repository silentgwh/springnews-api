package by.mosquitto.mapper;

import by.mosquitto.dto.UserDto;
import by.mosquitto.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .name(user.getName())
                .surname(user.getSurname())
                .parentName(user.getParentName())
                .creationDate(user.getCreationDate())
                .lastEditDate(user.getLastEditDate())
                .build();
    }

    public static User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .name(dto.getName())
                .surname(dto.getSurname())
                .parentName(dto.getParentName())
                .creationDate(dto.getCreationDate())
                .lastEditDate(dto.getLastEditDate())
                .build();
    }
}