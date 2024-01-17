package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;

public interface Validator {
    Validator setNext(Validator handler);

    boolean handle(CheckSubject checkSubject) throws ValidatorException;
}
