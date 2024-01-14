package nl.tudelft.sem.template.example.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class PaperController implements PaperApi {

    private UserService userService;
    private PaperService paperService;
    private final ReviewerPreferencesService reviewerPreferencesService;
    private final ReviewService reviewService;

    PaperController(UserService userService, PaperService paperService, ReviewerPreferencesService reviewerPreferencesService, ReviewService reviewService) {
        this.userService = userService;
        this.paperService = paperService;
        this.reviewerPreferencesService = reviewerPreferencesService;
        this.reviewService = reviewService;
    }

    /**
     * GET /paper/getPaperReviews : Gets the reviews for a paper
     * For a given paper ID, returns the list of 3 reviews associated to that paper
     *
     * @param paperId the id of the paper (required)
     * @param userID  The ID of the user, used for authorization (required)
     * @return Successful response (status code 200)
     * or Invalid Paper ID (status code 400)
     * or Unauthorized (status code 401)
     * or Not found (status code 404)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<List<Review>> paperGetPaperReviewsGet(Integer paperId, Integer userID) {
        if (paperId == null || paperId < 0 || userID == null || userID < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userService.validateUser(userID)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(reviewService.findAllReviewsByPaperId(paperId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Paper>> paperGetPaperByIDGet(
            @NotNull @Parameter(name = "PaperId", description = "The id for which the paper should be reviewed.",
                    required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "PaperId") Integer paperId,
            @NotNull @Parameter(name = "userId", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "userId") Integer userId
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
            return foundPaper.map(paper -> new ResponseEntity<>(List.of(paper), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /paper/getPaperComments : Get comments for this paper
     * Given the paper ID, return a list of all comments made on this specific paper that are accessible by the given user
     *
     * @param paperID The ID of the paper for which the PC Chair comments are returned (required)
     * @param userID  The ID of the user, used for authorization (required)
     * @return Successful response (status code 200)
     * or Invalid Paper ID (status code 400)
     * or Server Error (status code 500)
     */
    @Override
    public ResponseEntity<List<Comment>> paperGetPaperCommentsGet(
            @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to view the title and abstract",
                    required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "paperID") Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "userID") Integer userID
    ) {
        if (userID == null || paperID == null || paperID < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userService.validateUser(userID)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Comment> comments = paperService.paperGetPaperCommentsGet(paperID);
        if (comments.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(comments, HttpStatus.OK);
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
            @Valid @RequestParam(value = "paperID") Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "userID") Integer userID
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

    /**
     * GET /paper/getAllPapersForID : Gets all papers that are to be reviewed by a specific reviewer
     * Get all the papers for which the review is assigned to a reviewer with a given reviewer id
     *
     * @param reviewerId the id of the reviewer for which the assigned papers should be returned (required)
     * @return Successful response (status code 200)
     * or Invalid Reviewer ID (status code 400)
     * or Unauthorized (status code 401)
     * or Not found (status code 404)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<List<Paper>> paperGetAllPapersForIDGet(@NotNull @Parameter(name = "paperID", description =
            "The ID of the paper we want to see the reviewer preferences for", required = true,
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer reviewerId) {
        if (reviewerId == null || reviewerId < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!userService.validateUser(reviewerId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Paper> papers = paperService.findAllPapersForIdList(reviewService.findAllPapersByReviewerId(reviewerId));
        if (papers == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (papers.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReviewerPreferences>> paperGetPreferencesByPaperGet(
            @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to see the reviewer preferences for", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userID
    ) {
        if (userID == null || userID < 0 || paperID == null || paperID < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userID);
        if (!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        List<ReviewerPreferences> prefs = reviewerPreferencesService.getPreferencesForPaper(paperID);
        return new ResponseEntity<>(prefs, HttpStatus.ACCEPTED);
    }

    /**
     * PUT /paper/updatePaperStatus : Updates a paper with provided ID with the provided new status
     * Update the &#39;status&#39; field of the paper with provided id with a new status
     *
     * @param paperID The ID of the paper we want to change the status for (required)
     * @param status  The new status of the paper. Can be &#39;Unresolved&#39;, &#39;Accepted&#39; or &#39;Rejected&#39; (required)
     * @param userID  The ID of the user, used for authorization (required)
     * @return Successful update (status code 200)
     * or paperID not found (status code 400)
     * or status not provided (status code 401)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<Void> paperUpdatePaperStatusPut(
            @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to change the status for",
                    required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperID,
            @Parameter(name = "status", description = "The new status of the paper. Can be 'Unresolved', 'Accepted' " +
                    "or 'Rejected'", required = true) String status,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
                    required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userID
    ) {
        if (paperID == null || userID == null || paperID < 0 || userID < 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (status == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!paperService.isExistingPaper(paperID)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Paper.FinalVerdictEnum verdict;
        if (status.equals("Unresolved")) {
            verdict = null;
        } else if (status.equals("Accepted") || status.equals("Rejected")) {
            verdict = Paper.FinalVerdictEnum.fromValue(status);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean success = paperService.paperUpdatePaperStatusPut(paperID, verdict);
        if (!success)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * endpoint that saves the provided preference a reviewer has over a paper
     * @param reviewerId The id of the reviewer (required)
     * @param paperId The id of the paper (required)
     * @param preference The preference score (required)
     * @return
     * BAD_REQUEST if input data is wrong
     * NOT_FOUND if there is no user/paper with given ids
     * INTERNAL_SERVER_ERROR if something went wrong
     * OK if successful
     */

    public ResponseEntity<Void> paperPostPreferenceScorePost(
            @NotNull @Parameter(name = "reviewer_id", description = "The id of the reviewer", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviewer_id", required = true) Integer reviewerId,
            @NotNull @Parameter(name = "paper_id", description = "The id of the paper", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paper_id", required = true) Integer paperId,
            @NotNull @Parameter(name = "preference", description = "The preference score", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "preference", required = true) String preference
    ) {
        if(reviewerId == null || paperId == null || preference == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(!Objects.equals(preference, "CAN_REVIEW") && !Objects.equals(preference, "CANNOT_REVIEW")
        && !Objects.equals(preference, "NEUTRAL"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!userService.validateUser(reviewerId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        boolean doesPaperExist = paperService.isExistingPaper(paperId);
        if(!doesPaperExist)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        PreferenceEntity preferenceEntity = new PreferenceEntity(
                reviewerId, paperId, ReviewerPreferences.ReviewerPreferenceEnum.valueOf(preference)
        );
        PreferenceEntity saved = reviewerPreferencesService.saveReviewerPreference(preferenceEntity);

        if(saved == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
