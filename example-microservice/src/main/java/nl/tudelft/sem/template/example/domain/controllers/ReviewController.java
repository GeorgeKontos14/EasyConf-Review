package nl.tudelft.sem.template.example.domain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.ReviewApi;
import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.example.domain.validator.ChainManager;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class ReviewController implements ReviewApi {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ReviewerPreferencesService reviewerPreferencesService;
    private final PaperService paperService;
    private final TrackPhaseService trackPhaseService;

    private final ChainManager chainManager;

    /**
     * Constructor for reviewController.
     *
     * @param userService                to be called on
     * @param reviewService              to be called on
     * @param reviewerPreferencesService to be called on
     * @param paperService               to be called on
     * @param trackPhaseService          to be called on
     * @param chainManager               chain manager instance
     */
    public ReviewController(UserService userService, ReviewService reviewService,
                            ReviewerPreferencesService reviewerPreferencesService, PaperService paperService,
                            TrackPhaseService trackPhaseService, ChainManager chainManager) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.reviewerPreferencesService = reviewerPreferencesService;
        this.paperService = paperService;
        this.trackPhaseService = trackPhaseService;
        this.chainManager = chainManager;
    }

    @Override
    public ResponseEntity<Void> reviewStartBiddingForTrackGet(Integer trackId) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Collections.singletonList(trackId)));
        builder.setTrack(trackId);
        CheckSubject checkSubject = builder.build();

        ResponseEntity<Void> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }

        Optional<List<Integer>> papersOpt = trackPhaseService
            .getTrackPapers(trackId);
        if (papersOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TrackPhase phase = new TrackPhase(papersOpt.get());
        trackPhaseService.saveTrackPhase(phase);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @Override
    public ResponseEntity<Void> reviewAssignPapersPost(
        @Parameter(name = "trackID", description = "The id of the track", in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "trackID", required = false) Integer trackId,
        @Parameter(name = "userId", description = "The user ID, used for verification", in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "userId", required = false) Integer userId,
        @Parameter(name = "reviews", description = "The review objects with papers assigned to reviewers",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviews",
            required = false) List<@Valid Review> reviews
    ) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(trackId, userId, reviews)));
        builder.setUserId(userId);
        builder.setTrack(trackId);
        CheckSubject checkSubject = builder.build();

        ResponseEntity<Void> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint that allows a pcChair to change review Assignments.
     *
     * @param trackId the ID of the track of the papers to be reviewed.
     *                (to be used for verification).
     * @param userId  the ID of the user.
     * @param reviews the reviews to be changed
     * @return Successful Response (status code 200)
     * or invalid input (status code 400)
     * or reviewer/paper not found (status code 404)
     * or server error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/review/changeAssignments"
    )
    public ResponseEntity<Void> changeReviews(
        @Parameter(name = "trackID", description = "The id of the track", in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "trackID", required = false) Integer trackId,
        @Parameter(name = "userId", description = "The user ID, used for verification", in = ParameterIn.QUERY)
        @Valid @RequestParam(value = "userId", required = false) Integer userId,
        @Parameter(name = "reviews", description = "The review objects with papers assigned to reviewers",
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviews",
            required = false) List<@Valid Review> reviews) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(trackId, userId, reviews)));
        builder.setUserId(userId);
        builder.setTrack(trackId);
        CheckSubject checkSubject = builder.build();

        ResponseEntity<Void> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }

        if (!reviewService.verifyPcChair(userId, trackId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<Review>> reviewFindAllReviewsByUserIDGet(
        @NotNull @Parameter(name = "userID", description = "The ID of the user the reviews of whom are returned.",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId
    ) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Collections.singletonList(userId)));
        builder.setUserId(userId);
        CheckSubject checkSubject = builder.build();

        ResponseEntity<List<Review>> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }
        List<Review> reviews = reviewService.reviewsByReviewer(userId);
        return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<Review>> reviewFindAllReviewsByPaperIdGet(
        @NotNull @Parameter(name = "paperID", description = "The ID of the paper the reviews of which are returned",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperId,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId
    ) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(paperId, userId)));
        builder.setUserId(userId);
        CheckSubject checkSubject = builder.build();

        ResponseEntity<List<Review>> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }
        List<Review> reviews = reviewService.reviewsByPaper(paperId);
        return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<Paper> reviewFindPaperByReviewIdGet(
        @NotNull @Parameter(name = "reviewID", description = "The ID of the review for which the paper should "
            + "be returned", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value =
            "reviewID") Integer reviewId,
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(reviewId, userId)));
        builder.setUserId(userId);
        builder.setReviewIds(new ArrayList<>(Collections.singletonList(reviewId)));
        CheckSubject checkSubject = builder.build();

        ResponseEntity<Paper> responseStatus = chainManager.evaluate(checkSubject);

        if (responseStatus != null) {
            return responseStatus;
        }

        Optional<Review> review = reviewService.findReviewObjectWithId(reviewId);
        if (review.isEmpty()) {
            throw new RuntimeException("Review object should not be empty due to checks made in chain.");
        }
        int paperId = review.get().getPaperId();
        Optional<Paper> paper = paperService.getPaperObjectWithId(paperId);
        return paper.map(value -> new ResponseEntity<>(value, HttpStatus.ACCEPTED))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Review> reviewEditConfidenceScorePut(
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId,
        @Parameter(name = "Review", description = "the review to be updated", required = true)
        @Valid @RequestBody Review review
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(userId, review)));
        builder.setUserId(userId);
        if (review != null) {
            builder.setReviewIds(new ArrayList<>(Arrays.asList(review.getId())));
        }

        CheckSubject checkSubject = builder.build();
        ResponseEntity<Review> responseStatus = chainManager.evaluate(checkSubject);
        if (responseStatus != null) {
            return responseStatus;
        }

        Review updatedReview = reviewService.saveAndReturnReview(review);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReviewerPreferences>> reviewFindAllPreferencesByUserIdGet(
        @NotNull @Parameter(name = "reviewerID", description =
            "The ID of the reviewer the reviews of whom are returned.", required = true,
            in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviewerID") Integer reviewId
    ) {

        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Collections.singletonList(reviewId)));
        builder.setUserId(reviewId);
        CheckSubject checkSubject = builder.build();
        ResponseEntity<List<ReviewerPreferences>> responseStatus = chainManager.evaluate(checkSubject);
        if (responseStatus != null) {
            return responseStatus;
        }

        List<ReviewerPreferences> preferences = reviewerPreferencesService.getPreferencesForReviewer(reviewId);
        return new ResponseEntity<>(preferences, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<Review> reviewEditOverallScorePut(
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userId,
        @Parameter(name = "Review", description = "the review to be updated", required = true)
        @Valid @RequestBody Review review) {
        return reviewEditConfidenceScorePut(userId, review);
    }

    @Override
    public ResponseEntity<String> reviewGetBiddingDeadlineGet(Integer trackId, Integer userId) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(new ArrayList<>(Arrays.asList(trackId, userId)));
        builder.setUserId(userId);
        builder.setTrack(trackId);

        CheckSubject checkSubject = builder.build();
        ResponseEntity<String> responseStatus = chainManager.evaluate(checkSubject);
        if (responseStatus != null) {
            return responseStatus;
        }
        Optional<String> deadline = reviewService.getTrackDeadline(trackId, new RestTemplate());
        return deadline.map(s -> new ResponseEntity<>(s, HttpStatus.ACCEPTED))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST /review/postComment : Post a comment on a review post a comment on a review.
     *
     * @param userId  The ID of the user, used for authorization (required)
     * @param comment The comment to post (required)
     * @return Successful operation (status code 200)
     * or Bad request (status code 400)
     * or Unauthorized (status code 401)
     * or Server error (status code 500)
     */
    @Override
    public ResponseEntity<Comment> reviewPostCommentPost(
        @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization",
            required = true) Integer userId,
        @Parameter(name = "Comment", description = "the comment to post", required = true) @RequestBody
        Comment comment) {
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        builder.setInputParameters(Arrays.asList(userId, comment));
        builder.setUserId(userId);

        CheckSubject checkSubject = builder.build();
        ResponseEntity<Comment> responseStatus = chainManager.evaluate(checkSubject);
        if (responseStatus != null) {
            return responseStatus;
        }
        Comment c = reviewService.reviewPostCommentPost(comment);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }


}
