package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
}
