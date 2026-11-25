package com.example.demo;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ServerErrorDto> handleResourceNotFound(
            ResourceNotFoundException ex) {
        var error = new ServerErrorDto(
                "Сущность не найдена", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ServerErrorDto> handleConflict(
            ConflictException ex) {
        var error = new ServerErrorDto(
                "Конфликт данных", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        var error = new ServerErrorDto(
                "Ошибка валидации", details, LocalDateTime.now());

        return new ResponseEntity<>(error, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (status.is5xxServerError()) {
            log.error("MVC server error", ex);
            var error = new ServerErrorDto(
                    "Внутренняя ошибка сервера",
                    "Повторите запрос позже",
                    LocalDateTime.now());

            return new ResponseEntity<>(error, headers, status);
        }
        var error = new ServerErrorDto(
                "Ошибка HTTP-запроса",
                "Проверьте адрес, параметры, метод и формат запроса",
                LocalDateTime.now());

        return new ResponseEntity<>(error, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerErrorDto> handleUnexpected(Exception ex) {
        log.error("Unexpected server error", ex);
        var error = new ServerErrorDto(
                "Внутренняя ошибка сервера",
                "Повторите запрос позже",
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}