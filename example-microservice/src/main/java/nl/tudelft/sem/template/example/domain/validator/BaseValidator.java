package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;

public abstract class BaseValidator implements Validator {
    private Validator next;

    public void setNext(Validator next) {
        this.next = next;
    }

    public Validator getNext() {
        return this.next;
    }

    protected boolean checkNext(CheckSubject checkSubject) throws ValidatorException {
        if (this.next == null) {
            return true;
        } else {
            return next.handle(checkSubject);
        }
    }

}
