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

    /**
     * ChainManager constructor.
     *
     * @param userService - reference to userService
     * @param paperService - reference to paperService
     * @param reviewService - reference to reviewService
     * @param trackPhaseService - reference to a trackPhaseService
     */
    public ChainManager(UserService userService, PaperService paperService, ReviewService reviewService,
                        TrackPhaseService trackPhaseService) {
        this.userService = userService;
        this.paperService = paperService;
        this.reviewService = reviewService;
        this.trackPhaseService = trackPhaseService;
    }

    /**
     * Method that creates a chain of ParameterValidator -> UserValidator -> DatabaseObjectValidator.
     *
     * @return reference to the first Validator in chain
     */
    public Validator createChain() {
        Validator parameterValidator = new ParameterValidator();
        Validator userValidator = new UserValidator(userService);
        parameterValidator.setNext(userValidator);
        DatabaseObjectValidator databaseObjectValidator = new DatabaseObjectValidator(paperService, reviewService);
        userValidator.setNext(databaseObjectValidator);
        return parameterValidator;
    }

    /**
     * Method that evaluates a checkSubject object through the chain.
     *
     * @param checkSubject - the object that needs to be tested through the chain
     * @param <T> The type of the object this ResponseEntity returns
     * @return null if passed the Chain, or ResponseEntity with error in case it's fine
     *
     */

    public <T> ResponseEntity<T> evaluate(CheckSubject checkSubject) {
        Validator firstValidator = createChain();
        try {
            boolean httpStatus = firstValidator.handle(checkSubject);
        } catch (ValidatorException ve) {
            return new ResponseEntity<>(ve.getHttpStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
