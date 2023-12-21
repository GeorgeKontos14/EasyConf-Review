package nl.tudelft.sem.template.example.domain.services;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * Function that authenticates an user with given id.
     *
     * @param userId - the user id this function should validate
     * @return true if this user is validated, false otherwise
     */
    public boolean validateUser(int userId) {
        //TODO

        return true;
    }
}
