package by.mosquitto.api;

import by.mosquitto.dto.UserDto;
import by.mosquitto.service.contract.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 *
 * Эндпоинты:
 * - Получение пользователя по ID
 * - Получение всех пользователей
 * - Создание нового пользователя
 * - Обновление существующего пользователя
 * - Удаление пользователя
 *
 * Все действия логируются. Используется валидация входных данных.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("GET /api/users/{} — fetch user by ID", id);
        return ResponseEntity.ok(userService.getById(id));
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список DTO пользователей
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /api/users — fetch all users");
        return ResponseEntity.ok(userService.getAll());
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto DTO с данными пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("POST /api/users — create user");
        log.debug("Payload: {}", userDto);
        UserDto created = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновляет данные пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @param userDto DTO с обновлёнными данными
     * @return обновлённый пользователь
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        log.info("PUT /api/users/{} — update user", id);
        log.debug("Payload: {}", userDto);
        return ResponseEntity.ok(userService.update(id, userDto));
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return пустой ответ с кодом 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} — delete user", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}