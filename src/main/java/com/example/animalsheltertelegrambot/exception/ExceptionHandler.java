package com.example.animalsheltertelegrambot.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(VolunteerNotFoundException.class)
    public ResponseEntity<String> handlesVolunteerNotFoundException(VolunteerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Волантер с id = %d не найден", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AnimalNotFoundException.class)
    public ResponseEntity<String> handlesAnimalNotFoundException(AnimalNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Животное с id = %d не найдено", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handlesUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Пользователь с id = %d не найден", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<String> handlesPhotoNotFoundException(PhotoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Фотография с id = %d не найдена", e.getId()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NumberNotFoundException.class)
    public ResponseEntity<String> handlesPhotoNotFoundException(NumberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Введите номер 1 или 2");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handlesMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        e.getBindingResult().getFieldErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", "))
                );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<String> handlesHttpMessageNotReadableException(InvalidFormatException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
                            e.getValue(), e.getPath().get(e.getPath().size()-1).getFieldName(), Arrays.toString(e.getTargetType().getEnumConstants())));

    }
}
