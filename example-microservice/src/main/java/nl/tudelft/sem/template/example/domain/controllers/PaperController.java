package nl.tudelft.sem.template.example.domain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class PaperController implements PaperApi {

    private UserService userService;
    private PaperService paperService;


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
        if (userId == null || paperId == null || paperId < 0 || userId < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            boolean isUserValid = userService.validateUser(userId);
            if (!isUserValid) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Optional<Paper> foundPaper = paperService.getPaperObjectWithId(paperId);
            if (foundPaper.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(List.of(foundPaper.get()), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * endpoint for getting title and abstract
     * @param paperID The ID of the paper we want to view the title and abstract (required)
     * @param userID The ID of the user, used for authorization (required)
     * @return a ResponseEntity object, which needs to be a Paper with only title and abstract
     */
    public ResponseEntity<List<Paper>> paperGetTitleAndAbstractGet(
            @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to view the title and abstract",
                    required = true, in = ParameterIn.QUERY)
                        @Valid @RequestParam(value = "paperID", required = true) Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY)
                        @Valid @RequestParam(value = "userID", required = true) Integer userID
    ) {
        if (userID == null || paperID == null || paperID < 0 || userID < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            boolean isUserValid = userService.validateUser(userID);
            if (!isUserValid) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Optional<PaperResponse> foundPaper = paperService.getPaperObjectFromSubmissions(paperID, new RestTemplate());
            if (foundPaper.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Paper response = new Paper();

            response.setTitle(foundPaper.get().getTitle());
            response.setAbstract(foundPaper.get().getAbstract());

            return new ResponseEntity<>(List.of(response), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
