package by.mosquitto.service;

import by.mosquitto.dto.UserDto;
import by.mosquitto.entity.User;
import by.mosquitto.exception.UserNotFoundException;
import by.mosquitto.mapper.UserMapper;
import by.mosquitto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceManagerTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private UserServiceManager userService;

    private User user;
    private UserDto dto;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("thor")
                .password("secure")
                .name("Thorin")
                .surname("Oakenshield")
                .parentName("Thrain")
                .creationDate(LocalDateTime.now().minusDays(1))
                .lastEditDate(LocalDateTime.now())
                .build();

        dto = UserDto.builder()
                .id(1L)
                .username("thor")
                .password("secure")
                .name("Thorin")
                .surname("Oakenshield")
                .parentName("Thrain")
                .creationDate(user.getCreationDate())
                .lastEditDate(user.getLastEditDate())
                .build();
    }

    @Test
    void getById_shouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toDto(user)).thenReturn(dto);

            UserDto result = userService.getById(1L);

            assertEquals(dto, result);
        }
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void getAll_shouldReturnListOfDtos() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toDto(user)).thenReturn(dto);

            List<UserDto> result = userService.getAll();

            assertEquals(1, result.size());
            assertEquals(dto, result.get(0));
        }
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        User entityToSave = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .name(dto.getName())
                .surname(dto.getSurname())
                .parentName(dto.getParentName())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto)).thenReturn(entityToSave);
            mocked.when(() -> UserMapper.toDto(user)).thenReturn(dto);

            UserDto result = userService.create(dto);

            assertEquals(dto, result);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void update_shouldModifyAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toDto(user)).thenReturn(dto);

            UserDto result = userService.update(1L, dto);

            assertEquals(dto.getUsername(), result.getUsername());
            verify(userRepository).save(user);
        }
    }

    @Test
    void update_shouldThrowIfUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(999L, dto));
    }

    @Test
    void delete_shouldRemoveIfExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.delete(999L));
    }
}
