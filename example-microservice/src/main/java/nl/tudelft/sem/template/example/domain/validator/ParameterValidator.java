package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ParameterValidator extends BaseValidator{

    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException {

        boolean inputStatus = areInputParametersValid(checkSubject);
        if(!inputStatus)
            throw new ValidatorException(HttpStatus.BAD_REQUEST);

        boolean enumStatus = isEnumStringValid(checkSubject);
        if(!enumStatus)
            throw new ValidatorException(HttpStatus.BAD_REQUEST);

        return super.checkNext(checkSubject);
    }

    boolean areInputParametersValid(CheckSubject checkSubject)
    {
        List<Object> inputParameters = checkSubject.getInputParameters();
        if(inputParameters == null)
            return true;
        for(Object o : inputParameters)
            if(o == null)
                return false;
        return true;
    }

    boolean isEnumStringValid(CheckSubject checkSubject) {
        if(checkSubject.getAcceptedEnumStrings() == null)
            return true;
        if(checkSubject.getEnumString() == null)
            return true;

        boolean isEnumStringAmongGoodValues = false;
        for(String goodValue : checkSubject.getAcceptedEnumStrings())
            if (goodValue.equals(checkSubject.getEnumString())) {
                isEnumStringAmongGoodValues = true;
                break;
            }
        return isEnumStringAmongGoodValues;

    }
}
