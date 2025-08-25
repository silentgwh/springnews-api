package by.mosquitto.exception;

import org.springframework.http.HttpStatus;

public class NewsNotFoundException extends AppException {
    public NewsNotFoundException(Long id) {
        super("News not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}