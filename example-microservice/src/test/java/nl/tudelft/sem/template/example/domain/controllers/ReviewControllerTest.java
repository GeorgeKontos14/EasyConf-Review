package nl.tudelft.sem.template.example.domain.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.*;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ReviewControllerTest {
    private UserService userService;
    private ReviewService reviewService;
    private PaperService paperService;
    private ReviewerPreferencesService reviewerPreferencesService;
    private TrackPhaseService trackPhaseService;
    private ChainManager chainManager;
    private ReviewController sut;

    /**
     * Constructor method for reviews.
     *
     * @param id         the id of the review.
     * @param paperId    the id of the paper.
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

    private Paper buildPaper(int id, List<Integer> authors, Paper.FinalVerdictEnum finalVerdictEnum) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setAuthors(authors);
        paper.setFinalVerdict(finalVerdictEnum);
        return paper;
    }

    /**
     * Constructor method for reviewer Preferences.
     *
     * @param reviewerId     the id of the reviewer.
     * @param paperId        the id of the paper.
     * @param preferenceEnum the preference.
     * @return the reviewer preferences object.
     */
    private ReviewerPreferences buildReviewPreferences(
            int reviewerId, int paperId, ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum) {
        ReviewerPreferences pref = new ReviewerPreferences();
        pref.setReviewerId(reviewerId);
        pref.setPaperId(paperId);
        pref.setReviewerPreference(preferenceEnum);
        return pref;
    }

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        userService = Mockito.mock(UserService.class);
        reviewService = Mockito.mock(ReviewService.class);
        paperService = Mockito.mock(PaperService.class);
        reviewerPreferencesService = Mockito.mock(ReviewerPreferencesService.class);
        trackPhaseService = Mockito.mock(TrackPhaseService.class);
        chainManager = new ChainManager(userService, paperService, reviewService, trackPhaseService);
        sut = new ReviewController(userService, reviewService, reviewerPreferencesService,
                paperService, trackPhaseService, chainManager);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
        paper.setAuthors(Arrays.asList(1, 2, 3));
        paper.setFinalVerdict(Paper.FinalVerdictEnum.ACCEPTED);
        Mockito.when(paperService.getPaperObjectWithId(1)).thenReturn(
                Optional.of(paper));
        Mockito.when(paperService.getPaperObjectWithId(2)).thenReturn(
                Optional.empty());
        Mockito.when(reviewService.existsReview(1)).thenReturn(true);
        Mockito.when(userService.validateUser(1)).thenReturn(true);
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
                buildReview(1, 1, 2), buildReview(2, 1, 3));
        Mockito.when(reviewService.reviewsByPaper(1)).thenReturn(reviews);
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByPaperIdGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(reviews);
    }

    @Test
    public void assignPapersBadRequestTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        ResponseEntity<Void> response = sut
                .reviewAssignPapersPost(null, 1, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut
                .reviewAssignPapersPost(1, null, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut
                .reviewAssignPapersPost(-1, 1, new ArrayList<>());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void assignPapersUnauthorizedTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        ResponseEntity<Void> response =
                sut.reviewAssignPapersPost(1, 2, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void assignPapersOkTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        ResponseEntity<Void> response = sut
                .reviewAssignPapersPost(1, 1, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        Mockito.verify(reviewService).saveReviews(Arrays.asList(r1, r2));
    }

    @Test
    public void changeReviewsBadRequestTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        Mockito.when(userService.validateUser(1)).thenReturn(true);
        ResponseEntity<Void> response = sut
                .changeReviews(null, 1, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut
                .changeReviews(1, null, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut
                .changeReviews(-1, 1, new ArrayList<>());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void changeReviewsUnauthorizedTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        Mockito.when(reviewService.verifyPcChair(3, 1)).thenReturn(false);
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        ResponseEntity<Void> response =
                sut.changeReviews(1, 2, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        response =
                sut.changeReviews(1, 3, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void changeReviewsOkTest() {
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 2);
        Mockito.when(reviewService.verifyPcChair(1, 1)).thenReturn(true);
        ResponseEntity<Void> response = sut
                .changeReviews(1, 1, Arrays.asList(r1, r2));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        Mockito.verify(reviewService).saveReviews(Arrays.asList(r1, r2));
    }

    @Test
    public void findAllReviewsByUserIdBadRequestTest() {
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByUserIDGet(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindAllReviewsByUserIDGet(-1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void findAllReviewsByUserIdUnauthorizedTest() {
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByUserIDGet(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Mockito.verify(userService).validateUser(2);
    }

    @Test
    public void findAllReviewsByUserIdOkTest() {
        List<Review> reviews = Arrays.asList(
                buildReview(1, 1, 2), buildReview(2, 1, 3));
        Mockito.when(reviewService.reviewsByReviewer(1)).thenReturn(reviews);
        ResponseEntity<List<Review>> response = sut.reviewFindAllReviewsByUserIDGet(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(reviews);
    }


    @Test
    void reviewEditConfidenceScorePut() {
        Mockito.when(reviewService.existsReview(2)).thenReturn(true);
        Review review = buildReview(2, 3, 4);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        Mockito.when(reviewService.saveAndReturnReview(any())).thenReturn(review);
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(1, review);
        assertThat(receivedReview.getBody()).isEqualTo(review);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void editConfidenceScoreUnauthorizedTest() {
        Review review = buildReview(2, 3, 4);
        ResponseEntity<Review> response = sut.reviewEditConfidenceScorePut(2, review);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void reviewEditConfidenceScore() {
        Mockito.when(userService.validateUser(anyInt())).thenReturn(true);
        Mockito.when(reviewService.existsReview(2)).thenReturn(false);
        Review review = buildReview(2, 3, 4);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_2);
        Mockito.when(reviewService.saveAndReturnReview(any())).thenReturn(review);
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(4, review);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void reviewEditConfidenceScoreBadReq() {
        ResponseEntity<Review> receivedReview = sut.reviewEditConfidenceScorePut(4, null);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void editOverallScoreTest() {
        Mockito.when(reviewService.existsReview(2)).thenReturn(true);
        Review review = buildReview(2, 3, 4);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        Mockito.when(reviewService.saveAndReturnReview(any())).thenReturn(review);
        ResponseEntity<Review> receivedReview = sut.reviewEditOverallScorePut(1, review);
        assertThat(receivedReview.getBody()).isEqualTo(review);
        assertThat(receivedReview.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postReviewInvalidTest() {
        ResponseEntity<Comment> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(sut.reviewPostCommentPost(null, new Comment())).isEqualTo(response);
        assertThat(sut.reviewPostCommentPost(1, null)).isEqualTo(response);
        assertThat(sut.reviewPostCommentPost(2, new Comment()))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void postReviewTest() {
        Comment c = new Comment();
        Mockito.when(reviewService.reviewPostCommentPost(c)).thenReturn(c);
        assertThat(sut.reviewPostCommentPost(1, c)).isEqualTo(new ResponseEntity<>(c, HttpStatus.OK));
    }


    @Test
    public void findAllPreferencesByUserIdBadRequest() {
        ResponseEntity<List<ReviewerPreferences>> response = sut
                .reviewFindAllPreferencesByUserIdGet(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewFindAllPreferencesByUserIdGet(-3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void findAllPreferencesByUserIdUnauthorizedTest() {
        Mockito.when(userService.validateUser(2)).thenReturn(false);
        ResponseEntity<List<ReviewerPreferences>> response = sut
                .reviewFindAllPreferencesByUserIdGet(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void findAllPreferencesByUserIdOkTest() {
        ReviewerPreferences pref1 = buildReviewPreferences(1, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        ReviewerPreferences pref2 = buildReviewPreferences(2, 4,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
        Mockito.when(reviewerPreferencesService.getPreferencesForReviewer(1))
                .thenReturn(Arrays.asList(pref1, pref2));
        ResponseEntity<List<ReviewerPreferences>> response = sut
                .reviewFindAllPreferencesByUserIdGet(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(Arrays.asList(pref1, pref2));
    }

    @Test
    public void startBiddingForTrackBadRequestTest() {
        ResponseEntity<Void> response = sut.reviewStartBiddingForTrackGet(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewStartBiddingForTrackGet(-1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getBiddingDeadlineBadRequestTest() {
        Mockito.when(userService.validateUser(1)).thenReturn(true);
        Mockito.when(userService.validateUser(-1)).thenReturn(false);
        ResponseEntity<String> response = sut.reviewGetBiddingDeadlineGet(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewGetBiddingDeadlineGet(-1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewGetBiddingDeadlineGet(1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.reviewGetBiddingDeadlineGet(1, -1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void startBiddingForTrackNotFoundTest() {
        Mockito.when(trackPhaseService.getTrackPapers(2))
                .thenReturn(Optional.empty());
        ResponseEntity<Void> response = sut.reviewStartBiddingForTrackGet(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void startBiddingDeadlineOkTest() {
        Mockito.when(trackPhaseService.getTrackPapers(anyInt()))
                .thenReturn(Optional.of(Arrays.asList(1, 2, 3)));
        ResponseEntity<Void> response = sut.reviewStartBiddingForTrackGet(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        Mockito.verify(trackPhaseService).saveTrackPhase(any(TrackPhase.class));
    }

    @Test
    public void getBiddingDeadlineUnauthorizedTest() {
        ResponseEntity<String> response = sut.reviewGetBiddingDeadlineGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    public void getBiddingDeadlineNotFoundTest() {
        Mockito.when(reviewService.getTrackDeadline(2))
                .thenReturn(Optional.empty());
        ResponseEntity<String> response = sut.reviewGetBiddingDeadlineGet(2, 1);
        assertThat(response.getStatusCode()).isEqualTo((HttpStatus.NOT_FOUND));
    }

    @Test
    public void getBiddingDeadlineOkTest() {
        Optional<String> opt = Optional.of("2024-12-12");
        Mockito.when(reviewService.getTrackDeadline(anyInt()))
                .thenReturn(opt);
        ResponseEntity<String> response = sut.reviewGetBiddingDeadlineGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    public void assignAutomaticallyBadRequestTest() {
        ResponseEntity<Void> response = sut.assignAutomatically(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = sut.assignAutomatically(1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void assignAutomaticallyUnauthorizedTest() {
        Mockito.when(reviewService.verifyPcChair(1,1)).thenReturn(false);
        ResponseEntity<Void> response = sut.assignAutomatically(1,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void assignAutomaticallyNotFoundTest() {
        Mockito.when(trackPhaseService.getTrackPapers(1)).thenReturn(Optional.empty());
        Mockito.when(reviewService.verifyPcChair(1,1)).thenReturn(true);
        ResponseEntity<Void> response = sut.assignAutomatically(1,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void assignAutomaticallyOkTest() {
        Paper p1 = buildPaper(1, Arrays.asList(1,2,3),
                Paper.FinalVerdictEnum.ACCEPTED);
        Paper p2 = buildPaper(2, Arrays.asList(11,2,3),
                Paper.FinalVerdictEnum.ACCEPTED);
        Mockito.when(trackPhaseService.getTrackPapers(1))
                .thenReturn(Optional.of(Arrays.asList(1,2)));
        Mockito.when(paperService.findAllPapersForIdList(Arrays.asList(1,2)))
                        .thenReturn(Arrays.asList(p1,p2));
        Mockito.when(reviewService.verifyPcChair(1,1)).thenReturn(true);
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(1, Arrays.asList(1,11));
        map.put(2, Arrays.asList(2,3));
        map.put(3, Collections.emptyList());
        map.put(4, Collections.singletonList(1));
        Mockito.when(paperService.getConflictsPerReviewers(Arrays.asList(1,2)))
                .thenReturn(map);
        ResponseEntity<Void> response = sut.assignAutomatically(1,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        Mockito.verify(reviewService).saveReviews(any());
    }


}
