package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ChainManager {

    private UserService userService;

    private PaperService paperService;

    public ChainManager(UserService userService, PaperService paperService) {
        this.userService = userService;
        this.paperService = paperService;
    }

    public Validator createChain()
    {
        Validator parameterValidator = new ParameterValidator();
        Validator userValidator = new UserValidator(userService);
        parameterValidator.setNext(userValidator);
        DatabaseObjectValidator databaseObjectValidator = new DatabaseObjectValidator(paperService);
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
