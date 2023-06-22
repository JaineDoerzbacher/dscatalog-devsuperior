package com.devsuperior.dscatalog.resourses.exceptions;

import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ResorseExceptionHandler {

    @ExceptionHandler(ResourseNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(ResourseNotFoundException e, HttpServletRequest request) { // Para interceptar a exceção
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now()); // Para pegar o instante atual
        err.setStatus(status.value()); // Para pegar o status
        err.setError("Resourse not found"); // Para pegar o erro
        err.setMessage(e.getMessage()); // Para pegar a mensagem
        err.setPath(request.getRequestURI()); // Para pegar o caminho da requisição
        return ResponseEntity.status(status).body(err);  // Para retornar a resposta com o status de erro do HTTP
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now()); // Para pegar o instante atual
        err.setStatus(status.value()); // Para pegar o status
        err.setError("Database exception"); // Para pegar o erro
        err.setMessage(e.getMessage()); // Para pegar a mensagem
        err.setPath(request.getRequestURI()); // Para pegar o caminho da requisição
        return ResponseEntity.status(status).body(err);  // Para retornar a resposta com o status de erro do HTTP
    }

}
