package nl.tudelft.sem.template.example.domain.validator;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidatorException extends Exception {
    private final HttpStatus httpStatus;

    ValidatorException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}
