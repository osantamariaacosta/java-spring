package com.project.demo.logic.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalResponseHandler {
    @ResponseBody
    public <T> ResponseEntity<?> handleResponse(String message, T body, HttpStatus status, HttpServletRequest request) {
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());

        if (body instanceof HttpResponse) {
            HttpResponse<?> response = (HttpResponse<?>) body;
            response.setMeta(meta); // Agregar los metadatos
            return new ResponseEntity<>(response, status);
        }

        HttpResponse<T> response = new HttpResponse<>(message, body, meta);
        return new ResponseEntity<>(response, status);
    }

    @ResponseBody
    public <T> ResponseEntity<?> handleResponse(String message, T body, HttpStatus status, Meta meta) {

        if (body instanceof HttpResponse) {
            HttpResponse<?> response = (HttpResponse<?>) body;
            response.setMeta(meta); // Agregar los metadatos
            return new ResponseEntity<>(response, status);
        }

        HttpResponse<T> response = new HttpResponse<>(message, body, meta);
        return new ResponseEntity<>(response, status);
    }
}
