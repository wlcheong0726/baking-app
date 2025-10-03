package com.wenglam.baking_app.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.wenglam.baking_app.dto.ErrorResponseDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class) // Http status code: 500
        public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,
                        HttpServletRequest httpServletRequest) {
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                                httpServletRequest.getRequestURI(),
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                exception.getMessage(),
                                LocalDateTime.now());
                return ResponseEntity.internalServerError().body(errorResponseDto);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class) // Http status code: 400
        public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception,
                        HttpServletRequest httpServletRequest) {
                Map<String, String> errors = new HashMap<>();
                List<FieldError> fieldErrorList = exception.getBindingResult().getFieldErrors();
                fieldErrorList.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                                httpServletRequest.getRequestURI(),
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                errors.toString(),
                                LocalDateTime.now());

                return ResponseEntity.badRequest().body(errorResponseDto);
        }

        @ExceptionHandler(IllegalArgumentException.class) // Http status code: 400
        public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception,
                        HttpServletRequest httpServletRequest) {
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                                httpServletRequest.getRequestURI(),
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                LocalDateTime.now());

                return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class) // Http status code: 400
        public ResponseEntity<ErrorResponseDto> handleMaxUploadSizeExceededException(
                        MaxUploadSizeExceededException exception,
                        HttpServletRequest httpServletRequest) {
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                                httpServletRequest.getRequestURI(),
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage(),
                                LocalDateTime.now());

                return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(EntityNotFoundException.class) // Http status code: 404
        public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException exception,
                        HttpServletRequest httpServletRequest) {
                ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                                httpServletRequest.getRequestURI(),
                                HttpStatus.NOT_FOUND,
                                exception.getMessage(),
                                LocalDateTime.now());

                return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
        }

        // TODO: DataIntegrityViolationException - 409
}
