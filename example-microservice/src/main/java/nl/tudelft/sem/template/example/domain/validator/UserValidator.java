package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.springframework.http.HttpStatus;

public class UserValidator extends BaseValidator{
    private final UserService userService;

    UserValidator(UserService userService)
    {
        this.userService = userService;
    }
    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException{
        boolean isUserValid = userService.validateUser(checkSubject.getUserId());
        if(!isUserValid)
            throw new ValidatorException(HttpStatus.UNAUTHORIZED);
        return super.checkNext(checkSubject);
    }
}
