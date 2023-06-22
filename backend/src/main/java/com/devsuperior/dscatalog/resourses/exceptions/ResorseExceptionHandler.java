package com.devsuperior.dscatalog.resourses.exceptions;

import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ResorseExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(EntityNotFoundException e, HttpServletRequest request) { // Para interceptar a exceção
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now()); // Para pegar o instante atual
        err.setStatus(HttpStatus.NOT_FOUND.value()); // Para pegar o status
        err.setError("Resourse not found"); // Para pegar o erro
        err.setMessage(e.getMessage()); // Para pegar a mensagem
        err.setPath(request.getRequestURI()); // Para pegar o caminho da requisição
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);  // Para retornar a resposta com o status de erro do HTTP
    }
}
