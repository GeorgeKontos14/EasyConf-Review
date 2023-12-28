package nl.tudelft.sem.template.example.domain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.tudelft.sem.template.api.ReviewApi;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
        if (userId == null || trackID == null || reviews.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        boolean isUserValid = userService.validateUser(userId);
        if(!isUserValid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        reviewService.saveReviews(reviews);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
