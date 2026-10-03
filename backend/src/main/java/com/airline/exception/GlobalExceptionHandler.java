package com.airline.exception;

import com.airline.dto.response.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
            "Not Found", ex.getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<ApiErrorDto> handleSeatConflict(SeatUnavailableException ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.CONFLICT.value(),
            "Conflict", ex.getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiErrorDto> handlePaymentFailure(PaymentFailedException ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
            "Payment Failed", ex.getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
            "Bad Request", ex.getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));

        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
            "Bad Request", detail, request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
            "Bad Request", "Malformed JSON: " + ex.getMostSpecificCause().getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneralException(Exception ex, HttpServletRequest request) {
        ApiErrorDto error = new ApiErrorDto(
            LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error", ex.getMessage(), request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
