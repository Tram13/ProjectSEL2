package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.conflict.ConflictException;
import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ConflictAdvice {

    @ResponseBody
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorModel conflictHandler(ConflictException ex) {
        return new ErrorModel(ex);
    }
}
