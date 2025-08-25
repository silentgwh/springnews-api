package by.mosquitto.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends AppException {
    public EntityAlreadyExistsException(String entity, String field, String value) {
        super(entity + " already exists with " + field + ": " + value, HttpStatus.CONFLICT);
    }
}