package by.mosquitto.service;

import by.mosquitto.dto.UserDto;
import by.mosquitto.entity.User;
import by.mosquitto.mapper.UserMapper;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static by.mosquitto.mapper.UserMapper.toDto;
import static by.mosquitto.mapper.UserMapper.toEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceManager implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = toEntity(userDto);
        user.setCreationDate(LocalDateTime.now());
        User saved = userRepository.save(user);
        return toDto(saved);
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

        return toDto(userRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}