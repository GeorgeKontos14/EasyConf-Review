package nl.tudelft.sem.template.example.domain.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReviewControllerTest {
    private UserService userService;
    private ReviewService reviewService;
    private PaperService paperService;
    private ReviewController sut;

    /**
     * Constructor method for reviews.
     * @param id the id of the review.
     * @param paperId the id of the paper.
     * @param reviewerId the id of the reviewer.
     * @return the review object.
     */
    private Review buildReview(int id, int paperId, int reviewerId) {
        Review review = new Review();
        review.setId(id);
        review.setPaperId(paperId);
        review.setReviewerId(reviewerId);
        return review;
    }

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        userService = Mockito.mock(UserService.class);
        reviewService = Mockito.mock(ReviewService.class);
        paperService = Mockito.mock(PaperService.class);
        sut = new ReviewController(userService, reviewService, paperService);
        Mockito.when(userService.validateUser(1)).thenReturn(true);
        Mockito.when(userService.validateUser(2)).thenReturn(false);
    }

    /**
     * Test that returns a bad request for the reviewFindPaperByReviewIdGet endpoint.
     */
    @Test
    public void findPaperByReviewIdBadRequestTest() {
        ResponseEntity<Paper> response = sut.reviewFindPaperByReviewIdGet(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindPaperByReviewIdGet(-1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindPaperByReviewIdGet(1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindPaperByReviewIdGet(1, -1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Test that returns 'unauthorized' for the reviewFindPaperByReviewIdGet endpoint.
     */
    @Test
    public void findPaperByReviewIdUnauthorizedTest() {
        ResponseEntity<Paper> response = sut.reviewFindPaperByReviewIdGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Mockito.verify(userService).validateUser(2);
    }

    /**
     * Test that returns 'not found' or a successful response for the reviewFindPaperByReviewIdGet endpoint.
     */
    @Test
    public void findPaperByReviewIdLookForReviewAndPaperTest() {
        Mockito.when(reviewService.findReviewObjectWithId(1)).thenReturn(
                Optional.of(buildReview(1, 1, 1)));
        Mockito.when(reviewService.findReviewObjectWithId(2)).thenReturn(
                Optional.of(buildReview(1, 2, 2)));
        Mockito.when(reviewService.findReviewObjectWithId(3)).thenReturn(
                Optional.empty());
        Paper paper = new Paper();
        paper.setId(1);
        paper.setAuthors(Arrays.asList(1,2,3));
        paper.setFinalVerdict(Paper.FinalVerdictEnum.ACCEPTED);
        Mockito.when(paperService.getPaperObjectWithId(1)).thenReturn(
                Optional.of(paper));
        Mockito.when(paperService.getPaperObjectWithId(2)).thenReturn(
                Optional.empty());
        ResponseEntity<Paper> response = sut.reviewFindPaperByReviewIdGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(paper);
        response = sut.reviewFindPaperByReviewIdGet(2, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        response = sut.reviewFindPaperByReviewIdGet(3, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test that returns a bad request for the reviewFindAllReviewsByPaperIdGet endpoint.
     */
    @Test
    public void findAllReviewsByPaperIdBadRequestTest() {
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByPaperIdGet(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindAllReviewsByPaperIdGet(-1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindAllReviewsByPaperIdGet(1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindAllReviewsByPaperIdGet(1, -1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Test that returns 'unauthorized' for the reviewFindAllReviewsByPaperIdGet endpoint.
     */
    @Test
    public void findAllReviewsByPaperIdUnauthorizedTest() {
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByPaperIdGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Mockito.verify(userService).validateUser(2);
    }

    /**
     * Test that returns a successful for the reviewFindAllReviewsByPaperIdGet endpoint.
     */
    @Test
    public void findAllReviewsByPaperIdOkTest() {
        List<Review> reviews = Arrays.asList(
                buildReview(1,1,2), buildReview(2,1,3));
        Mockito.when(reviewService.reviewsByPaper(1)).thenReturn(reviews);
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByPaperIdGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(reviews);
    }

    @Test
    void reviewEditConfidenceScorePut() {
        Mockito.when(userService.validateUser(anyInt())).thenReturn(true);
        Mockito.when(reviewService.existsReview(2)).thenReturn(true);
        Review review = buildReview(2,3,4);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        Mockito.when(reviewService.saveAndReturnReview(any())).thenReturn(review);
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(4,review);
        assertThat(receivedReview.getBody()).isEqualTo(review);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void reviewEditConfidenceScore() {
        Mockito.when(userService.validateUser(anyInt())).thenReturn(true);
        Mockito.when(reviewService.existsReview(2)).thenReturn(false);
        Review review = buildReview(2,3,4);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_2);
        Mockito.when(reviewService.saveAndReturnReview(any())).thenReturn(review);
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(4,review);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void reviewEditConfidenceScoreBadReq()
    {
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(4, null);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


}
