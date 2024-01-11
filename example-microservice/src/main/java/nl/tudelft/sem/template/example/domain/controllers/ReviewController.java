package nl.tudelft.sem.template.example.domain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.tudelft.sem.template.api.ReviewApi;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class ReviewController implements ReviewApi {

    private final UserService userService;
    private final ReviewService reviewService;

    public ReviewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @Override
    public ResponseEntity<Void> reviewAssignPapersPost(
            @Parameter(name = "trackID", description = "The id of the track", in = ParameterIn.QUERY) @Valid @RequestParam(value = "trackID", required = false) Integer trackID,
            @Parameter(name = "userId", description = "The user ID, used for verification", in = ParameterIn.QUERY) @Valid @RequestParam(value = "userId", required = false) Integer userId,
            @Parameter(name = "reviews", description = "The review objects with papers assigned to reviewers", in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviews", required = false) List<@Valid Review> reviews
    ) {
        if (nullCheck(userId, trackID, reviews))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userId);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint that allows a pcChair to change review Assignments
     * @param trackID the ID of the track of the papers to be reviewed.
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
            @Parameter(name = "trackID", description = "The id of the track", in = ParameterIn.QUERY) @Valid @RequestParam(value = "trackID", required = false) Integer trackID,
            @Parameter(name = "userId", description = "The user ID, used for verification", in = ParameterIn.QUERY) @Valid @RequestParam(value = "userId", required = false) Integer userId,
            @Parameter(name = "reviews", description = "The review objects with papers assigned to reviewers", in = ParameterIn.QUERY) @Valid @RequestParam(value = "reviews", required = false) List<@Valid Review> reviews) {
        if (nullCheck(userId, trackID, reviews))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userId);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (!reviewService.verifyPcChair(userId, trackID))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<Review>> reviewFindAllReviewsByUserIDGet(
            @NotNull @Parameter(name = "userID", description = "The ID of the user the reviews of whom are returned.", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userID
    ) {
        if (userID == null || userID < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userID);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        List<Review> reviews = reviewService.reviewsByReviewer(userID);
        return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<List<Review>> reviewFindAllReviewsByPaperIdGet(
            @NotNull @Parameter(name = "paperID", description = "The ID of the paper the reviews of which are returned", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "paperID") Integer paperID,
            @NotNull @Parameter(name = "userID", description = "The ID of the user, used for authorization", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userID") Integer userID
    ) {
        if (userID == null || userID < 0 || paperID == null || paperID < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userID);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        List<Review> reviews = reviewService.reviewsByPaper(paperID);
        return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
    }

    /**
     * Null check for the start of each method
     * @param userId the user ID
     * @param trackID the track ID
     * @param reviews the reviews in question
     * @return true if-f nothing is null/empty
     */
    private boolean nullCheck(Integer userId, Integer trackID, List<Review> reviews) {
        return userId == null || trackID == null || reviews.isEmpty();
    }


}
