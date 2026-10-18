package com.umbrastack.ecommerce.controllers.exceptions;

import com.umbrastack.ecommerce.services.exceptions.DatabaseException;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException e, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;//404
        //estrtura padrão RFC 7807
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://umbrastack.com/errors/not-found"));

        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return problemDetail;
    }
    // erro de banco de dados (delete)
    @ExceptionHandler(DatabaseException.class)
    public ProblemDetail handleDatabaseException(DatabaseException e, WebRequest request){
        HttpStatus status = HttpStatus.CONFLICT; //409 conflict
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());

        problemDetail.setTitle("Database Integrity Violation");
        problemDetail.setType(URI.create("https://umbrastack.com/errors/database-conflict"));

        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return problemDetail;
    }
    // erro de validação (post/put)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request){
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT; //422 unprocessable content
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "A Validação de um ou mais campos falhou.");

        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://umbrastack.com/errors/validation-failed"));

        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()){
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problemDetail.setProperty("errors", fieldErrors);

        return problemDetail;
    }

}
