package by.mosquitto.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends AppException {
    public CommentNotFoundException(Long id) {
        super("Comment not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}