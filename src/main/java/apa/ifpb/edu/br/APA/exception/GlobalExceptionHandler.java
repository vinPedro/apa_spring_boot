package apa.ifpb.edu.br.APA.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {

        ErrorMessage response = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(RecursoJaExisteException.class)
    public ResponseEntity<ErrorMessage> handleRecursoJaExiste(RecursoJaExisteException ex) {

        ErrorMessage response = new ErrorMessage(HttpStatus.CONFLICT.value(),ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(OperacaoInvalidaException.class)
    public ResponseEntity<ErrorMessage> handleOperacaoInvalida(OperacaoInvalidaException ex) {

        ErrorMessage response = new ErrorMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
