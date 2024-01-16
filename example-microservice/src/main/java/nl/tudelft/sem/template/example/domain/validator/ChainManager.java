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
     * @param userService       - reference to userService
     * @param paperService      - reference to paperService
     * @param reviewService     - reference to reviewService
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
     * Method that creates a chain of validators, depending on the data in checkSubject.
     *
     * @param checkSubject - the object containing data to be checked
     * @return reference to the first Validator in chain
     */
    public Validator createChain(CheckSubject checkSubject) {

        BaseValidator head = new SuccessfulState();
        BaseValidator tail = head;
        if (shouldAddParameterValidator(checkSubject)) {
            tail = (BaseValidator) tail.setNext(new ParameterValidator());
        }
        if (shouldAddUserValidator(checkSubject)) {
            tail = (BaseValidator) tail.setNext(new UserValidator(userService));
        }
        if (shouldAddDatabaseObjectValidator(checkSubject)) {
            tail = (BaseValidator) tail.setNext(new DatabaseObjectValidator(paperService, reviewService));
        }
        tail.setNext(new SuccessfulState());
        return head.getNext();
    }

    boolean shouldAddParameterValidator(CheckSubject checkSubject) {
        if (checkSubject.getInputParameters() != null) {
            return true;
        }
        if (checkSubject.getUserId() != null) {
            return true;
        }
        if (checkSubject.getTrackId() != null) {
            return true;
        }
        if (checkSubject.getAcceptedEnumStrings() != null) {
            return true;
        }
        if (checkSubject.getEnumString() != null) {
            return true;
        }
        return false;
    }

    boolean shouldAddUserValidator(CheckSubject checkSubject) {
        return checkSubject.getUserId() != null;
    }

    boolean shouldAddDatabaseObjectValidator(CheckSubject checkSubject) {
        if (checkSubject.getReviewIds() != null) {
            return true;
        }
        if (checkSubject.getPaperIds() != null) {
            return true;
        }
        return false;
    }


    /**
     * Method that evaluates a checkSubject object through the chain.
     *
     * @param checkSubject - the object that needs to be tested through the chain
     * @param <T>          The type of the object this ResponseEntity returns
     * @return null if passed the Chain, or ResponseEntity with error in case it's fine
     */

    public <T> ResponseEntity<T> evaluate(CheckSubject checkSubject) {
        Validator firstValidator = createChain(checkSubject);
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
