package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ParameterValidator extends BaseValidator{

    @Override
    public ResponseEntity<Void> handle(CheckSubject checkSubject) {
        List<Object> inputParameters = checkSubject.getInputParameters();
        for(Object o : inputParameters)
            if(o == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        boolean ok = false;
        for(String goodValue : checkSubject.getAcceptedEnumStrings())
            if (goodValue.equals(checkSubject.getEnumString())) {
                ok = true;
                break;
            }

        if(!ok)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return super.checkNext(checkSubject);
    }
}
