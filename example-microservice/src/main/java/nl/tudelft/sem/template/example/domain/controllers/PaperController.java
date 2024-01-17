package nl.tudelft.sem.template.example.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.PaperApi;
import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.example.domain.validator.ChainManager;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class PaperController implements PaperApi {

    private final UserService userService;
    private final PaperService paperService;
    private final ReviewerPreferencesService reviewerPreferencesService;
    private final ReviewService reviewService;

    private final ChainManager chainManager;

    PaperController(UserService userService, PaperService paperService,
                    ReviewerPreferencesService reviewerPreferencesService,
                    ReviewService reviewService, ChainManager chainManager) {
        this.userService = userService;
        this.paperService = paperService;
        this.reviewerPreferencesService = reviewerPreferencesService;
        this.reviewService = reviewService;
        this.chainManager = chainManager;
    }

    /**
     * GET /paper/getPaperReviews : Gets the reviews for a paper.
     * For a given paper ID, returns the list of 3 reviews associated to that paper
     *
     * @param paperId the id of the paper (required)
     * @param userId  The ID of the user, used for authorization (required)
     * @return Successful response (status code 200)
     * or Invalid Paper ID (status code 400)
     * or Unauthorized (status code 401)
     * or Not found (status code 404)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<List<Review>> paperGetPaperReviewsGet(Integer paperId, Integer userId) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));
        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<Review>> validatorStatus = chainManager.evaluate(checkSubject);
        if (validatorStatus != null) {
            return validatorStatus;
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

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));

        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<Paper>> validatorStatus = chainManager.evaluate(checkSubject);
        if (validatorStatus != null) {
            return validatorStatus;
        }

        Optional<Paper> foundPaper = paperService.getPaperObjectWithId(paperId);
        if (foundPaper.isEmpty()) {
            throw new RuntimeException("foundPaper should not be empty at this point due to Validator chain");
        }
        return new ResponseEntity<>(List.of(foundPaper.get()), HttpStatus.OK);
    }

    /**
     * GET /paper/getPaperComments : Get comments for this paper.
     * Given the paper ID, return a list of all comments made on this specific paper that are accessible by the given user
     *
     * @param paperId The ID of the paper for which the PC Chair comments are returned (required)
     * @param userId  The ID of the user, used for authorization (required)
     * @return Successful response (status code 200)
     * or Invalid Paper ID (status code 400)
     * or Server Error (status code 500)
     */
    @Override
    public ResponseEntity<List<Comment>> paperGetPaperCommentsGet(
        @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to view the title and abstract",
            required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "paperID") Integer paperId,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "userID") Integer userId
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));
        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<Comment>> validatorStatus = chainManager.evaluate(checkSubject);

        if (validatorStatus != null) {
            return validatorStatus;
        }

        List<Comment> comments = paperService.paperGetPaperCommentsGet(paperId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * endpoint for getting title and abstract.
     *
     * @param paperId The ID of the paper we want to view the title and abstract (required)
     * @param userId  The ID of the user, used for authorization (required)
     * @return a ResponseEntity object, which needs to be a Paper with only title and abstract
     */
    @Override
    public ResponseEntity<String> paperGetTitleAndAbstractGet(
        @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to view the title and abstract",
            required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "paperID") Integer paperId,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "userID") Integer userId
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));

        CheckSubject checkSubject = builder.build();
        ResponseEntity<String> validatorStatus = chainManager.evaluate(checkSubject);

        if (validatorStatus != null) {
            return validatorStatus;
        }


        Optional<PaperResponse> foundPaper = paperService.getPaperObjectFromSubmissions(paperId, new RestTemplate());
        if (foundPaper.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("title", foundPaper.get().getTitle());
        responseMap.put("abstract", foundPaper.get().getAbstract());

        ObjectMapper objectMapper = new ObjectMapper();
        String response = null;
        try {
            response = objectMapper.writeValueAsString(responseMap);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * GET /paper/getAllPapersForID : Gets all papers that are to be reviewed by a specific reviewer.
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

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Collections.singletonList(reviewerId)));
        builder.setUserId(reviewerId);
        if (reviewerId != null) {
            builder.setPaperIds(reviewService.findAllPapersByReviewerId(reviewerId));
        }

        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<Paper>> validatorStatus = chainManager.evaluate(checkSubject);
        if (validatorStatus != null) {
            return validatorStatus;
        }

        List<Paper> papers = paperService.findAllPapersForIdList(reviewService.findAllPapersByReviewerId(reviewerId));
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReviewerPreferences>> paperGetPreferencesByPaperGet(
        @NotNull @Parameter(name = "paperID", description =
            "The ID of the paper we want to see the reviewer preferences for",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperId,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId
    ) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));

        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<ReviewerPreferences>> validatorStatus = chainManager.evaluate(checkSubject);
        if (validatorStatus != null) {
            return validatorStatus;
        }

        List<ReviewerPreferences> prefs = reviewerPreferencesService.getPreferencesForPaper(paperId);
        return new ResponseEntity<>(prefs, HttpStatus.ACCEPTED);
    }

    /**
     * PUT /paper/updatePaperStatus : Updates a paper with provided ID with the provided new status.
     * Update the &#39;status&#39; field of the paper with provided id with a new status
     *
     * @param paperId The ID of the paper we want to change the status for (required)
     * @param status  The new status of the paper. Can be &#39;Unresolved&#39;, &#39;Accepted&#39;
     *                or &#39;Rejected&#39; (required)
     * @param userId  The ID of the user, used for authorization (required)
     * @return Successful update (status code 200)
     * or paperId not found (status code 400)
     * or status not provided (status code 401)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<Void> paperUpdatePaperStatusPut(
        @NotNull @Parameter(name = "paperID", description = "The ID of the paper we want to change the status for",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperId,
        @Parameter(name = "status", description = "The new status of the paper. Can be 'Unresolved', 'Accepted' "
            + "or 'Rejected'", required = true) String status,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId, status)));
        builder.setUserId(userId);
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));
        builder.setEnumValue(status);
        builder.setGoodEnumValues(List.of("Unresolved", "Accepted", "Rejected"));

        CheckSubject checkSubject = builder.build();

        ResponseEntity<Void> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }

        boolean success = paperService.paperUpdatePaperStatusPut(paperId, status);
        if (!success) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * endpoint that saves the provided preference a reviewer has over a paper.
     *
     * @param reviewerId The id of the reviewer (required)
     * @param paperId    The id of the paper (required)
     * @param preference The preference score (required)
     * @return BAD_REQUEST if input data is wrong
     * NOT_FOUND if there is no user/paper with given ids
     * INTERNAL_SERVER_ERROR if something went wrong
     * OK if successful
     */

    public ResponseEntity<Void> paperPostPreferenceScorePost(
        @NotNull
        @Parameter(name = "reviewer_id", description = "The id of the reviewer", required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "reviewer_id", required = true) Integer reviewerId,
        @NotNull @Parameter(name = "paper_id", description = "The id of the paper", required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "paper_id", required = true) Integer paperId,
        @NotNull
        @Parameter(name = "preference", description = "The preference score", required = true, in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "preference", required = true) String preference
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(reviewerId, paperId, preference)));
        builder.setUserId(reviewerId);
        builder.setEnumValue(preference);
        builder.setGoodEnumValues(List.of("Can review", "Cannot review", "Neutral"));
        builder.setPaperIds(new ArrayList<>(Collections.singletonList(paperId)));

        CheckSubject checkSubject = builder.build();
        ResponseEntity<Void> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }

        PreferenceEntity preferenceEntity = new PreferenceEntity(
            reviewerId, paperId, PreferenceEntity.changeStringToEnumValue(preference)
        );
        PreferenceEntity saved = reviewerPreferencesService.saveReviewerPreference(preferenceEntity);

        if (saved == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
