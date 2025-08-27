package by.mosquitto.api;

import by.mosquitto.dto.response.ErrorResponse;
import by.mosquitto.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений.
 *
 * Обрабатывает:
 * - {@link AppException} — с кастомным статусом и сообщением
 * - Все прочие {@link Exception} — как 500 Internal Server Error
 *
 * Формирует единый формат ответа {@link ErrorResponse}, содержащий:
 * - HTTP статус и reason-фразу
 * - Сообщение об ошибке
 * - Путь запроса
 * - Таймстамп
 *
 * Логирует ошибки: handled — с деталями, unexpected — с трейсом.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

    /**
     * Обрабатывает исключения бизнес-логики типа {@link AppException}.
     *
     * @param ex      выброшенное исключение
     * @param request текущий HTTP-запрос
     * @return структурированный ответ об ошибке с кастомным статусом
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        log.error("Handled AppException — status={}, message='{}', path='{}'",
                ex.getStatus(), ex.getMessage(), request.getRequestURI());
        return buildErrorResponse(ex.getStatus(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Обрабатывает все неожиданные исключения.
     *
     * @param ex      выброшенное исключение
     * @param request текущий HTTP-запрос
     * @return ответ с кодом 500 и сообщением "Unexpected error"
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at path='{}'", request.getRequestURI(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI());
    }

    /**
     * Формирует объект {@link ErrorResponse} и оборачивает его в {@link ResponseEntity}.
     *
     * @param status HTTP-статус
     * @param message сообщение об ошибке
     * @param path путь запроса
     * @return готовый HTTP-ответ с телом ошибки
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}