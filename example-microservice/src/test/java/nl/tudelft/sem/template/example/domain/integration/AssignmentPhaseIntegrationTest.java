package nl.tudelft.sem.template.example.domain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.repositories.*;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AssignmentPhaseIntegrationTest {
    @Autowired
    private TrackPhaseRepository trackPhaseRepository;
    @Autowired
    private ReviewerPreferencesRepository reviewerPreferencesRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private PcChairRepository pcChairRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private Paper buildPaper(int id, List<Integer> authors) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setAuthors(authors);
        paper.setFinalVerdict(Paper.FinalVerdictEnum.ACCEPTED);
        return paper;
    }

    private PreferenceEntity buildPref(int id, int paperId, int reviewerId,
                                       ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum) {
        PreferenceEntity pref = new PreferenceEntity();
        pref.setId(id);
        pref.setPaperId(paperId);
        pref.setReviewerId(reviewerId);
        pref.setPreferenceEnum(preferenceEnum);
        return pref;
    }

    private Review buildReview(int id, int paperId, int reviewerId) {
        Review review = new Review();
        review.setId(id);
        review.setPaperId(paperId);
        review.setReviewerId(reviewerId);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        review.setOverallScore(Review.OverallScoreEnum.NUMBER_1);
        return review;
    }

    private PcChair buildPCChair(int id, List<Integer> tracks) {
        PcChair res = new PcChair(tracks);
        res.setId(id);
        return res;
    }

    private void addPrefs() {
        reviewerPreferencesRepository.save(buildPref(1, 1, 1,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW));
        reviewerPreferencesRepository.save(buildPref(2, 1, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW));
        reviewerPreferencesRepository.save(buildPref(3, 1, 3,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW));
        reviewerPreferencesRepository.save(buildPref(4, 1, 4,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL));
        reviewerPreferencesRepository.save(buildPref(5, 2, 1,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW));
        reviewerPreferencesRepository.save(buildPref(6, 2, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL));
        reviewerPreferencesRepository.save(buildPref(7, 2, 3,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL));
        reviewerPreferencesRepository.save(buildPref(8, 2, 4,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW));
        reviewerPreferencesRepository.save(buildPref(9, 3, 1,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW));
        reviewerPreferencesRepository.save(buildPref(10, 3, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW));
        reviewerPreferencesRepository.save(buildPref(11, 3, 3,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW));
        reviewerPreferencesRepository.save(buildPref(12, 3, 4,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL));
    }

    @Test
    public void manualAssignmentTest() throws Exception {
        //Create the PC chair
        PcChair chair = buildPCChair(1, Arrays.asList(1,2));
        pcChairRepository.save(chair);
        // The PC chair starts the bidding phase for a track
        given(restTemplate.getForObject("localhost:8081/tracks/1/submissions", TrackPhaseService.IntegerList.class))
                .willReturn(new TrackPhaseService.IntegerList(Arrays.asList(1,2,3)));
        ResultActions startBidding = mockMvc.perform(get("/review/startBiddingForTrack")
                .contentType(MediaType.APPLICATION_JSON)
                .param("trackID", Integer.toString(1)));
        startBidding.andExpect(status().isAccepted());
        assertThat(trackPhaseRepository.existsById(1)).isTrue();

        // The PC chair gets the bidding deadline
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        given(restTemplate.exchange("localhost:8082/1/deadline", HttpMethod.GET, entity, String.class))
                .willReturn(ResponseEntity.of(Optional.of("2024-10-10")));
        ResultActions biddingDeadline = mockMvc.perform(get("/review/getBiddingDeadline")
                .contentType(MediaType.APPLICATION_JSON)
                .param("trackID", Integer.toString(1))
                .param("userID", Integer.toString(1)));
        biddingDeadline.andExpect(status().isAccepted());
        biddingDeadline.andExpect(jsonPath("$").value("2024-10-17"));

        // Until the deadline, the reviewers post their preferences
        addPrefs();
        paperRepository.save(buildPaper(1,Arrays.asList(1,2)));
        paperRepository.save(buildPaper(2,Arrays.asList(1,2)));
        paperRepository.save(buildPaper(3,Arrays.asList(1,2)));
        ResultActions prefs1 = mockMvc.perform(get("/paper/getPreferencesByPaper")
                .contentType(MediaType.APPLICATION_JSON)
                .param("paperID", Integer.toString(1))
                .param("userID", Integer.toString(1)));
        prefs1.andExpect(status().isAccepted());
        //prefs1.andExpect(jsonPath("$").value("a"));
        prefs1.andExpect(jsonPath("$[0].reviewer_preference")
                .value("Can review"));
        prefs1.andExpect(jsonPath("$[1].reviewer_preference")
                .value("Can review"));
        prefs1.andExpect(jsonPath("$[2].reviewer_preference")
                .value("Cannot review"));
        prefs1.andExpect(jsonPath("$[3].reviewer_preference")
                .value("Neutral"));
        ResultActions prefs2 = mockMvc.perform(get("/paper/getPreferencesByPaper")
                .contentType(MediaType.APPLICATION_JSON)
                .param("paperID", Integer.toString(2))
                .param("userID", Integer.toString(1)));
        prefs2.andExpect(status().isAccepted());
        prefs2.andExpect(jsonPath("$[0].reviewer_preference")
                .value("Can review"));
        prefs2.andExpect(jsonPath("$[1].reviewer_preference")
                .value("Neutral"));
        prefs2.andExpect(jsonPath("$[2].reviewer_preference")
                .value("Neutral"));
        prefs2.andExpect(jsonPath("$[3].reviewer_preference")
                .value("Cannot review"));
        ResultActions prefs3 = mockMvc.perform(get("/paper/getPreferencesByPaper")
                .contentType(MediaType.APPLICATION_JSON)
                .param("paperID", Integer.toString(3))
                .param("userID", Integer.toString(1)));
        prefs3.andExpect(status().isAccepted());
        prefs3.andExpect(jsonPath("$[0].reviewer_preference")
                .value("Cannot review"));
        prefs3.andExpect(jsonPath("$[1].reviewer_preference")
                .value("Can review"));
        prefs3.andExpect(jsonPath("$[2].reviewer_preference")
                .value("Cannot review"));
        prefs3.andExpect(jsonPath("$[3].reviewer_preference")
                .value("Neutral"));

        // The PC Chair assigns reviews manually
        List<Review> reviews = new ArrayList<>();
        reviews.add(buildReview(1,1,1));
        reviews.add(buildReview(2,1,2));
        reviews.add(buildReview(3,1,4));
        reviews.add(buildReview(4,2,1));
        reviews.add(buildReview(5,2,2));
        reviews.add(buildReview(6,2,3));
        reviews.add(buildReview(7,3,2));
        reviews.add(buildReview(8,3,3));
        reviews.add(buildReview(9,3,4));
        ResultActions assign = mockMvc.perform(post("/review/assignPapers")
                .contentType(MediaType.APPLICATION_JSON)
                .param("trackID", Integer.toString(1))
                .param("userId", Integer.toString(1))
                .content(new ObjectMapper().writeValueAsBytes(reviews)));
        assign.andExpect(status().isAccepted());
        for (int i = 1; i <= 9; i++)
            assertThat(reviewRepository.existsById(i)).isTrue();

        // The PC chair makes changes
        List<Review> changed = new ArrayList<>();
        changed.add(reviews.get(0));
        changed.add(reviews.get(8));
        changed.get(0).setReviewerId(3);
        changed.get(1).setReviewerId(1);
        ResultActions change = mockMvc.perform(put("/review/changeAssignments")
                .contentType(MediaType.APPLICATION_JSON)
                .param("trackID", Integer.toString(1))
                .param("userId", Integer.toString(1))
                .content(new ObjectMapper().writeValueAsBytes(changed)));
        change.andExpect(status().isAccepted());
    }
}
