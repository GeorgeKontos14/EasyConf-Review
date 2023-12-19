package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.services.PaperService;
import nl.tudelft.sem.template.example.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PaperController implements PaperApi {

    private transient UserService userService;
    private transient PaperService paperService;


    PaperController(UserService userService, PaperService paperService) {
        this.userService = userService;
        this.paperService = paperService;
    }

    @Override
    public ResponseEntity<List<Paper>> paperGetPaperByIDGet(
            @NotNull @Parameter(name = "PaperId", description = "The id for which the paper should be reviewed.",
                    required = true, in = ParameterIn.QUERY)
                        @Valid @RequestParam(value = "PaperId", required = true) Integer paperId,
            @NotNull @Parameter(name = "userId", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY)
                        @Valid @RequestParam(value = "userId", required = true) Integer userId
    ) {
        if (userId == null || paperId == null || paperId < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //TODO maybe a try-catch for internal server error when retrieving from db
        Paper foundPaper = paperService.getPaperWithId(paperId);
        if (foundPaper == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(List.of(foundPaper), HttpStatus.OK);
    }
}
