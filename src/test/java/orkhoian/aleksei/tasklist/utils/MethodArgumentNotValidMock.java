package orkhoian.aleksei.tasklist.utils;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

public final class MethodArgumentNotValidMock {

    private MethodArgumentNotValidMock() {
    }

    public static MethodArgumentNotValidException getMethodArgumentNotValidException() throws NoSuchMethodException {
        BindingResult bindingResult = new BindException(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "name", "cannot be empty"));

        return new MethodArgumentNotValidException(
            new MethodParameter(getTestMethod(), 0),
            bindingResult
        );
    }

    private static Method getTestMethod() throws NoSuchMethodException {
        return MethodArgumentNotValidMock.class.getDeclaredMethod("fakeValidationMethod", String.class);
    }

    private static void fakeValidationMethod(String param) {
    }
}
