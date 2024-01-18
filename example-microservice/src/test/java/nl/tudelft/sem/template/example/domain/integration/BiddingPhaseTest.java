package nl.tudelft.sem.template.example.domain.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.Access;
import javax.persistence.criteria.CriteriaBuilder;
import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.models.Reviewer;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.responses.SubmissionPaperIdsResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.PcChairReviewComment;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BiddingPhaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PcChairRepository pcChairRepository;

    @Autowired
    private ReviewerPreferencesRepository reviewerPreferencesRepository;

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private PaperService paperService;

    @MockBean
    private RestTemplate restTemplate;


    private PreferenceEntity buildPref(int id, int paperId, int reviewerId,
                                       ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum) {
        PreferenceEntity pref = new PreferenceEntity();
        pref.setId(id);
        pref.setPaperId(paperId);
        pref.setReviewerId(reviewerId);
        pref.setPreferenceEnum(preferenceEnum);
        return pref;
    }


    private Reviewer buildReviewer(int id, List<Integer> reviews, List<Integer> preferences)
    {
        Reviewer reviewer = new Reviewer();
        reviewer.setId(id);
        reviewer.setReviews(reviews);
        reviewer.setPreferences(preferences);
        return reviewer;
    }


    /**
     * A reviewer gets the bidding deadline for a track, reads the titles and abstracts for the papers of that track and posts their preferences
     */
    @Test
    void scenarioTest() throws Exception {

        //Create the Reviewer
        Reviewer reviewer = buildReviewer(1, List.of(), List.of());
        reviewerRepository.save(reviewer);

        // The reviewer gets the bidding deadline
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


        SubmissionPaperIdsResponse submissionPaperIdsResponse = new SubmissionPaperIdsResponse();
        submissionPaperIdsResponse.setSubmissionIds(List.of(1,2,3));
        given(restTemplate.exchange("localhost:8082/tracks/1/submissions", HttpMethod.GET, entity, SubmissionPaperIdsResponse.class))
            .willReturn(ResponseEntity.of(Optional.of(submissionPaperIdsResponse)));

        PaperResponse paperResponse = new PaperResponse("title1", List.of(), null, "abstract1", null, null, null, null);
        PaperResponse paperResponse2 = new PaperResponse("title2", List.of(), null, "abstract2", null, null, null, null);
        PaperResponse paperResponse3 = new PaperResponse("title3", List.of(), null, "abstract3", null, null, null, null);
        submissionPaperIdsResponse.setSubmissionIds(List.of(1,2,3));
        given(restTemplate.exchange("localhost:8082/submissions/1/info", HttpMethod.GET, entity, PaperResponse.class))
            .willReturn(ResponseEntity.of(Optional.of(paperResponse)));
        given(restTemplate.exchange("localhost:8082/submissions/2/info", HttpMethod.GET, entity, PaperResponse.class))
            .willReturn(ResponseEntity.of(Optional.of(paperResponse2)));
        given(restTemplate.exchange("localhost:8082/submissions/3/info", HttpMethod.GET, entity, PaperResponse.class))
            .willReturn(ResponseEntity.of(Optional.of(paperResponse3)));


        /// Functionality that retrieves all the Paper's for a Reviewer
        List<Integer> paperIds = paperService.getAllPaperIdsForTrack(1).get();
        List<Paper> titlesAndAbstracts = new ArrayList<>();

        /// Get title and abstract from all papers
        checkTitleAbstractRetrieval(1, 1);
        checkTitleAbstractRetrieval(2, 1);
        checkTitleAbstractRetrieval(3, 1);

        /// Reviewer posts their preference on all three papers paperPostPreferenceScorePost
        storeEntity(1, 1, 1, ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL);
        storeEntity(2, 1, 2, ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        storeEntity(3, 1, 3, ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
    }

    private void checkTitleAbstractRetrieval(int paperId, int userId) throws Exception {
        String title = "title"+Integer.toString(paperId);
        String _abstract = "abstract"+ Integer.toString(paperId);
        ResultActions response = mockMvc.perform(get("/paper/getTitleAndAbstract")
            .contentType(MediaType.APPLICATION_JSON)
            .param("paperID", Integer.toString(paperId))
            .param("userID", Integer.toString(userId)));
        response.andExpect(status().isOk());
        String expectedString = "{\"abstract\":\""+ _abstract + "\",\"title\":\""+title+"\"}";
        response.andExpect(content().string(expectedString));
    }

    void storeEntity(int id, int reviewerId, int paperId, ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum)
        throws Exception {
        PreferenceEntity preference = buildPref(id, paperId, reviewerId, preferenceEnum);
        ResultActions storedStatus = mockMvc.perform(post("/paper/postPreferenceScore")
            .contentType(MediaType.APPLICATION_JSON)
            .param("reviewer_id", Integer.toString(reviewerId))
            .param("paper_id", Integer.toString(paperId))
            .param("preference", PreferenceEntity.changeEnumValueToString(preferenceEnum)));
        storedStatus.andExpect(status().isOk());
        assertThat(reviewerPreferencesRepository.existsById(id)).isEqualTo(true);
    }
}
