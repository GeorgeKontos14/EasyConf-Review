package nl.tudelft.sem.template.example.domain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.template.api.ReviewApi;
import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.example.domain.util.NullChecks;
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

    /**
     * Constructor for reviewController.
     *
     * @param userService to be called on
     * @param reviewService to be called on
     * @param reviewerPreferencesService to be called on
     * @param paperService to be called on
     * @param trackPhaseService to be called on
     */
    public ReviewController(UserService userService, ReviewService reviewService,
                            ReviewerPreferencesService reviewerPreferencesService, PaperService paperService,
                            TrackPhaseService trackPhaseService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.reviewerPreferencesService = reviewerPreferencesService;
        this.paperService = paperService;
        this.trackPhaseService = trackPhaseService;
    }

    @Override
    public ResponseEntity<Void> reviewStartBiddingForTrackGet(Integer trackId) {
        if (NullChecks.nullCheck(trackId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<List<Integer>> papersOpt = trackPhaseService
                .getTrackPapers(trackId, new RestTemplate());
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
        if (NullChecks.nullCheck(userId, trackId, reviews)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint that allows a pcChair to change review Assignments.
     *
     * @param trackId the ID of the track of the papers to be reviewed.
     *                (to be used for verification).
     * @param userId the ID of the user.
     * @param reviews the reviews to be changed
     * @return Successful Response (status code 200)
     *         or invalid input (status code 400)
     *         or reviewer/paper not found (status code 404)
     *         or server error (status code 500)
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
        if (NullChecks.nullCheck(userId, trackId, reviews)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        if (NullChecks.nullCheck(userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        if (NullChecks.nullCheck(paperId, userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        if (NullChecks.nullCheck(reviewId, userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Review> review = reviewService.findReviewObjectWithId(reviewId);
        if (review.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        if (NullChecks.nullCheck(userId, review)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(userId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!reviewService.existsReview(review.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        if (NullChecks.nullCheck(reviewId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isUserValid = userService.validateUser(reviewId);
        if (!isUserValid) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        if (NullChecks.nullCheck(trackId, userId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userService.validateUser(userId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        if (NullChecks.nullCheck(userId, comment)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userService.validateUser(userId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Comment c = reviewService.reviewPostCommentPost(comment);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }


}
