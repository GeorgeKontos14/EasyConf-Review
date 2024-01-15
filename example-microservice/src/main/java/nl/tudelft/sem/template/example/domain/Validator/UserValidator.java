package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserValidator extends BaseValidator{
    private UserService userService;

    UserValidator(UserService userService)
    {
        this.userService = userService;
    }
    @Override
    public ResponseEntity<Void> handle(CheckSubject checkSubject) {
        boolean isUserValid = userService.validateUser(checkSubject.getUserId());
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return super.checkNext(checkSubject);
    }
}
