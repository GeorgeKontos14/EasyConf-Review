package nl.tudelft.sem.template.example.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class PaperController implements PaperApi {

    private UserService userService;
    private PaperService paperService;
    private ReviewService reviewService;


    PaperController(UserService userService, PaperService paperService, ReviewService reviewService) {
        this.userService = userService;
        this.paperService = paperService;
        this.reviewService = reviewService;
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
    @Override
    public ResponseEntity<String> paperGetTitleAndAbstractGet(
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

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("title", foundPaper.get().getTitle());
            responseMap.put("abstract", foundPaper.get().getAbstract());

            ObjectMapper objectMapper = new ObjectMapper();
            String response = objectMapper.writeValueAsString(responseMap);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> paperPostPreferenceScorePost(
            @NotNull @Parameter(name = "reviewer_id", description = "The id of the reviewer", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviewer_id", required = true) Integer reviewerId,
            @NotNull @Parameter(name = "paper_id", description = "The id of the paper", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paper_id", required = true) Integer paperId,
            @NotNull @Parameter(name = "preference", description = "The preference score", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "preference", required = true) String preference
    ) {
        if(reviewerId == null || paperId == null || preference == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(!Objects.equals(preference, "Can review") || !Objects.equals(preference, "Cannot review")
        || !Objects.equals(preference, "Neutral"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!userService.validateUser(reviewerId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        boolean doesPaperExist = paperService.doesPaperWithIdExist(paperId);
        if(!doesPaperExist)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ReviewerPreferences reviewerPreference = new ReviewerPreferences();

        reviewerPreference.setPaperId(paperId);
        reviewerPreference.setReviewerId(reviewerId);
        reviewerPreference.setReviewerPreference(ReviewerPreferences.ReviewerPreferenceEnum.valueOf(preference));
        ReviewerPreferences saved = reviewService.saveReviewerPreference(reviewerPreference);

        if(saved == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
