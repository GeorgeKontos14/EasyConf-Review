package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.services.PaperService;
import nl.tudelft.sem.template.example.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class PaperController implements PaperApi {

    private UserService userService;
    private PaperService paperService;


    PaperController(UserService userService, PaperService paperService)
    {
        this.userService = userService;
        this.paperService = paperService;
    }

    @Override
    public ResponseEntity<List<Paper>> paperGetPaperByIDGet(
            @NotNull @Parameter(name = "PaperID", description = "The id for which the paper should be reviewed.", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "PaperID", required = true) Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID", required = true) Integer userID
    )
    {
        if(userID == null || paperID == null || paperID < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        boolean isUserValid = userService.validateUser(userID);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        //TODO maybe a try-catch for internal server error when retrieving from db
        Paper foundPaper = paperService.getPaperWithId(paperID);
        if(foundPaper == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(List.of(foundPaper), HttpStatus.OK);
    }
}
