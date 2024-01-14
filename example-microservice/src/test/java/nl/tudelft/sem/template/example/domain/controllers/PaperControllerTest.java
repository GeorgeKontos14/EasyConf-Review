package nl.tudelft.sem.template.example.domain.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;


public class PaperControllerTest {

    private RestTemplate restTemplate;
    private PaperService paperService;
    private UserService userService;
    private ReviewerPreferencesService reviewerPreferencesService;
    private ReviewService reviewService;
    private PaperController paperController;

    private Paper goodPaper;

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

    @BeforeEach
    void setup() {
        goodPaper = buildPaper(3, List.of(1, 2, 4, 5), null);
        paperService = Mockito.mock(PaperService.class);
        userService = Mockito.mock(UserService.class);
        reviewerPreferencesService = Mockito.mock(ReviewerPreferencesService.class);
        reviewService = Mockito.mock(ReviewService.class);
        paperController = new PaperController(userService, paperService, reviewerPreferencesService, reviewService);
        NullChecks nullChecks = new NullChecks();
    }

    @Test
    void paperGetPaperById_BadRequest_Test() {
        when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(Optional.of(goodPaper));
        when(userService.validateUser(any(Integer.class))).thenReturn(true);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(null, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(3, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(1, -1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(-1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);
    }

    @Test
    void paperGetPaperById_Unauthorized_Test() {
        Mockito.when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(Optional.of(goodPaper));
        Mockito.when(userService.validateUser(0)).thenReturn(false);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(0, 0);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_NotFound_Test() {
        when(userService.validateUser(any(Integer.class))).thenReturn(true);
        when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.empty());

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 8);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_Ok_Test() {
        when(userService.validateUser(any(Integer.class))).thenReturn(true);
        when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.of(goodPaper));

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0)).isEqualTo(goodPaper);
        assertThat(response.getBody().size()).isEqualTo(1);

    }

    @Test
    void paperGetPaperById_InternalServerError_Test() {

        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.of(goodPaper));
        RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(userService.validateUser(4)).thenThrow(e);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(null);
        Mockito.verify(e).printStackTrace();
    }

    @Test
    void paperGetTitleAndAbstractGet() {
        PaperResponse paperResponse = new PaperResponse("hello", List.of(1, 2, 3),
                4, "abstr", List.of("key1", "key2"), "link1", List.of(1, 2, 3), "link2");
        when(userService.validateUser(4)).thenReturn(true);
        when(paperService.getPaperObjectFromSubmissions(anyInt(), any(RestTemplate.class)))
                .thenReturn(Optional.of(paperResponse));

        ResponseEntity<String> response = paperController.paperGetTitleAndAbstractGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String responseObject = "{\"abstract\":\"abstr\",\"title\":\"hello\"}";

        assertThat(response.getBody()).isEqualTo(responseObject);
    }

    @Test
    void paperGetTitleAndAbstractError() {
        when(userService.validateUser(4)).thenReturn(true);
        when(paperService.getPaperObjectFromSubmissions(anyInt(), any(RestTemplate.class)))
                .thenReturn(Optional.empty());
        ResponseEntity<String> response = paperController.paperGetTitleAndAbstractGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getTitleAndAbstractBadRequestTest() {
        ResponseEntity<String> response = paperController
                .paperGetTitleAndAbstractGet(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getTitleAndAbstractUnauthorizedTest() {
        Mockito.when(userService.validateUser(2)).thenReturn(false);
        ResponseEntity<String> response = paperController
                .paperGetTitleAndAbstractGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getTitleAndAbstractExceptionTest() {
        RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(userService.validateUser(1)).thenReturn(true);
        Mockito.when(paperService.getPaperObjectFromSubmissions(anyInt(), any(RestTemplate.class)))
                .thenThrow(e);
        ResponseEntity<String> response = paperController.paperGetTitleAndAbstractGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.verify(e).printStackTrace();
    }


    @Test
    void paperGetPaperCommentsGetInvalidTest() {
        ResponseEntity<Comment> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(paperController.paperGetPaperCommentsGet(null, 3)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(3, null)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(-1, 1)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(1, -1)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(1, -1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        Mockito.when(userService.validateUser(2)).thenReturn(false);
        assertThat(paperController.paperGetPaperCommentsGet(3, 2).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getPaperCommentTest() {
        Paper p = new Paper();
        p.id(1);
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        Mockito.when(paperService.getPaperObjectWithId(2)).thenReturn(Optional.of(p));
        paperController.paperGetPaperByIDGet(2, 3);
        Mockito.when(paperService.getPaperObjectWithId(0)).thenReturn(Optional.empty());
        Mockito.when(userService.validateUser(0)).thenReturn(true);
        ResponseEntity<List<Comment>> response = paperController.paperGetPaperCommentsGet(0, 0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Comment c1 = new Comment();
        c1.id(1);
        Comment c2 = new Comment();
        c2.id(2);
        Mockito.when(paperService.paperGetPaperCommentsGet(3)).thenReturn(Arrays.asList(c1, c2));
        response = paperController.paperGetPaperCommentsGet(3, 0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Arrays.asList(c1, c2));
    }

    @Test
    public void getPreferencesByPaperBadRequestTest() {
        ResponseEntity<List<ReviewerPreferences>> response = paperController
                .paperGetPreferencesByPaperGet(null, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = paperController
                .paperGetPreferencesByPaperGet(-1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = paperController
                .paperGetPreferencesByPaperGet(1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        response = paperController
                .paperGetPreferencesByPaperGet(1, -1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getPreferencesByPaperUnauthorizedTest() {
        when(userService.validateUser(2)).thenReturn(false);
        ResponseEntity<List<ReviewerPreferences>> response = paperController
                .paperGetPreferencesByPaperGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getPreferencesByPaperTest() {
        when(userService.validateUser(1)).thenReturn(true);
        ReviewerPreferences pref1 = buildReviewPreferences(1, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        ReviewerPreferences pref2 = buildReviewPreferences(2, 4,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
        when(reviewerPreferencesService.getPreferencesForPaper(1))
                .thenReturn(Arrays.asList(pref1, pref2));
        ResponseEntity<List<ReviewerPreferences>> response = paperController
                .paperGetPreferencesByPaperGet(1, 1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(Arrays.asList(pref1, pref2));
    }

    @Test
    void paperUpdatePaperStatusPutTest() {
        Mockito.when(paperService.isExistingPaper(5)).thenReturn(true);
        Mockito.when(paperService.isExistingPaper(6)).thenReturn(true);
        Mockito.when(paperService.paperUpdatePaperStatusPut(5, null)).thenReturn(true);
        Mockito.when(paperService.paperUpdatePaperStatusPut(5, "Accepted"))
                .thenReturn(true);
        Mockito.when(paperService.paperUpdatePaperStatusPut(5, "Rejected"))
                .thenReturn(true);
        Mockito.when(paperService.paperUpdatePaperStatusPut(5, "Unresolved"))
                .thenReturn(true);
        Mockito.when(paperService.paperUpdatePaperStatusPut(6, "Rejected"))
                .thenReturn(false);
        assertThat(paperController.paperUpdatePaperStatusPut(5, "Unresolved", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        assertThat(paperController.paperUpdatePaperStatusPut(5, "Accepted", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        assertThat(paperController.paperUpdatePaperStatusPut(5, "Rejected", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        assertThat(paperController.paperUpdatePaperStatusPut(6, "Rejected", 1).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void paperUpdatePaperStatusPutTest2() {
        assertThat(paperController.paperUpdatePaperStatusPut(null, "", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(paperController.paperUpdatePaperStatusPut(1, "", null))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(paperController.paperUpdatePaperStatusPut(-1, "", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(paperController.paperUpdatePaperStatusPut(1, "", -1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(paperController.paperUpdatePaperStatusPut(1, null, 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        when(paperService.isExistingPaper(3)).thenReturn(false);
        assertThat(paperController.paperUpdatePaperStatusPut(3, "", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        when(paperService.isExistingPaper(5)).thenReturn(true);
        assertThat(paperController.paperUpdatePaperStatusPut(5, "Bad input", 1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @Test
    public void paperGetAllPapersForIdGetFailTest() {
        ResponseEntity<List<Paper>> badRequest = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(paperController.paperGetAllPapersForIDGet(null))
                .isEqualTo(badRequest);
        assertThat(paperController.paperGetAllPapersForIDGet(-1))
                .isEqualTo(badRequest);

        when(userService.validateUser(29)).thenReturn(false);
        assertThat(paperController.paperGetAllPapersForIDGet(29))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        when(userService.validateUser(anyInt())).thenReturn(true);
        when(reviewService.findAllPapersByReviewerId(2))
                        .thenReturn(List.of(5));
        when(paperService.findAllPapersForIdList(List.of(5)))
                .thenReturn(null);
        assertThat(paperController.paperGetAllPapersForIDGet(2))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        when(reviewService.findAllPapersByReviewerId(2))
                .thenReturn(List.of(3));
        when(paperService.findAllPapersForIdList(List.of(3)))
                .thenReturn(new ArrayList<>(0));
        assertThat(paperController.paperGetAllPapersForIDGet(3))
                .isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Test
    public void paperGetAllPapersForIDGetTest() {
        when(userService.validateUser(anyInt())).thenReturn(true);

        Paper p = new Paper();
        p.id(5);
        when(reviewService.findAllPapersByReviewerId(1))
                        .thenReturn(List.of(5));
        when(paperService.findAllPapersForIdList(List.of(5)))
                .thenReturn(List.of(p));
        assertThat(paperController.paperGetAllPapersForIDGet(1))
                .isEqualTo(new ResponseEntity<>(List.of(p), HttpStatus.OK));
    }

    @Test
    void paperGetPaperReviewsGetFailTest() {
        ResponseEntity<List<Review>> badRequest = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(paperController.paperGetPaperReviewsGet(1, null))
                .isEqualTo(badRequest);
        assertThat(paperController.paperGetPaperReviewsGet(null, 1))
                .isEqualTo(badRequest);
        assertThat(paperController.paperGetPaperReviewsGet(1, -1))
                .isEqualTo(badRequest);
        assertThat(paperController.paperGetPaperReviewsGet(-1, 1))
                .isEqualTo(badRequest);
        when(userService.validateUser(3)).thenReturn(false);
        assertThat(paperController.paperGetPaperReviewsGet(1, 3))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void paperGetPaperReviewsGetTest() {
        when(userService.validateUser(3)).thenReturn(true);
        when(reviewService.findAllReviewsByPaperId(1)).thenReturn(List.of(new Review()));
        assertThat(paperController.paperGetPaperReviewsGet(1, 3))
                .isEqualTo(new ResponseEntity<>(List.of(new Review()), HttpStatus.OK));

    }

    @Test
    void paperPostPreferenceScorePostBadRequest() {
        assertThat(paperController.paperPostPreferenceScorePost(null,2,"Neutral"))
                .isEqualTo(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Test
    void paperPostPreferenceScoreBadRequestPref() {
        assertThat(paperController.paperPostPreferenceScorePost(3, 2, "neutral"))
                .isEqualTo(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Test
    void paperPostPreferenceScoreNotFoundUser()
    {
        when(userService.validateUser(3)).thenReturn(false);
        assertThat(paperController.paperPostPreferenceScorePost(3,2,"NEUTRAL"))
                .isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Test
    void paperPostPreferenceScoreNotFoundPaper()
    {
        when(userService.validateUser(3)).thenReturn(true);
        when(paperService.isExistingPaper(2)).thenReturn(false);
        assertThat(paperController.paperPostPreferenceScorePost(3,2,"NEUTRAL"))
                .isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Test
    void paperPostPreferenceScoreOk()
    {
        when(userService.validateUser(3)).thenReturn(true);
        when(paperService.isExistingPaper(2)).thenReturn(true);
        PreferenceEntity good = new PreferenceEntity(2,3, ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL);
        when(reviewerPreferencesService.saveReviewerPreference(any())).thenReturn(good);

        assertThat(paperController.paperPostPreferenceScorePost(3,2,"NEUTRAL"))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(reviewerPreferencesService, times(1)).saveReviewerPreference(any());
        when(reviewerPreferencesService.saveReviewerPreference(any())).thenReturn(null);
        assertThat(paperController.paperPostPreferenceScorePost(3,2,"NEUTRAL").getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
