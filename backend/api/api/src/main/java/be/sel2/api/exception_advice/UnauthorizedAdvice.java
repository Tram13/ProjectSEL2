package be.sel2.api.exception_advice;

import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UnauthorizedAdvice {

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    // Which message do we receive when a BadCredentialsException is thrown?
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel badCredentialsHandler() {
        return new ErrorModel("Bad Request", 400, "Incorrect username or password");
    }
}
