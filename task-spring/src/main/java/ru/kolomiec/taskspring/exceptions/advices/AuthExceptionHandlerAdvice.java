package ru.kolomiec.taskspring.exceptions.advices;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kolomiec.taskspring.exceptions.ResponseException;

import java.time.LocalDateTime;
import java.util.Date;


@RestControllerAdvice
public class AuthExceptionHandlerAdvice {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseException> badPasswordHandler(BadCredentialsException ex) {
        //TODO тогде уже     UNAUTHORIZED(401, HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),
        return new ResponseEntity(new ResponseException(ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseException> defunctUsername(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new ResponseException(ex.getMessage(), new Date()), HttpStatus.BAD_REQUEST);
    }
}
