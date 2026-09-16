package com.thartheeb.catalog.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "The request contains invalid fields.", request);
        detail.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(detail);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, HttpRequestMethodNotSupportedException.class,
        HttpMediaTypeNotSupportedException.class, MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class, ConstraintViolationException.class,
        NoResourceFoundException.class})
    ResponseEntity<ProblemDetail> framework(Exception ex, HttpServletRequest request) {
        if (ex instanceof HttpRequestMethodNotSupportedException) return response(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "The HTTP method is not supported for this endpoint.", request);
        if (ex instanceof HttpMediaTypeNotSupportedException) return response(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "The request content type is not supported.", request);
        if (ex instanceof NoResourceFoundException) return response(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "The requested resource was not found.", request);
        if (ex instanceof HttpMessageNotReadableException) return response(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "The request body is missing or malformed.", request);
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_PARAMETER", "A request parameter is missing or invalid.", request);
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ProblemDetail> status(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return response(status, status.name(), ex.getReason() == null ? status.getReasonPhrase() : ex.getReason(), request);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> conflict(DataIntegrityViolationException ex, HttpServletRequest request) { return response(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data.", request); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> unexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled API error for {}", request.getRequestURI(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected server error occurred.", request);
    }
    private ResponseEntity<ProblemDetail> response(HttpStatus status, String code, String message, HttpServletRequest request) { return ResponseEntity.status(status).body(problem(status, code, message, request)); }
    private ProblemDetail problem(HttpStatus status, String code, String message, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(code); detail.setType(URI.create("https://api.thartheeb.qa/problems/" + code.toLowerCase()));
        detail.setInstance(URI.create(request.getRequestURI())); detail.setProperty("code", code);
        detail.setProperty("error", status.getReasonPhrase()); detail.setProperty("message", message);
        detail.setProperty("timestamp", Instant.now()); detail.setProperty("path", request.getRequestURI());
        return detail;
    }
}
