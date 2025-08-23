package by.mosquitto.service;

import by.mosquitto.dto.UserDto;
import by.mosquitto.entity.User;
import by.mosquitto.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceManager implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = mapToEntity(userDto);
        user.setCreationDate(LocalDateTime.now());
        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        existing.setUsername(userDto.getUsername());
        existing.setPassword(userDto.getPassword());
        existing.setName(userDto.getName());
        existing.setSurname(userDto.getSurname());
        existing.setParentName(userDto.getParentName());
        existing.setLastEditDate(LocalDateTime.now());

        return mapToDto(userRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private User mapToEntity(UserDto dto) {
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

    private UserDto mapToDto(User user) {
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
}
