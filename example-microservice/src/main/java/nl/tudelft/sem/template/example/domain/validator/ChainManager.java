package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ChainManager {
    private TrackPhaseService trackPhaseService;
    private UserService userService;
    private ReviewService reviewService;

    private PaperService paperService;

    public ChainManager(UserService userService, PaperService paperService, ReviewService reviewService, TrackPhaseService trackPhaseService) {
        this.userService = userService;
        this.paperService = paperService;
        this.reviewService = reviewService;
        this.trackPhaseService = trackPhaseService;
    }

    public Validator createChain()
    {
        Validator parameterValidator = new ParameterValidator(trackPhaseService);
        Validator userValidator = new UserValidator(userService);
        parameterValidator.setNext(userValidator);
        DatabaseObjectValidator databaseObjectValidator = new DatabaseObjectValidator(paperService, reviewService);
        userValidator.setNext(databaseObjectValidator);
        return parameterValidator;
    }

    public  <T> ResponseEntity<T> evaluate(CheckSubject checkSubject) {
        Validator firstValidator = createChain();
        try{
            boolean httpStatus = firstValidator.handle(checkSubject);
        } catch (ValidatorException ve) {
            return new ResponseEntity<>(ve.getHttpStatus());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
