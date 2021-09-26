package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ForbiddenAdvice {

    @ResponseBody
    @ExceptionHandler(ForbiddenException.class) // Which message do we receive when a ForbiddenException is thrown?
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorModel forbiddenHandler(ForbiddenException ex) {
        return new ErrorModel(ex);
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    // Which message do we receive when a AccessDeniedException is thrown?
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorModel accessDeniedHandler(AccessDeniedException ex) {
        return new ErrorModel("Forbidden", 403, ex.getMessage());
    }
}
