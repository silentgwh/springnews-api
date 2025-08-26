package by.mosquitto.api;

import by.mosquitto.dto.UserDto;
import by.mosquitto.service.contract.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .id(1L)
                .username("alice")
                .password("pass123")
                .name("Alice")
                .surname("Smith")
                .parentName("Marie")
                .creationDate(LocalDateTime.now().minusDays(15))
                .lastEditDate(LocalDateTime.now().minusDays(14))
                .build();
    }

    @Test
    void getUser_shouldReturnUserDto() {
        Mockito.when(userService.getById(1L)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUser(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<UserDto> users = List.of(user);
        Mockito.when(userService.getAll()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(users, response.getBody());
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        Mockito.when(userService.create(user)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.createUser(user);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        Mockito.when(userService.update(1L, user)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.updateUser(1L, user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void deleteUser_shouldReturnNoContent() {
        Mockito.doNothing().when(userService).delete(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
