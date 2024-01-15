package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseValidator implements Validator{
    private Validator next;

    public void setNext(Validator next) {
        this.next = next;
    }

    public Validator getNext() {
        return this.next;
    }

    protected ResponseEntity<Void> checkNext(CheckSubject checkSubject) {
        if(this.next == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return next.handle(checkSubject);
    }

}
