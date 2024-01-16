package nl.tudelft.sem.template.example.domain.validator;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import org.springframework.http.HttpStatus;

public class ParameterValidator extends BaseValidator {


    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException {

        if (!areInputParametersValid(checkSubject)) {
            throw new ValidatorException(HttpStatus.BAD_REQUEST);
        }

        if (!isEnumStringValid(checkSubject)) {
            throw new ValidatorException(HttpStatus.BAD_REQUEST);
        }

        if (!isUserIdPositive(checkSubject)) {
            throw new ValidatorException(HttpStatus.BAD_REQUEST);
        }

        if (!isTrackIdPositive(checkSubject)) {
            throw new ValidatorException(HttpStatus.BAD_REQUEST);
        }
        return super.checkNext(checkSubject);
    }

    private boolean isUserIdPositive(CheckSubject checkSubject) {
        Integer userId = checkSubject.getUserId();
        if (userId == null) {
            return true;
        }
        return userId >= 0;
    }

    boolean areInputParametersValid(CheckSubject checkSubject) {
        List<Object> inputParameters = checkSubject.getInputParameters();
        if (inputParameters == null) {
            return true;
        }
        for (Object o : inputParameters) {
            if (o == null) {
                return false;
            }
        }
        return true;
    }

    boolean isEnumStringValid(CheckSubject checkSubject) {
        if (checkSubject.getAcceptedEnumStrings() == null) {
            return true;
        }
        if (checkSubject.getEnumString() == null) {
            return true;
        }

        boolean isEnumStringAmongGoodValues = false;
        for (String goodValue : checkSubject.getAcceptedEnumStrings()) {
            if (goodValue.equals(checkSubject.getEnumString())) {
                isEnumStringAmongGoodValues = true;
                break;
            }
        }
        return isEnumStringAmongGoodValues;
    }

    boolean isTrackIdPositive(CheckSubject checkSubject) {
        Integer trackId = checkSubject.getTrackId();
        if (trackId == null) {
            return true;
        }
        return trackId >= 0;
    }
}
