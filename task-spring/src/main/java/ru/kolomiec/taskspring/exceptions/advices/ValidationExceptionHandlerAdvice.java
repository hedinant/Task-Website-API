package ru.kolomiec.taskspring.exceptions.advices;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kolomiec.taskspring.exceptions.ResponseException;


@RestControllerAdvice
public class ValidationExceptionHandlerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseException> constraintValidationException(ConstraintViolationException ex) {
        return new ResponseEntity(new ResponseException(ex.getSQLException().getMessage(), new Date()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<List<ResponseException>> bindValidationException(BindException ex) {
        List<ResponseException> responseExceptionList = convertFieldErrorsToResponseExceptionList(ex.getFieldErrors());
        return new ResponseEntity<>(responseExceptionList, HttpStatus.BAD_REQUEST);
    }

    private List<ResponseException> convertFieldErrorsToResponseExceptionList(List<FieldError> errors) {
        List<ResponseException> responseExceptionsList = new ArrayList<>();
        // TODO errors.forEach(a -> {
        /* или
           errors.stream().map(
                fieldError -> new ResponseException(a.getField() + " " + a.getDefaultMessage(), new Date()))
                .collect(Collectors.toList());
         Это с точки зрения stream api более правильный стиль.
         */
        errors.stream().forEach(a -> {
            responseExceptionsList.add(new ResponseException(a.getField() + " " + a.getDefaultMessage(), new Date()));
        });
        return responseExceptionsList;
    }
}
