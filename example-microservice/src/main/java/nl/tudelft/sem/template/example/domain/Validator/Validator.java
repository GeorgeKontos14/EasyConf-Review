package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import org.springframework.http.ResponseEntity;

public interface Validator {
    void setNext(Validator handler);
    ResponseEntity<Void> handle(CheckSubject checkSubject);
}
