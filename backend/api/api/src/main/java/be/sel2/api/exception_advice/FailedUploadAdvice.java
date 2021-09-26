package be.sel2.api.exception_advice;

import be.sel2.api.exceptions.EmptyFileException;
import be.sel2.api.exceptions.UploadFailedError;
import be.sel2.api.models.ErrorModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FailedUploadAdvice {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFilesize;

    private static final String BAD_REQUEST_TITLE = "Bad Request";

    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel maxUploadSizeHandler() {
        return new ErrorModel(BAD_REQUEST_TITLE, 400, "File exceeds maximum filesize of " + maxFilesize);
    }

    @ResponseBody
    @ExceptionHandler(UploadFailedError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel uploadFailHandler() {
        return new ErrorModel(BAD_REQUEST_TITLE, 400, "Was unable to save file");
    }

    @ResponseBody
    @ExceptionHandler(EmptyFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorModel emptyFileHandler() {
        return new ErrorModel(BAD_REQUEST_TITLE, 400, "Upload was empty");
    }
}
