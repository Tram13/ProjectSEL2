package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class InvalidInputAdvice {

    //Is necessary?
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            errors.put("message", message);
            String parameter = ((FieldError) error).getField();
            errors.put("parameter", parameter);
        });
        return errors;
    }

    @ResponseBody
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel handleInvalidInput(InvalidInputException ex) {
        return new ErrorModel(ex);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel handleInvalidInput(HttpMessageNotReadableException ex) {
        return new ErrorModel("Bad Request", 400, ex.getMostSpecificCause().getMessage());
    }
}
