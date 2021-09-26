package be.sel2.api.models;

import be.sel2.api.exceptions.ForbiddenException;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.exceptions.conflict.ConflictException;
import be.sel2.api.exceptions.not_found.NotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

/**
 * Model used to return errors following the format:
 * <pre>
 * {
 *     "title": "string",
 *     "status": 500,
 *     "detail": "string",
 *     "errors": [
 *          {
 *              "parameter": "string",
 *              "message": "string"
 *          }, ...
 *     ]
 * }
 * </pre>
 */
@Getter
public class ErrorModel {
    private final String title;
    private final Integer status;
    private final String detail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<InvalidInputException.ParamErrorPair> errors;

    public ErrorModel(InvalidInputException ex) {
        this("Bad Request", 400, "Invalid input", ex.getErrors());
    }

    public ErrorModel(ForbiddenException ex) {
        this("Forbidden", 403, ex.getMessage());
    }

    public ErrorModel(NotFoundException ex) {
        this("Not Found", 404, ex.getMessage());
    }

    public ErrorModel(ConflictException ex) {
        this("Conflict", 409, ex.getMessage());
    }

    public ErrorModel(String title, Integer status, String detail) {
        this(title, status, detail, null);
    }

    public ErrorModel(String title, Integer status, String detail, List<InvalidInputException.ParamErrorPair> errors) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.errors = errors;
    }
}
