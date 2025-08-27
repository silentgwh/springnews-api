package by.mosquitto.service;

import by.mosquitto.dto.UserDto;
import by.mosquitto.entity.User;
import by.mosquitto.exception.UserNotFoundException;
import by.mosquitto.mapper.UserMapper;
import by.mosquitto.repository.UserRepository;
import by.mosquitto.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис управления пользователями.
 *
 * Реализует:
 * - Получение пользователя по ID
 * - Получение списка всех пользователей
 * - Создание, обновление и удаление пользователя
 *
 * Особенности:
 * - Обработка ошибок через кастомное исключение UserNotFoundException
 * - Логирование: info — для действий, debug — для payload'ов, warn — при ошибках
 * - Используется @Transactional на уровне класса для операций записи
 * - Преобразование между сущностями и DTO через UserMapper
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceManager implements UserService {

    private final UserRepository userRepository;

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public UserDto getById(Long id) {
        log.info("Fetching user by id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new UserNotFoundException(id);
                });
        return UserMapper.toDto(user);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список DTO пользователей
     */
    @Override
    public List<UserDto> getAll() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    /**
     * Создаёт нового пользователя.
     *
     * @param userDto DTO с данными пользователя
     * @return созданный пользователь
     */
    @Override
    public UserDto create(UserDto userDto) {
        log.info("Creating user with username='{}'", userDto.getUsername());
        log.debug("Payload: {}", userDto);

        User user = UserMapper.toEntity(userDto);
        user.setCreationDate(LocalDateTime.now());

        User saved = userRepository.save(user);
        log.info("User created: id={}", saved.getId());
        return UserMapper.toDto(saved);
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param id идентификатор пользователя
     * @param userDto DTO с обновлёнными данными
     * @return обновлённый пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Updating user id={}", id);
        log.debug("Payload: {}", userDto);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update: id={}", id);
                    return new UserNotFoundException(id);
                });

        existing.setUsername(userDto.getUsername());
        existing.setPassword(userDto.getPassword());
        existing.setName(userDto.getName());
        existing.setSurname(userDto.getSurname());
        existing.setParentName(userDto.getParentName());
        existing.setLastEditDate(LocalDateTime.now());

        User updated = userRepository.save(existing);
        log.info("User updated: id={}", updated.getId());
        return UserMapper.toDto(updated);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public void delete(Long id) {
        log.info("Deleting user id={}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion: id={}", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }
}