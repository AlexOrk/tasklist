package orkhoian.aleksei.tasklist.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.exception.ImageUploadException;
import orkhoian.aleksei.tasklist.domain.exception.NulabResponseException;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.utils.MethodArgumentNotValidMock;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerAdviceTest {

    @InjectMocks
    private ControllerAdvice controllerAdvice;

    @Test
    @DisplayName("handleResourceNotFoundException handles ResourceNotFoundException successfully")
    void handleResourceNotFoundException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";

        ExceptionBody exceptionBody =
            controllerAdvice.handleResourceNotFoundException(new ResourceNotFoundException(exceptionMessage));
        HttpStatus status = getResponseStatus("handleResourceNotFoundException", ResourceNotFoundException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, status);
    }

    @Test
    @DisplayName("handleIllegalStateException handles IllegalStateException successfully")
    void handleIllegalStateException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";

        ExceptionBody exceptionBody = controllerAdvice.handleIllegalStateException(new IllegalStateException(exceptionMessage));
        HttpStatus status = getResponseStatus("handleIllegalStateException", IllegalStateException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, status);
    }

    @Test
    @DisplayName("handleAccessDeniedException handles AccessDeniedException successfully")
    void handleAccessDeniedException() throws NoSuchMethodException {
        String exceptionMessage = "Access denied";

        ExceptionBody exceptionBody = controllerAdvice.handleAccessDeniedException();
        HttpStatus status = ControllerAdvice.class
            .getDeclaredMethod("handleAccessDeniedException")
            .getAnnotation(ResponseStatus.class)
            .value();

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, status);
    }

    @Test
    @DisplayName("handleMethodArgumentNotValidException handles MethodArgumentNotValidException successfully")
    void handleMethodArgumentNotValidException() throws NoSuchMethodException {
        String exceptionMessage = "Validation failed";
        MethodArgumentNotValidException exception = MethodArgumentNotValidMock.getMethodArgumentNotValidException();

        ExceptionBody exceptionBody = controllerAdvice.handleMethodArgumentNotValidException(exception);
        HttpStatus expectedStatus =
            getResponseStatus("handleMethodArgumentNotValidException", MethodArgumentNotValidException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertTrue(exceptionBody.getErrors().containsKey("name"));
        assertTrue(exceptionBody.getErrors().containsValue("cannot be empty"));
        assertEquals(HttpStatus.BAD_REQUEST, expectedStatus);
    }

    @Test
    @DisplayName("handleConstraintViolationException handles ConstraintViolationException successfully")
    void handleConstraintViolationException() throws NoSuchMethodException {
        String exceptionMessage = "Validation failed";
        String invalidField = "name";
        String violationMessage = "must not be null";

        Path path = Mockito.mock(Path.class);
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);

        when(path.toString()).thenReturn(invalidField);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(violationMessage);

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ExceptionBody exceptionBody = controllerAdvice.handleConstraintViolationException(exception);
        HttpStatus expectedStatus =
            getResponseStatus("handleConstraintViolationException", ConstraintViolationException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertTrue(exceptionBody.getErrors().containsKey(invalidField));
        assertTrue(exceptionBody.getErrors().containsValue(violationMessage));
        assertEquals(HttpStatus.BAD_REQUEST, expectedStatus);
    }

    @Test
    @DisplayName("handleAuthenticationException handles AuthenticationException successfully")
    void handleAuthenticationException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";
        String logMessage = "Authentication failed: " + exceptionMessage;

        ExceptionBody exceptionBody = controllerAdvice.handleAuthenticationException(new BadCredentialsException(exceptionMessage));
        HttpStatus expectedStatus = getResponseStatus("handleAuthenticationException", AuthenticationException.class);

        assertEquals(logMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, expectedStatus);
    }

    @Test
    @DisplayName("handleImageUploadException handles ImageUploadException successfully")
    void handleImageUploadException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";

        ExceptionBody exceptionBody = controllerAdvice.handleImageUploadException(new ImageUploadException(exceptionMessage));
        HttpStatus expectedStatus = getResponseStatus("handleImageUploadException", ImageUploadException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, expectedStatus);
    }

    @Test
    @DisplayName("handleNulabResponseException handles NulabResponseException successfully")
    void handleNulabResponseException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";

        ExceptionBody exceptionBody = controllerAdvice.handleNulabResponseException(new NulabResponseException(exceptionMessage));
        HttpStatus expectedStatus = getResponseStatus("handleNulabResponseException", NulabResponseException.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, expectedStatus);
    }

    @Test
    @DisplayName("handleResponseStatusException handles ResponseStatusException successfully")
    void handleResponseStatusException() {
        String exceptionMessage = "Test exception";
        String responseMessage = "400 BAD_REQUEST \"%s\"".formatted(exceptionMessage);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ResponseEntity<ExceptionBody> exceptionBody =
            controllerAdvice.handleResponseStatusException(new ResponseStatusException(status, exceptionMessage));

        assertEquals(responseMessage, Objects.requireNonNull(exceptionBody.getBody()).getMessage());
        assertEquals(status, exceptionBody.getStatusCode());
    }

    @Test
    @DisplayName("handleAuthenticationCredentialsNotFoundException handles AuthenticationCredentialsNotFoundException successfully")
    void handleAuthenticationCredentialsNotFoundException() {
        String exceptionMessage = "Test exception";

        ResponseEntity<ExceptionBody> exceptionBody =
            controllerAdvice.handleAuthenticationCredentialsNotFoundException(
                new AuthenticationCredentialsNotFoundException(exceptionMessage));

        assertEquals(exceptionMessage, Objects.requireNonNull(exceptionBody.getBody()).getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exceptionBody.getStatusCode());
    }

    @Test
    @DisplayName("handleMissingServletRequestPartException handles MissingServletRequestPartException successfully")
    void handleMissingServletRequestPartException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";
        String responseMessage = "Required part '%s' is not present.".formatted(exceptionMessage);

        ExceptionBody exceptionBody =
            controllerAdvice.handleMissingServletRequestPartException(new MissingServletRequestPartException(exceptionMessage));
        HttpStatus expectedStatus =
            getResponseStatus("handleMissingServletRequestPartException", MissingServletRequestPartException.class);

        assertEquals(responseMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, expectedStatus);
    }

    @Test
    @DisplayName("handleException handles Exception successfully")
    void handleException() throws NoSuchMethodException {
        String exceptionMessage = "Internal error";

        ExceptionBody exceptionBody = controllerAdvice.handleException(new Exception(exceptionMessage));
        HttpStatus expectedStatus = getResponseStatus("handleException", Exception.class);

        assertEquals(exceptionMessage, exceptionBody.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, expectedStatus);
    }

    private HttpStatus getResponseStatus(String methodName, Class<?> exceptionClass) throws NoSuchMethodException {
        return ControllerAdvice.class
            .getDeclaredMethod(methodName, exceptionClass)
            .getAnnotation(ResponseStatus.class)
            .value();
    }
}
