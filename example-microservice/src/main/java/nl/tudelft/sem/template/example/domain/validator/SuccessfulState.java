package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;

public class SuccessfulState extends BaseValidator {
    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException {
        return true;
    }

}
