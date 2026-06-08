package com.example.demo.student.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.helper.CustomResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CustomResponse> handleNotFound(ResourceNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CustomResponse("NOT_FOUND", ex.getMessage()));
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<CustomResponse> handleConflict(ConflictException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomResponse("CONFLICT", ex.getMessage()));
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<CustomResponse> handleValidation(ValidationException ex) {

		return ResponseEntity.badRequest().body(new CustomResponse("VALIDATION_ERROR", ex.getMessage()));
	}
}
