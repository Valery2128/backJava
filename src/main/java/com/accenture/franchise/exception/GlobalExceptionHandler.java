package com.accenture.franchise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

/* 
  Con este controlador centralizo el manejo de errores. 
  Me ayuda a mantener los controladores limpios y a que 
  el cliente siempre reciba un JSON estructurado.
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* 
       Capturo las RuntimeException que suelo lanzar cuando un recurso 
       no existe. Prefiero esto a que el servidor mande un error 500
       con una traza técnica que nadie entiende.
    */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /* 
       Si el cliente manda datos que no cumplen mis reglas (como un nombre vacío),
       aqu atrapo esos errores de validacin y devuelvo qu campo fall.
    */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
