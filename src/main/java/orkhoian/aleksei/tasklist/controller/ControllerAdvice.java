package orkhoian.aleksei.tasklist.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.domain.exception.AccessDeniedException;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.exception.ImageUploadException;
import orkhoian.aleksei.tasklist.domain.exception.NulabResponseException;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn(ex.getMessage());
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(IllegalStateException ex) {
        log.warn(ex.getMessage());
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, org.springframework.security.access.AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied() {
        log.warn("Access denied");
        return new ExceptionBody("Access denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existingMessage, newMessage) -> existingMessage + " " + newMessage)
                ));
        log.warn(exceptionBody.getMessage());
        return exceptionBody;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleConstraintViolation(ConstraintViolationException ex) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed");
        exceptionBody.setErrors(ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage)));
        log.warn(exceptionBody.getMessage());
        return exceptionBody;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleAuthentication(AuthenticationException ex) {
        log.warn(ex.getMessage());
        return new ExceptionBody("Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(ImageUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleImageUpload(ImageUploadException ex) {
        log.warn(ex.getMessage());
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler(NulabResponseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleImageUpload(NulabResponseException ex) {
        log.warn(ex.getMessage());
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionBody> handleImageUpload(ResponseStatusException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity
            .status(ex.getStatusCode())
            .body(new ExceptionBody(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(Exception ex) {
        ex.printStackTrace();
        return new ExceptionBody("Internal error");
    }
}
