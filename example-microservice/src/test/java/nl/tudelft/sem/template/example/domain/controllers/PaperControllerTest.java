package nl.tudelft.sem.template.example.domain.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.controllers.PaperController;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewerPreferencesService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class PaperControllerTest {

    private RestTemplate restTemplate;
    private PaperService paperService;
    private UserService userService;
    private ReviewerPreferencesService reviewerPreferencesService;
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
     * @param reviewerId the id of the reviewer.
     * @param paperId the id of the paper.
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
        paperController = new PaperController(userService, paperService, reviewerPreferencesService);
    }

    @Test
    void paperGetPaperById_BadRequest_Test() {
        Mockito.when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(Optional.of(goodPaper));
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(null, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(3, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(3, -4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(-4, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);
    }

    @Test
    void paperGetPaperById_Unauthorized_Test() {
        Mockito.when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(Optional.of(goodPaper));
        Mockito.when(userService.validateUser(4)).thenReturn(false);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(5, 4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_NotFound_Test() {
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);
        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.empty());

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 8);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_Ok_Test() {
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);
        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.of(goodPaper));

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0)).isEqualTo(goodPaper);
        assertThat(response.getBody().size()).isEqualTo(1);

    }

    @Test
    void paperGetPaperById_InternalServerError_Test() {

        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(Optional.of(goodPaper));
        Mockito.when(userService.validateUser(4)).thenThrow(RuntimeException.class);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(null);
    }

    @Test
    void paperGetTitleAndAbstractGet() {
        PaperResponse paperResponse = new PaperResponse("hello", List.of(1, 2, 3),
                4, "abstr", List.of("key1", "key2"), "link1", List.of(1, 2, 3), "link2");
        Mockito.when(userService.validateUser(4)).thenReturn(true);
        Mockito.when(paperService.getPaperObjectFromSubmissions(anyInt(), any(RestTemplate.class)))
                .thenReturn(Optional.of(paperResponse));

        ResponseEntity<String> response = paperController.paperGetTitleAndAbstractGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String responseObject = "{\"abstract\":\"abstr\",\"title\":\"hello\"}";

        assertThat(response.getBody()).isEqualTo(responseObject);
    }

    @Test
    void paperGetTitleAndAbstractError() {
        Mockito.when(userService.validateUser(4)).thenReturn(true);
        Mockito.when(paperService.getPaperObjectFromSubmissions(anyInt(), any(RestTemplate.class)))
                .thenReturn(Optional.empty());
        ResponseEntity<String> response = paperController.paperGetTitleAndAbstractGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void paperGetPaperCommentsGetInvalidTest() {
        ResponseEntity<Comment> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(paperController.paperGetPaperCommentsGet(null, 1)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(1, null)).isEqualTo(response);
        assertThat(paperController.paperGetPaperCommentsGet(-1, 1)).isEqualTo(response);

        assertThat(paperController.paperGetPaperCommentsGet(1, -1))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void getPaperCommentTest() {
        Paper p = new Paper();
        p.id(1);
        Mockito.when(paperService.getPaperObjectWithId(1)).thenReturn(Optional.of(p));
        paperController.paperGetPaperByIDGet(1, 1);
    }

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
        Mockito.when(userService.validateUser(2)).thenReturn(false);
        ResponseEntity<List<ReviewerPreferences>> response = paperController
                .paperGetPreferencesByPaperGet(1, 2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getPreferencesByPaperTest() {
        Mockito.when(userService.validateUser(1)).thenReturn(true);
        ReviewerPreferences pref1 = buildReviewPreferences(1,2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        ReviewerPreferences pref2 = buildReviewPreferences(2,4,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
        Mockito.when(reviewerPreferencesService.getPreferencesForPaper(1))
                .thenReturn(Arrays.asList(pref1, pref2));
        ResponseEntity<List<ReviewerPreferences>> response = paperController
                .paperGetPreferencesByPaperGet(1,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo(Arrays.asList(pref1, pref2));
    }

    @Test
    public void paperGetAllPapersForIDGetFailTest() {
        ResponseEntity<List<Paper>> badRequest = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        assertThat(paperController.paperGetAllPapersForIDGet(null))
                .isEqualTo(badRequest);
        assertThat(paperController.paperGetAllPapersForIDGet(-1))
                .isEqualTo(badRequest);

        Mockito.when(userService.validateUser(29)).thenReturn(false);
        assertThat(paperController.paperGetAllPapersForIDGet(29))
                .isEqualTo(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        Mockito.when(userService.validateUser(anyInt())).thenReturn(true);
        Mockito.when(paperService.paperGetAllPapersForIDGet(2))
                .thenReturn(null);
        assertThat(paperController.paperGetAllPapersForIDGet(2))
                .isEqualTo(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        Mockito.when(paperService.paperGetAllPapersForIDGet(3))
                .thenReturn(new ArrayList<>(0));
        assertThat(paperController.paperGetAllPapersForIDGet(3))
                .isEqualTo(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Test
    public void paperGetAllPapersForIDGetTest() {
        Mockito.when(userService.validateUser(anyInt())).thenReturn(true);

        Paper p = new Paper();
        p.id(5);
        Mockito.when(paperService.paperGetAllPapersForIDGet(1))
                .thenReturn(List.of(p));
        assertThat(paperController.paperGetAllPapersForIDGet(1))
                .isEqualTo(new ResponseEntity<>(List.of(p), HttpStatus.OK));
    }
}
