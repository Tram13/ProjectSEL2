package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.not_found.NotFoundException;
import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorModel contactNotFoundHandler(NotFoundException ex) {
        return new ErrorModel(ex);
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ErrorModel methodNotSupported() {
        return new ErrorModel("Method Not Allowed", 405, "This method is not allowed here");
    }
}
