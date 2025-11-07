package apa.ifpb.edu.br.APA.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoJaExisteException extends RuntimeException {

    public RecursoJaExisteException(String message) {
        super(message);
    }
}
