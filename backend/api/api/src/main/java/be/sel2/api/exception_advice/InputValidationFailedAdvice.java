package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.InputValidationFailedException;
import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InputValidationFailedAdvice {

    @ResponseBody
    @ExceptionHandler(InputValidationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorModel inputValidationFailed() {
        return new ErrorModel("Input validation failed", 500, "Could not validate the length of the input.");
    }
}
